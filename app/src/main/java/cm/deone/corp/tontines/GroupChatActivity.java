package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import cm.deone.corp.tontines.adapters.AdapterGroupChats;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.GroupChat;

public class GroupChatActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private String gUID;
    private String gROLE;
    private String myUID;

    private ImageView mGroupIcon;
    private TextView mGroupTitle;
    //private TextView mGroupStatus;
    private ImageButton mGroupJoinFile;
    private ImageButton mGroupSend;
    private EditText mGroupMessage;

    private RecyclerView mGroupRv;
    private AdapterGroupChats adapterGroupChats;
    private ArrayList<GroupChat> groupChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        checkUserStatus();
        initViews();
        loadGroupInfo();
        loadGroupMessages();
        loadMyGroupRole();
        mGroupJoinFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mGroupSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mGroupMessage.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "Le champs message est vide...", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }

            }
        });
    }

    private void loadMyGroupRole() {
        DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference("Groupes");
        refGroup.child(gUID).child("Participants").orderByChild("uid").equalTo(myUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    gROLE = ds.child("role").getValue(String.class);
                    // refresh menu items
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadGroupMessages() {
        groupChats = new ArrayList<>();
        DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference("Groupes");
        refGroup.child(gUID).child("Messages")
                .addValueEventListener(new ValueEventListener() {
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
        });

    }

    private void sendMessage(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMapGroupMessage = new HashMap<>();
        hashMapGroupMessage.put("sender", ""+myUID);
        hashMapGroupMessage.put("message", ""+message);
        hashMapGroupMessage.put("timestamp", ""+timestamp);
        hashMapGroupMessage.put("type", "text");


        DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference("Groupes");
        refGroup.child(gUID).child("Messages").child(timestamp)
                .setValue(hashMapGroupMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid) {
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

    private void loadGroupInfo() {
        DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference("Groupes");
        refGroup.orderByChild("groupId").equalTo(gUID).addValueEventListener(new ValueEventListener() {
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