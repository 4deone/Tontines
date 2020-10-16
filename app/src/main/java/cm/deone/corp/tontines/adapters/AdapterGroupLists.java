package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.GroupChatList;

public class AdapterGroupLists extends RecyclerView.Adapter<AdapterGroupLists.MyHolder> {

    private FirebaseUser firebaseUser;
    private Context context;
    private IntRvClickListner listener;
    private ArrayList<GroupChatList> groupChatLists;

    public AdapterGroupLists(Context context, ArrayList<GroupChatList> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grouplist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterGroupLists.MyHolder holder, final int position) {
        GroupChatList groupChatList = groupChatLists.get(position);

        String groupId = groupChatList.getGroupId();
        String groupIcon = groupChatList.getGroupIcon();
        String groupTitre = groupChatList.getGroupTitle();

        holder.clSenderTv.setText("");
        holder.clMessageTv.setText("");
        holder.clOnlineTv.setText("");

        loadLastMessage(groupChatList, holder);

        holder.clGroupTitleTv.setText(groupTitre);
        try {
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_action_members).into(holder.avatarIv);
        }catch(Exception e){
            holder.avatarIv.setImageResource(R.drawable.ic_action_members);
        }
    }

    private void loadLastMessage(GroupChatList groupChatList, final MyHolder holder) {
        DatabaseReference refLastMessage = FirebaseDatabase.getInstance().getReference("Groupes");
        refLastMessage.child(groupChatList.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds:snapshot.getChildren()){
                            String message = ds.child("message").getValue(String.class);
                            String timestamp = ds.child("timestamp").getValue(String.class);
                            String sender = ds.child("sender").getValue(String.class);

                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                            holder.clMessageTv.setText(message);
                            holder.clOnlineTv.setText(dateTime);
                            DatabaseReference refSender = FirebaseDatabase.getInstance().getReference("Users");
                            refSender.orderByChild("idUser").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()){
                                                String nameUser = ds.child("nameUser").getValue(String.class);
                                                holder.clSenderTv.setText(nameUser);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
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
        return groupChatLists.size();
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView avatarIv;
        TextView clGroupTitleTv;
        TextView clSenderTv;
        TextView clMessageTv;
        TextView clOnlineTv;
        TextView clNumberMessageNonLusTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.clAvatarIv);
            clGroupTitleTv = itemView.findViewById(R.id.clGroupTitleTv);
            clSenderTv = itemView.findViewById(R.id.clSenderTv);
            clMessageTv = itemView.findViewById(R.id.clMessageTv);
            clOnlineTv = itemView.findViewById(R.id.clOnlineTv);
            clNumberMessageNonLusTv = itemView.findViewById(R.id.clNumberMessageNonLusTv);

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
