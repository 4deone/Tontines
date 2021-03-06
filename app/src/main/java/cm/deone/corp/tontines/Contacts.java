package cm.deone.corp.tontines;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cm.deone.corp.tontines.adapters.AdapterContacts;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.User;

import static cm.deone.corp.tontines.outils.MesOutils.formatPhone;
import static cm.deone.corp.tontines.outils.MesOutils.loadAllContacts;

public class Contacts extends AppCompatActivity {

    private static final  String TAG_REQUEST_TONTINE = "TONTINES";
    private static final  String TAG_REQUEST_MESSAGES = "MESSAGES";
    private static final  String TAG_REQUEST_MEMBRES = "MEMBRES";
    private static final  String TAG_REQUEST_GROUPES = "GROUPES";
    private static final  String TAG_REQUEST_PARTICIPANTS = "PARTICIPANTS";

    private String request;
    private String gUID;
    private String myUID;

    private Toolbar contactToolbar;

    private DatabaseReference reference;
    private AdapterContacts adapterContacts;
    private RecyclerView rvContacts;
    private TextView mNoContacts;
    private List<User> contactList;
    private List<User> uContactList;
    private ArrayList<String> mMemberList;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        checkUser();
        initViews();

        reference.addValueEventListener(valGetAllContacts);

        floatingActionButton.setOnClickListener(fabOnClick);
    }

    @Override
    protected void onStop() {
        reference.removeEventListener(valGetAllContacts);
        super.onStop();
    }

    private void checkUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null){
            startActivity(new Intent(Contacts.this, MainActivity.class));
            finish();
        }else{
            myUID = firebaseUser.getUid();
        }
    }

    private void initViews(){
        Intent intent = getIntent();
        request = intent.getStringExtra("REQUEST");
        gUID = intent.getStringExtra("groupeId");
        contactToolbar = findViewById(R.id.contactToolbar);
        contactToolbar.setTitle("Mes contacts");
        setSupportActionBar(contactToolbar);
        contactList = loadAllContacts(Contacts.this);
        uContactList = new ArrayList<>();
        mMemberList = new ArrayList<>();
        floatingActionButton = findViewById(R.id.getSelectedContactFab);
        mNoContacts = findViewById(R.id.noContacts);
        rvContacts = findViewById(R.id.recycleContacts);
        rvContacts.setHasFixedSize(true);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void addParticipant(String idUser) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", ""+idUser);
        hashMap.put("role", "participant");
        hashMap.put("timestamp", ""+timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groupes");
        ref.child(gUID).child("Participants").child(idUser).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Contacts.this, "Added successfuly...", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Contacts.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchDatabase(final String searchQuery) {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uContactList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for (int count = 0; count < contactList.size(); count++){
                        if (contactList.get(count).getPhoneUser().equals(user.getPhoneUser())&&(user.getNameUser().toLowerCase().contains(searchQuery.toLowerCase()) ||
                                user.getPhoneUser().toLowerCase().contains(searchQuery.toLowerCase()))) {
                            uContactList.add(user);
                            break;
                        }
                    }
                    adapterContacts = new AdapterContacts(Contacts.this, uContactList);
                    rvContacts.setAdapter(adapterContacts);
                    adapterContacts.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Toast.makeText(Contacts.this, ""+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            Toast.makeText(Contacts.this, ""+uContactList.get(position).getPhoneUser(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Contacts.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ValueEventListener valGetAllContacts = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            uContactList.clear();
            int countContact = 0;
            for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                User user = dataSnapshot.getValue(User.class);
                Cursor phones = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, null, null, null);
                while (phones.moveToNext()) {
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // Cleanup the phone number
                    phoneNumber = formatPhone(phoneNumber);
                    // Add in the list
                    assert user != null;
                    if(phoneNumber.equals(user.getPhoneUser())){
                        uContactList.add(user);
                        countContact++;
                        break;
                    }
                }
                phones.close();
                contactToolbar.setSubtitle(""+countContact+" contacts");
                if(uContactList.isEmpty()){
                    mNoContacts.setVisibility(View.VISIBLE);
                    rvContacts.setVisibility(View.GONE);
                }else{
                    rvContacts.setVisibility(View.VISIBLE);
                    mNoContacts.setVisibility(View.GONE);
                    adapterContacts = new AdapterContacts(Contacts.this, uContactList);
                    rvContacts.setAdapter(adapterContacts);
                    adapterContacts.setTagRequest(""+request, ""+gUID);
                    adapterContacts.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(View view, final int position) {
                            if(request.equals(TAG_REQUEST_TONTINE)){
                                ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                if (mIvSelected.getVisibility() == View.VISIBLE){
                                    mIvSelected.setVisibility(View.GONE);
                                    mMemberList.remove(uContactList.get(position).getIdUser());
                                    if (mMemberList.size() == 0){
                                        floatingActionButton.setVisibility(floatingActionButton.getVisibility() != View.GONE ? View.GONE:View.VISIBLE);
                                    }
                                }else{

                                }
                            }else if(request.equals(TAG_REQUEST_MEMBRES)){
                                Toast.makeText(Contacts.this, ""+TAG_REQUEST_MEMBRES+" =>"+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                            }else if(request.equals(TAG_REQUEST_MESSAGES)){
                                Intent intent = new Intent(Contacts.this, ChatActivity.class);
                                intent.putExtra("hisUID", uContactList.get(position).getIdUser());
                                startActivity(intent);
                            }else if(request.equals(TAG_REQUEST_GROUPES)){
                                ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                if (mIvSelected.getVisibility() == View.VISIBLE){
                                    mIvSelected.setVisibility(View.GONE);
                                    mMemberList.remove(uContactList.get(position).getIdUser());
                                    if (mMemberList.size() == 0){
                                        floatingActionButton.setVisibility(floatingActionButton.getVisibility() != View.GONE ? View.GONE:View.VISIBLE);
                                    }
                                }else{

                                }
                            }else if(request.equals(TAG_REQUEST_PARTICIPANTS)){
                                ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                if (mIvSelected.getVisibility() == View.VISIBLE){
                                    Toast.makeText(Contacts.this, "Contact membre de ce groupe", Toast.LENGTH_SHORT).show();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Contacts.this);
                                    builder.setTitle("Ajouter un membre")
                                            .setMessage("Ajouter cet utilisateur dans ce groupe")
                                            .setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    addParticipant(uContactList.get(position).getIdUser());
                                                }
                                            })
                                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    builder.create().show();
                                }
                            }
                        }

                        @Override
                        public void onLongItemClick(View view, final int position) {
                            if(request.equals(TAG_REQUEST_TONTINE)){
                                mMemberList.add(uContactList.get(position).getIdUser());
                                ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                mIvSelected.setVisibility(mIvSelected.getVisibility()!=View.VISIBLE?View.VISIBLE:View.GONE);
                                floatingActionButton.setVisibility(floatingActionButton.getVisibility()!=View.VISIBLE?View.VISIBLE:View.GONE);
                            }else if(request.equals(TAG_REQUEST_MEMBRES)){

                            }else if(request.equals(TAG_REQUEST_MESSAGES)){

                            }else if(request.equals(TAG_REQUEST_GROUPES)){
                                mMemberList.add(uContactList.get(position).getIdUser());
                                ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                mIvSelected.setVisibility(mIvSelected.getVisibility()!=View.VISIBLE?View.VISIBLE:View.GONE);
                                floatingActionButton.setVisibility(floatingActionButton.getVisibility()!=View.VISIBLE?View.VISIBLE:View.GONE);
                            }else if(request.equals(TAG_REQUEST_PARTICIPANTS)){
                                ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                if (mIvSelected.getVisibility() == View.VISIBLE){
                                    Toast.makeText(Contacts.this, "Contact membre de ce groupe", Toast.LENGTH_SHORT).show();
                                }else{
                                    mMemberList.add(uContactList.get(position).getIdUser());
                                    mIvSelected.setVisibility(mIvSelected.getVisibility()!=View.VISIBLE?View.VISIBLE:View.GONE);
                                    floatingActionButton.setVisibility(floatingActionButton.getVisibility()!=View.VISIBLE?View.VISIBLE:View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(Contacts.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener fabOnClick = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            //Lancer la page Add Tontine / membreList
            if(mMemberList.isEmpty()){
                Toast.makeText(Contacts.this, "LISTE DES MEMBRES VIDE",Toast.LENGTH_SHORT).show();
            }else{
                if(request.equals(TAG_REQUEST_TONTINE)){
                    mMemberList.add(myUID);
                    Intent intent = new Intent(Contacts.this, AddTontine.class);
                    intent.putExtra("membreList", mMemberList);
                    startActivity(intent);
                }else  if(request.equals(TAG_REQUEST_GROUPES)){
                    mMemberList.add(myUID);
                    Intent intent = new Intent(Contacts.this, AddGroup.class);
                    intent.putExtra("membreList", mMemberList);
                    startActivity(intent);
                }else if(request.equals(TAG_REQUEST_PARTICIPANTS)){
                    for (String item:mMemberList){
                        addParticipant(item);
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        MenuItem settingsItem = menu.findItem(R.id.menu_action_settings);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchDatabase(query);
                }else {
                    reference.addValueEventListener(valGetAllContacts);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchDatabase(newText);
                }else {
                    reference.addValueEventListener(valGetAllContacts);
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_action_settings:
                //Lancer la page SettingsFragment
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}