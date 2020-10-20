package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.deone.corp.tontines.adapters.AdapterGroupChats;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.GroupChat;
import cm.deone.corp.tontines.notifications.Data;
import cm.deone.corp.tontines.notifications.Sender;
import cm.deone.corp.tontines.notifications.Token;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference reference;
    private DatabaseReference refUsers;
    private RequestQueue requestQueue;

    private String gUID;
    private String gROLE;
    private String myUID;
    private String myName;

    private ImageView mGroupIcon;
    private TextView mGroupTitle;
    //private TextView mGroupStatus;
    private ImageButton mGroupJoinFile;
    private ImageButton mGroupSend;
    private EditText mGroupMessage;

    private RecyclerView mGroupRv;
    private AdapterGroupChats adapterGroupChats;
    private ArrayList<GroupChat> groupChats;
    private ArrayList<String> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        checkUserStatus();
        initViews();
        reference.child(gUID).child("Participants").addValueEventListener(valMemberList);
        reference.orderByChild("groupId").equalTo(gUID).addValueEventListener(valLoadGroupInfo);
        reference.child(gUID).child("Messages").addValueEventListener(valLoadMessages);
        mGroupJoinFile.setOnClickListener(joinFileListener);
        mGroupSend.setOnClickListener(sendListener);
    }

    @Override
    protected void onStop() {
        reference.removeEventListener(valMemberList);
        refUsers.removeEventListener(valUserName);
        super.onStop();
    }

    private View.OnClickListener joinFileListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = mGroupMessage.getText().toString().trim();
            if(TextUtils.isEmpty(message)){
                Toast.makeText(GroupChatActivity.this, "Le champs message est vide...", Toast.LENGTH_SHORT).show();
            }else{
                sendMessage(message);
            }
        }
    };

    private ValueEventListener valLoadMessages = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            groupChats.clear();
            for (DataSnapshot ds:snapshot.getChildren()){
                GroupChat groupChat = ds.getValue(GroupChat.class);
                groupChats.add(groupChat);
                adapterGroupChats = new AdapterGroupChats(GroupChatActivity.this, groupChats);
                adapterGroupChats.notifyDataSetChanged();
                mGroupRv.setAdapter(adapterGroupChats);
                adapterGroupChats.setOnItemClickListener(new IntRvClickListner() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(GroupChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private ValueEventListener valLoadGroupInfo = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds:snapshot.getChildren()){
                String gTitle = ds.child("groupTitle").getValue(String.class);
                String gDescription = ds.child("groupDescription").getValue(String.class);
                String gIcon= ds.child("groupIcon").getValue(String.class);
                String timestamp= ds.child("timestamp").getValue(String.class);
                String createBy= ds.child("creatBy").getValue(String.class);

                mGroupTitle.setText(gTitle);
                try {
                    Picasso.get().load(gIcon).placeholder(R.drawable.ic_action_cover).into(mGroupIcon);
                }catch(Exception e){
                    mGroupIcon.setImageResource(R.drawable.ic_action_cover);
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(GroupChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private ValueEventListener valMemberList = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            memberList.clear();
            for (DataSnapshot ds:snapshot.getChildren()){
                String uid = ds.child("uid").getValue(String.class);
                if (!uid.equals(myUID)){
                    memberList.add(uid);
                }else{
                    gROLE = ds.child("role").getValue(String.class);
                    // refresh menu items
                    invalidateOptionsMenu();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(GroupChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private ValueEventListener valUserName = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot ds:snapshot.getChildren()){
                myName = ds.child("nameUser").getValue(String.class);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(GroupChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void sendMessage(final String message) {
        refUsers.orderByChild("idUser").equalTo(myUID).addValueEventListener(valUserName);
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMapGroupMessage = new HashMap<>();
        hashMapGroupMessage.put("sender", ""+myUID);
        hashMapGroupMessage.put("message", ""+message);
        hashMapGroupMessage.put("timestamp", ""+timestamp);
        hashMapGroupMessage.put("type", "text");

        reference.child(gUID).child("Messages").child(timestamp)
                .setValue(hashMapGroupMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (String item:memberList){
                            sendNotifications(item, myName, message);
                        }
                        mGroupMessage.setText(null);
                        mGroupMessage.setHint("Votre message");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        gUID = getIntent().getStringExtra("gUID");// groupId
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGroupIcon = findViewById(R.id.groupAvatarIv);
        mGroupTitle = findViewById(R.id.groupTitleTv);
        //mGroupStatus = findViewById(R.id.groupStatusTv);
        mGroupJoinFile = findViewById(R.id.joinFileIb);
        mGroupSend = findViewById(R.id.sendIb);
        mGroupMessage = findViewById(R.id.messageEt);
        groupChats = new ArrayList<>();
        memberList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Groupes");
        refUsers = FirebaseDatabase.getInstance().getReference("Users");
        requestQueue = Volley.newRequestQueue(this);

        mGroupRv = findViewById(R.id.groupChatRv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mGroupRv.setHasFixedSize(true);
        mGroupRv.setLayoutManager(linearLayoutManager);
    }

    private void checkUserStatus() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        if (fUser == null){
            startActivity(new Intent(GroupChatActivity.this, MainActivity.class));
            finish();
        }else{
            myUID = fUser.getUid();
        }
    }

    private void sendNotifications(final String hisUID, final String nameUser, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Query queryRef = ref.orderByKey().equalTo(hisUID);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUID, nameUser+": "+message, "New group message", hisUID, R.drawable.ic_notif);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAaqKJdP0:APA91bFcvbly22wYH5G2lUq3dzXKc5WmpD2oIC7bA79q2li3OONMW0gZUZsmSf7rWYTPDamKfttV8tzo7FlUBcGkGRxfwcVfz5oy3fWKK5yyQ4T4H5y8gjtQK-GYy1DKD_7bVbQYjr2t");

                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest); /// 12:28
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_chat, menu);
        menu.findItem(R.id.menu_action_edit_contact).setVisible(false);
        menu.findItem(R.id.menu_action_new_group).setVisible(false);
        /*if (gROLE.equals("creator")||gROLE.equals("admin")){
            menu.findItem(R.id.menu_action_details_group).setVisible(true);
        }else{
            menu.findItem(R.id.menu_action_details_group).setVisible(false);
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_details_group){
            Intent intent = new Intent(GroupChatActivity.this, GroupParticipants.class);
            intent.putExtra("gUID", gUID);
            intent.putExtra("gROLE", gROLE);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}