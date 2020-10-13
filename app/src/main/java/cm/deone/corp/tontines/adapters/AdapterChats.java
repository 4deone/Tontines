package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Chat;

public class AdapterChats extends RecyclerView.Adapter<AdapterChats.MyHolder> {

    private static final int MSG_TYPE_IN = 0;
    private static final int MSG_TYPE_OUT = 1;
    private FirebaseUser firebaseUser;
    private Context context;
    private IntRvClickListner listener;
    private List<Chat> chatList;
    private String imageUrl;

    public AdapterChats(Context context, List<Chat> chatList, String imageUrl) {
        this.context = context;
        this.chatList = chatList;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_IN){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_in, parent, false);
            return new MyHolder(view);
        }else if (viewType == MSG_TYPE_OUT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_out, parent, false);
            return new MyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterChats.MyHolder holder, final int position) {
        final String message = chatList.get(position).getMessage();
        final String timestamp = chatList.get(position).getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        if (chatList.get(position).getMessage().equals("Ce message a été supprimé...")){
            holder.mTimestamp.setVisibility(View.GONE);
            holder.mDelivery.setVisibility(View.GONE);
            holder.mMessage.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_arrondi));
            //mettre le text au centre
            holder.mMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //mettre en italic
            holder.mMessage.setTypeface(holder.mMessage.getTypeface(), Typeface.BOLD_ITALIC);
            //définir la taille du text
            holder.mMessage.setTextSize(TypedValue.COMPLEX_UNIT_IN,0.1f);
        }
        holder.mMessage.setText(message);
        holder.mTimestamp.setText(dateTime);
        try {
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_action_cover).into(holder.mAvatar);
        }catch(Exception e){
            Picasso.get().load(R.drawable.ic_action_cover).into(holder.mAvatar);
        }
        if (position == chatList.size()-1){
            if (chatList.get(position).isSeen()){
                holder.mDelivery.setText("Seen");
            }else{
                holder.mDelivery.setText("Delivered");
            }
        }else{
            holder.mDelivery.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_OUT;
        }else{
            return MSG_TYPE_IN;
        }
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView mAvatar;
        TextView mMessage;
        TextView mTimestamp;
        TextView mDelivery;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mMessage = itemView.findViewById(R.id.hisMessageTv);
            mAvatar = itemView.findViewById(R.id.avatarInTv);
            mTimestamp = itemView.findViewById(R.id.timeTv);
            mDelivery = itemView.findViewById(R.id.isSeenTv);

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
