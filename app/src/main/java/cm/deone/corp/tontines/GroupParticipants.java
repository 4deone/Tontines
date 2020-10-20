package cm.deone.corp.tontines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.List;

import cm.deone.corp.tontines.adapters.AdapterChats;
import cm.deone.corp.tontines.adapters.AdapterParticipants;
import cm.deone.corp.tontines.adapters.AdapterTontines;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Chat;
import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;

import static cm.deone.corp.tontines.outils.MesOutils.formatPhone;

public class GroupParticipants extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton faButton;
    private List<User> userList;
    private AdapterParticipants adapterParticipants;
    private RecyclerView rvParticipant;

    ArrayList<String> roleList;

    private ImageView groupIconIv;
    private TextView groupTitleTv;
    private TextView memberRoleTv;

    private String myUID;
    private String gUID;
    private String gROLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participants);
        checkUserStatus();
        initViews();
        loadGroupInfo();
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupParticipants.this, Contacts.class);
                intent.putExtra("REQUEST", "PARTICIPANTS");
                intent.putExtra("groupeId", gUID);
                startActivity(intent);
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
        }else {
            startActivity(new Intent(GroupParticipants.this, MainActivity.class));
            finish();
        }
    }

    private void initViews() {
        gUID = getIntent().getStringExtra("gUID");// groupId
        gROLE = getIntent().getStringExtra("gROLE");// myGroupeRole
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        groupIconIv = findViewById(R.id.groupIconIv);
        groupTitleTv = findViewById(R.id.groupTitleTv);
        memberRoleTv = findViewById(R.id.memberRoleTv);
        faButton = findViewById(R.id.faButton);
        rvParticipant = findViewById(R.id.rvParticipant);
        rvParticipant.setHasFixedSize(true);
        rvParticipant.setLayoutManager(new LinearLayoutManager(this));

    }

    private void loadGroupInfo() {
        userList = new ArrayList<>();
        roleList = new ArrayList<>();
        final DatabaseReference refGroupUser = FirebaseDatabase.getInstance().getReference("Users");
        final DatabaseReference refGroup = FirebaseDatabase.getInstance().getReference("Groupes");
        DatabaseReference refGroup1 = FirebaseDatabase.getInstance().getReference("Groupes");
        refGroup1.orderByChild("groupId").equalTo(gUID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot ds:snapshot.getChildren()){
                    final String gTitle = ds.child("groupTitle").getValue(String.class);
                    final String gIcon= ds.child("groupIcon").getValue(String.class);

                    gROLE = ds.child("Participants").child(myUID).child("role").getValue(String.class);
                    groupTitleTv.setText(gTitle);
                    memberRoleTv.setText(gROLE);
                    try {
                        Picasso.get().load(gIcon).placeholder(R.drawable.ic_action_cover).into(groupIconIv);
                    }catch(Exception e){
                        Picasso.get().load(R.drawable.ic_action_cover).into(groupIconIv);
                    }

                    refGroup.child(gUID).child("Participants")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    userList.clear();
                                    roleList.clear();
                                    for (DataSnapshot ds1:snapshot.getChildren()){
                                        final String idUser = ds1.child("uid").getValue(String.class);
                                        final String role = ds1.child("role").getValue(String.class);
                                        refGroupUser.orderByChild("idUser").equalTo(idUser).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds2:snapshot.getChildren()){
                                                    User user = ds2.getValue(User.class);
                                                    userList.add(user);
                                                    roleList.add(role);
                                                    adapterParticipants = new AdapterParticipants(GroupParticipants.this, userList, ""+gUID);
                                                    rvParticipant.setAdapter(adapterParticipants);
                                                    adapterParticipants.setOnItemClickListener(new IntRvClickListner() {
                                                        @Override
                                                        public void onItemClick(View view, int position) {

                                                        }

                                                        @Override
                                                        public void onLongItemClick(View view, final int position) {
                                                            String mUID = userList.get(position).getIdUser();
                                                            String hisPreviousRole = roleList.get(position);
                                                            String[] options;
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(GroupParticipants.this);
                                                            builder.setTitle("Sélectionner une option");
                                                            if (gROLE.equals("creator")){
                                                                if (hisPreviousRole.equals("creator")){
                                                                    Toast.makeText(GroupParticipants.this, "You", Toast.LENGTH_SHORT).show();
                                                                }else if (hisPreviousRole.equals("participant")){
                                                                    options = new String[]{"Make admin", "Remove user"};
                                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            switch(which){
                                                                                case 0 :
                                                                                    makeAdmin(userList.get(position));
                                                                                    break;
                                                                                case 1 :
                                                                                    removeParticipant(userList.get(position));
                                                                                    break;
                                                                                default:
                                                                            }
                                                                        }
                                                                    });
                                                                    builder.create().show();
                                                                }else if(hisPreviousRole.equals("admin")){
                                                                    options = new String[]{"Remove admin", "Remove user"};
                                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            switch(which){
                                                                                case 0 :
                                                                                    removeAdmin(userList.get(position));
                                                                                    break;
                                                                                case 1 :
                                                                                    removeParticipant(userList.get(position));
                                                                                    break;
                                                                                default:
                                                                            }
                                                                        }
                                                                    });
                                                                    builder.create().show();
                                                                }
                                                            }else if (gROLE.equals("admin")){
                                                                if (hisPreviousRole.equals("creator")){
                                                                    Toast.makeText(GroupParticipants.this, "Creator of group...", Toast.LENGTH_SHORT).show();
                                                                }else if (hisPreviousRole.equals("admin")){
                                                                    if(mUID.equals(idUser)){
                                                                        Toast.makeText(GroupParticipants.this, "You", Toast.LENGTH_SHORT).show();
                                                                    }else{
                                                                        options = new String[]{"Make admin", "Remove user"};
                                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                switch(which){
                                                                                    case 0 :
                                                                                        removeAdmin(userList.get(position));
                                                                                        break;
                                                                                    case 1 :
                                                                                        removeParticipant(userList.get(position));
                                                                                        break;
                                                                                    default:
                                                                                }
                                                                            }
                                                                        });
                                                                        builder.create().show();
                                                                    }
                                                                }else if (hisPreviousRole.equals("participants")){
                                                                    options = new String[]{"Make admin", "Remove user"};
                                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            switch(which){
                                                                                case 0 :
                                                                                    //makeAdmin(userList.get(position));
                                                                                    break;
                                                                                case 1 :
                                                                                    //removeParticipant(userList.get(position));
                                                                                    break;
                                                                                default:
                                                                            }
                                                                        }
                                                                    });
                                                                    builder.create().show();
                                                                }
                                                            }else if (gROLE.equals("participant")){
                                                                if(mUID.equals(idUser)){
                                                                    Toast.makeText(GroupParticipants.this, "You", Toast.LENGTH_SHORT).show();
                                                                }
                                                                if (hisPreviousRole.equals("creator")){
                                                                    Toast.makeText(GroupParticipants.this, "Creator of group...", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(GroupParticipants.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(GroupParticipants.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupParticipants.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void searchParticipants(String query) {
        userList = new ArrayList<>();

    }

    private void removeAdmin(User user) { // 30:53
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "participant");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groupes");
        ref.child(gUID).child("Participants").child(user.getIdUser()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupParticipants.this, "The user is no longer admin...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupParticipants.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeParticipant(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groupes");
        ref.child(gUID).child("Participants").child(user.getIdUser()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupParticipants.this, "The user is now removed...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupParticipants.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void makeAdmin(User user) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "admin");

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groupes");
        ref.child(gUID).child("Participants").child(user.getIdUser()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupParticipants.this, "The user is now admin...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupParticipants.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addParticipant(User user) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+user.getIdUser());
        hashMap.put("role", "participant");
        hashMap.put("timestamp", ""+timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groupes");
        ref.child(gUID).child("Participants").child(user.getIdUser()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupParticipants.this, "Added successfuly...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupParticipants.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchParticipants(query);
                }else {
                    //allUserContact();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchParticipants(newText);
                }else {
                    //allUserContact();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        MenuItem settingsItem = menu.findItem(R.id.menu_action_settings);
        settingsItem.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
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