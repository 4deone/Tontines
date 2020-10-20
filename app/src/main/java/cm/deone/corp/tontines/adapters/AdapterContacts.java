package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.User;

public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.MyHolder> {

    private static final  String TAG_REQUEST_PARTICIPANTS = "PARTICIPANTS";
    private Context context;
    private IntRvClickListner listener;
    private List<User> userList;
    private String tag_request;
    private String groupId;

    public AdapterContacts(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        tag_request = "";
        groupId = "";
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterContacts.MyHolder holder, final int position) {
        final String nom = userList.get(position).getNameUser();
        final String id = userList.get(position).getIdUser();
        holder.mContactName.setText(nom);
        contactNumberTontine(id, holder);
        if (tag_request.equals(TAG_REQUEST_PARTICIPANTS)){
            isMemberGroup(id, holder);
        }
    }

    private void isMemberGroup(String id, final MyHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groupes");
        ref.child(groupId).child("Participants").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.mSelected.setVisibility(snapshot.exists() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTagRequest(String tag, String gUID){
        tag_request = tag;
        groupId = gUID;
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private void contactNumberTontine(final String id, final MyHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tontines").child("Membres").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numTontineFondateur = 0;
                int numTontineMembre = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    String fondateur = ds.child("bureau").getValue(String.class);
                    assert fondateur != null;
                    if (!fondateur.equals("fondateur")) {
                        numTontineFondateur++;
                    }
                    if (ds.exists()) {
                        numTontineMembre++;
                    }
                }
                holder.mContactTontine.setText("Fondateur de "+numTontineFondateur+" tontines - Membre de "+numTontineMembre+" tontines");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView mAvatar;
        ImageView mSelected;
        TextView mContactName;
        TextView mContactTontine;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mSelected = itemView.findViewById(R.id.selectedIv);
            mAvatar = itemView.findViewById(R.id.imContact);
            mContactName = itemView.findViewById(R.id.tvContactName);
            mContactTontine = itemView.findViewById(R.id.tvContactNumberTontine);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(v, position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onLongItemClick(v, position);
            }
            return true;
        }
    }
}
