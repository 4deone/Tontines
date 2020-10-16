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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Chat;
import cm.deone.corp.tontines.models.GroupChat;

public class AdapterGroupChats extends RecyclerView.Adapter<AdapterGroupChats.MyHolder> {

    private static final int MSG_TYPE_IN = 0;
    private static final int MSG_TYPE_OUT = 1;
    private FirebaseUser firebaseUser;
    private Context context;
    private IntRvClickListner listener;
    private ArrayList<GroupChat> groupChats;

    public AdapterGroupChats(Context context, ArrayList<GroupChat> groupChats) {
        this.context = context;
        this.groupChats = groupChats;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_IN){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_in, parent, false);
            return new MyHolder(view);
        }else if (viewType == MSG_TYPE_OUT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_out, parent, false);
            return new MyHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterGroupChats.MyHolder holder, final int position) {
        GroupChat groupChat = groupChats.get(position);

        final String message = groupChat.getMessage();
        final String timestamp = groupChat.getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.mTimeTv.setText(dateTime);
        holder.mMessageTv.setText(message);
        setUserName(groupChat, holder);
    }

    private void setUserName(GroupChat groupChat, final MyHolder holder) {
        DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference("Users");
        refGroup.orderByChild("idUser").equalTo(groupChat.getSender()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String name = ds.child("nameUser").getValue(String.class);
                    holder.mNameTv.setText(name);
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
        return groupChats.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (groupChats.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_OUT;
        }else{
            return MSG_TYPE_IN;
        }
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView mNameTv;
        TextView mMessageTv;
        TextView mTimeTv;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mNameTv = itemView.findViewById(R.id.mNameTv);
            mMessageTv = itemView.findViewById(R.id.mMessageTv);
            mTimeTv = itemView.findViewById(R.id.mTimeTv);

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
