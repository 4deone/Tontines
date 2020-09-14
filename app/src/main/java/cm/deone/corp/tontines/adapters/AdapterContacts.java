package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.util.Log;
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
import cm.deone.corp.tontines.models.User;

public class AdapterContacts extends RecyclerView.Adapter<AdapterContacts.MyHolder> {

    private Context context;
    private List<User> userList;

    public AdapterContacts(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
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
        final String phone = userList.get(position).getPhoneUser();

        contactNumberTontine(id, holder.mContactTontine);
        holder.mContactName.setText(nom);

       /* Log.e("User Database", "user phone : "+ userList.get(position).getPhoneUser());
        Log.e("User Database", "Numbre de user : "+ userList.size());*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Téléphone -> ("+phone+")", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void contactNumberTontine(final String id, final TextView mContactTontine) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tontines");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numTontineFondateur = 0;
                int numTontineMembre = 0;
                for (DataSnapshot ds: snapshot.getChildren()){
                    String fondateur = ds.child("Bureau").child("fondateur").getValue(String.class);
                    String membre = ds.child("Membres").child(id).getValue(String.class);
                    if (fondateur.equals(id)) {
                        numTontineFondateur++;
                    }
                    if (!membre.equals(null)) {
                        numTontineMembre++;
                    }
                }
                mContactTontine.setText("Fondteur de "+numTontineFondateur+" tontines - Membre de "+numTontineMembre+" tontines");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView mAvatar;
        TextView mContactName;
        TextView mContactTontine;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.imContact);
            mContactName = itemView.findViewById(R.id.tvContactName);
            mContactTontine = itemView.findViewById(R.id.tvContactNumberTontine);
        }
    }
}
