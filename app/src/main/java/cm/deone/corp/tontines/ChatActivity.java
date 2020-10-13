package cm.deone.corp.tontines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cm.deone.corp.tontines.adapters.AdapterChats;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Chat;
import cm.deone.corp.tontines.models.User;
import cm.deone.corp.tontines.notifications.APIService;
import cm.deone.corp.tontines.notifications.Client;
import cm.deone.corp.tontines.notifications.Data;
import cm.deone.corp.tontines.notifications.MyResponse;
import cm.deone.corp.tontines.notifications.Sender;
import cm.deone.corp.tontines.notifications.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {


    APIService apiService;
    boolean notify = false;

    private Toolbar mChatToolbar;
    private ImageView mUserAvatarIv;
    private TextView mUserNameTv;
    private TextView mUserStatusTv;
    private RecyclerView mChatRv;
    private EditText mMessageEt;
    private ImageButton mSendIb;

    private ValueEventListener seenListener;
    private DatabaseReference userRefForSeen;

    private List<Chat> chatList;
    private AdapterChats adapterChats;

    private String myUID;
    private String hisUID;
    private String hisImageUrl;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference refUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        checkUserStatus();
        initViews();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnline("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnline(timestamp);
        checkTyping("noOne");
        userRefForSeen.removeEventListener(seenListener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkOnline("online");
        super.onResume();
    }

    private void checkUserStatus(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
        }else {
            startActivity(new Intent(ChatActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initViews() {
        hisUID = getIntent().getStringExtra("hisUID");
        mChatToolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(mChatToolbar);
        mChatToolbar.setTitle("");
        mUserAvatarIv = findViewById(R.id.userAvatarIv);
        mUserNameTv = findViewById(R.id.userNameTv);
        mUserStatusTv = findViewById(R.id.userStatusTv);
        mChatRv = findViewById(R.id.chatRv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mChatRv.setHasFixedSize(true);
        mChatRv.setLayoutManager(linearLayoutManager);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        mMessageEt = findViewById(R.id.messageEt);
        mSendIb = findViewById(R.id.sendIb);
        firebaseDatabase = FirebaseDatabase.getInstance();
        refUser = firebaseDatabase.getReference("Users");

        Query userQuery = refUser.orderByChild("idUser").equalTo(hisUID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String name = ds.child("nameUser").getValue(String.class);
                    String online = ds.child("onlineUser").getValue(String.class);
                    String typing = ds.child("typingTo").getValue(String.class);
                    hisImageUrl = ds.child("avatarUser").getValue(String.class);
                    assert typing != null;
                    if(typing.equals(myUID)){
                        mUserStatusTv.setText("Entrain d'écrire...");
                    }else{
                        assert online != null;
                        if(online.equals("online")){
                            mUserStatusTv.setText(online);
                        }else{
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(online));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            mUserStatusTv.setText(getResources().getString(R.string.last_seen, dateTime));
                        }
                    }
                    mUserNameTv.setText(name);
                    try {
                        Picasso.get().load(hisImageUrl).placeholder(R.drawable.ic_action_cover).into(mUserAvatarIv);
                    }catch(Exception e){
                        Picasso.get().load(R.drawable.ic_action_cover).into(mUserAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mSendIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String message = mMessageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Le message ne doit pas etre vide", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }

                mMessageEt.setText("");
            }
        });

        mMessageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0){
                    checkTyping("noOne");
                }else{
                    checkTyping(hisUID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessages();

        seenMessage();
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(myUID) && chat.getSender().equals(hisUID)){
                        HashMap<String, Object> hashSeenMessage = new HashMap<>();
                        hashSeenMessage.put("isSeen", true);
                        ds.getRef().updateChildren(hashSeenMessage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readMessages() {
        chatList = new ArrayList<>();
        DatabaseReference refChatList = FirebaseDatabase.getInstance().getReference("Chats");
        refChatList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    assert chat != null;
                    if (chat.getReceiver().equals(myUID) && chat.getSender().equals(hisUID) || chat.getReceiver().equals(hisUID) && chat.getSender().equals(myUID)){
                        chatList.add(chat);
                    }
                    adapterChats = new AdapterChats(ChatActivity.this, chatList, hisImageUrl);
                    adapterChats.notifyDataSetChanged();
                    mChatRv.setAdapter(adapterChats);
                    adapterChats.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            if (!chatList.get(position).getMessage().equals("Ce message a été supprimé...")){
                                showMessageDialog(chatList.get(position), view);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessageDialog(final Chat chat, final View view) {
        String[] options = {"Repondre", "Effacer" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    //repondreMessage();
                }else if (which == 1){
                    effacerMessage(chat, view);
                }
            }
        });
        builder.create().show();
    }

    private void sendMessage(final String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference refMessage = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMessage = new HashMap<>();
        hashMessage.put("sender", myUID);
        hashMessage.put("receiver", hisUID);
        hashMessage.put("message", message);
        hashMessage.put("timestamp", timestamp);
        hashMessage.put("isSeen", false);
        refMessage.child("Chats").push().setValue(hashMessage);

        DatabaseReference refNotif = FirebaseDatabase.getInstance().getReference("Users").child(myUID);
        refNotif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notify){
                    sendNotifications(hisUID, user.getNameUser(), message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotifications(final String hisUID, final String nameUser, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Query queryRef = ref.orderByKey().equalTo(hisUID);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUID, nameUser+":"+message, "New message", hisUID, R.drawable.ic_notif);
                    Sender sender = new Sender(data, token.getToken());
                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success != 1) {
                                    Toast.makeText(ChatActivity.this, "Failed ", Toast.LENGTH_LONG);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkOnline(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("onlineUser", status);;
        dbRef.updateChildren(hashMap);
    }

    private void checkTyping(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(myUID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);;
        dbRef.updateChildren(hashMap);
    }

    private void effacerMessage(final Chat chat, final View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Effacer");
        builder.setMessage("Voulez vous supprimer ce message...");
        builder.setPositiveButton("Effacer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makeDelete(chat, view);
            }
        });
        builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void makeDelete(Chat chat, final View view) {
        String msgTimestamp = chat.getTimestamp();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Chats");
        Query queryDelete = dbRef.orderByChild("timestamp").equalTo(msgTimestamp);
        queryDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    if (ds.child("sender").getValue(String.class).equals(myUID)){
                        //ds.getRef().removeValue();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "Ce message a été supprimé...");
                        ds.getRef().updateChildren(hashMap);
                        Toast.makeText(ChatActivity.this, "Message supprimé...", Toast.LENGTH_SHORT).show();
                        view.findViewById(R.id.timeTv).setVisibility(View.GONE);
                        view.findViewById(R.id.isSeenTv).setVisibility(View.GONE);
                        TextView textView = view.findViewById(R.id.hisMessageTv);
                        // Create corner background
                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_arrondi));
                        //mettre le text au centre
                        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        //mettre en italic
                        textView.setTypeface(textView.getTypeface(), Typeface.BOLD_ITALIC);
                        //définir la taille du text
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_IN,0.1f);
                    }else{
                        Toast.makeText(ChatActivity.this, "Impossible de supprimer ce message...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_edit_contact){
            //Afficher le contact
        }
        return super.onOptionsItemSelected(item);
    }
}