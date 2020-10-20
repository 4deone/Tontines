package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import com.squareup.picasso.Picasso;

import java.util.List;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Membre;
import cm.deone.corp.tontines.models.User;

import static cm.deone.corp.tontines.outils.MesOutils.findContact;

public class AdapterParticipants extends RecyclerView.Adapter<AdapterParticipants.MyHolder> {

    private Context context;
    private IntRvClickListner listener;
    private List<User> userList;
    private String groupeId;

    public AdapterParticipants(Context context, List<User> userList, String groupeId) {
        this.context = context;
        this.userList = userList;
        this.groupeId = groupeId;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_participant, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterParticipants.MyHolder holder, final int position) {
        User user = userList.get(position);

        String uImage = user.getAvatarUser();
        String uName = user.getNameUser();

        holder.partUsernameTv.setText(uName);
        try {
            Picasso.get().load(uImage).placeholder(R.drawable.ic_action_members).into(holder.partAvatarIv);
        }catch(Exception e){
            holder.partAvatarIv.setImageResource(R.drawable.ic_action_members);
        }
        checkIfAlreadyExist(user, holder);
    }

    private void checkIfAlreadyExist(User user, final MyHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groupes");
        reference.child(groupeId).child("Participants").child(user.getIdUser())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String hisRole = snapshot.child("role").getValue(String.class);
                            holder.partStatusTv.setText(hisRole);
                        }else{
                            holder.partStatusTv.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView partAvatarIv;
        TextView partUsernameTv;
        TextView partDeviseTv;
        TextView partStatusTv;
        TextView partNonLusTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            partAvatarIv = itemView.findViewById(R.id.partAvatarIv);
            partUsernameTv = itemView.findViewById(R.id.partUsernameTv);
            partDeviseTv = itemView.findViewById(R.id.partDeviseTv);
            partStatusTv = itemView.findViewById(R.id.partStatusTv);
            partNonLusTv = itemView.findViewById(R.id.partNonLusTv);

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
