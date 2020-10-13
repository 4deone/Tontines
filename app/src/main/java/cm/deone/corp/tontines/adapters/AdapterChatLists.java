package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import cm.deone.corp.tontines.ChatActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.User;

public class AdapterChatLists extends RecyclerView.Adapter<AdapterChatLists.MyHolder> {

    private FirebaseUser firebaseUser;
    private Context context;
    private IntRvClickListner listener;
    private List<User> userList;
    private HashMap<String, String> lastmessageMap;

    public AdapterChatLists(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
        lastmessageMap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterChatLists.MyHolder holder, final int position) {
        String hisUID = userList.get(position).getIdUser();
        String hisAvatar = userList.get(position).getAvatarUser();
        String hisName = userList.get(position).getNameUser();
        String lastmessage = lastmessageMap.get(hisUID);

        holder.nameTv.setText(hisName);
        /*if (lastmessage!=null || lastmessage.equals("default")){
            holder.lastmessageTv.setVisibility(View.GONE);
        }else{
            holder.lastmessageTv.setText(lastmessage);
            holder.lastmessageTv.setVisibility(View.VISIBLE);
        }*/
        try {
            Picasso.get().load(hisAvatar).placeholder(R.drawable.ic_action_cover).into(holder.avatarIv);
        }catch(Exception e){
            Picasso.get().load(R.drawable.ic_action_cover).into(holder.avatarIv);
        }

        if (userList.get(position).getOnlineUser().equals("online")){
            // online
            holder.lastseenTv.setVisibility(View.GONE);
            /*if (userList.get(position).getOnlineUser().equals("online")){
                holder.onlineTv.setVisibility(View.GONE);
            }*/
        }else{
            // offline
            holder.lastseenTv.setVisibility(View.VISIBLE);
            holder.onlineTv.setVisibility(View.GONE);
            /*Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timestamp));
            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();*/
        }

    }

    public void setLastMessageMap(String userID, String lastmessage){
        lastmessageMap.put(userID, lastmessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView avatarIv;
        TextView nameTv;
        TextView lastmessageTv;
        TextView onlineTv;
        TextView lastseenTv;
        TextView nonluTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv = itemView.findViewById(R.id.clAvatarIv);
            nameTv = itemView.findViewById(R.id.clUsernameTv);
            lastmessageTv = itemView.findViewById(R.id.clLastMessageTv);
            onlineTv = itemView.findViewById(R.id.clOnlineTv);
            lastseenTv = itemView.findViewById(R.id.clLastSeenTv);
            nonluTv = itemView.findViewById(R.id.clNumberMessageNonLusTv);

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
