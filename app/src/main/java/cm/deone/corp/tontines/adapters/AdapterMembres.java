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
import cm.deone.corp.tontines.models.Membre;
import cm.deone.corp.tontines.models.User;

public class AdapterMembres extends RecyclerView.Adapter<AdapterMembres.MyHolder> {

    private Context context;
    private List<Membre> membreList;

    public AdapterMembres(Context context, List<Membre> membreList) {
        this.context = context;
        this.membreList = membreList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMembres.MyHolder holder, final int position) {
        final String nom = membreList.get(position).getName();
        final String bureau = membreList.get(position).getBureau();
        holder.mContactName.setText(nom);
        holder.mContactTontine.setText(bureau);


       /* Log.e("User Database", "user phone : "+ userList.get(position).getPhoneUser());
        Log.e("User Database", "Numbre de user : "+ userList.size());*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Téléphone -> ("+phone+")", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return membreList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

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
