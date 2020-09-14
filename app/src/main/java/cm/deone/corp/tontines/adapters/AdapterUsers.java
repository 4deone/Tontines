package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.models.User;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    private Context context;
    private List<User> userList;

    public AdapterUsers(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterUsers.MyHolder holder, final int position) {

        final String nom = userList.get(position).getNameUser();
        final String phone = userList.get(position).getPhoneUser();

        holder.mNomUser.setText(phone);
        holder.mPhoneUser.setText(nom);

        if (position == 1){
            holder.mHeader.setVisibility(View.VISIBLE);
        }else {
            holder.mHeader.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+nom+"("+phone+")", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        TextView mHeader;
        TextView mNomUser;
        TextView mPhoneUser;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mHeader = itemView.findViewById(R.id.headerContact);
            mNomUser = itemView.findViewById(R.id.nomUser);
            mPhoneUser = itemView.findViewById(R.id.phoneUser);
        }
    }
}
