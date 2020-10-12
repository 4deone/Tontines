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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cm.deone.corp.tontines.adapters.AdapterContacts;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.User;

import static cm.deone.corp.tontines.outils.MesOutils.formatPhone;
import static cm.deone.corp.tontines.outils.MesOutils.loadAllContacts;

public class Contacts extends AppCompatActivity {

    private static final  String TAG_REQUEST_DASHBOARD_TONTINE = "TONTINES";
    private static final  String TAG_REQUEST_DASHBOARD_FORUM = "MESSAGES";
    private static final  String TAG_REQUEST_MEMBRES = "MEMBRES";

    private String request;

    private MenuItem searchItem;
    private MenuItem settingsItem;
    private MenuItem newTontineItem;

    private Toolbar contactToolbar;

    private DatabaseReference reference;
    private AdapterContacts adapterContacts;
    private RecyclerView rvContacts;
    private TextView mNoContacts;
    private List<User> contactList;
    private List<User> uContactList;
    private ArrayList<String> mMemberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initViews();
        contactsCompareDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        newTontineItem = menu.findItem(R.id.menu_action_add_into_tontine);
        settingsItem = menu.findItem(R.id.menu_action_settings);
        searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchDatabase(query);
                }else {
                    contactsCompareDatabase();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchDatabase(newText);
                }else {
                    contactsCompareDatabase();
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
            case R.id.menu_action_add_into_tontine:
                //Lancer la page Add Tontine / membreList
                if(mMemberList.isEmpty()){
                    Toast.makeText(Contacts.this, "LISTE DES MEMBRES VIDE",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Contacts.this, AddTontine.class);
                    intent.putExtra("membreList", mMemberList);
                    startActivity(intent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(){
        request = getIntent().getStringExtra("REQUEST");
        contactToolbar = findViewById(R.id.contactToolbar);
        contactToolbar.setTitle("Mes contacts");
        setSupportActionBar(contactToolbar);
        contactList = loadAllContacts(Contacts.this);
        uContactList = new ArrayList<>();
        mMemberList = new ArrayList<>();
        mNoContacts = findViewById(R.id.noContacts);
        rvContacts = findViewById(R.id.recycleContacts);
        rvContacts.setHasFixedSize(true);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void contactsCompareDatabase() {
        reference.addValueEventListener(new ValueEventListener() {
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
                        adapterContacts.setOnItemClickListener(new IntRvClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                if (searchItem.isVisible()||settingsItem.isVisible()){
                                    if(request.equals(TAG_REQUEST_DASHBOARD_FORUM)){
                                        Intent intent = new Intent(Contacts.this, ChatActivity.class);
                                        intent.putExtra("hisUID", uContactList.get(position).getIdUser());
                                        startActivity(intent);
                                    }
                                }else{
                                    settingsItem.setVisible(true);
                                    searchItem.setVisible(true);
                                    newTontineItem.setVisible(false);
                                    mMemberList.remove(uContactList.get(position).getIdUser());
                                    Toast.makeText(Contacts.this, "Nombre de membre dans la tontine"+mMemberList.size(), Toast.LENGTH_SHORT).show();
                                    if(request.equals(TAG_REQUEST_DASHBOARD_TONTINE)){
                                        ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                        if(mIvSelected.getVisibility() == View.VISIBLE){
                                            mIvSelected.setVisibility(View.GONE);
                                        }else{
                                            //Open chat
                                            searchItem.setVisible(false);
                                        }
                                        Toast.makeText(Contacts.this, ""+TAG_REQUEST_DASHBOARD_TONTINE+" =>"+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                                    }else if(request.equals(TAG_REQUEST_MEMBRES)){
                                        Toast.makeText(Contacts.this, ""+TAG_REQUEST_MEMBRES+" =>"+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                                    }else if(request.equals(TAG_REQUEST_DASHBOARD_FORUM)){
                                        Intent intent = new Intent(Contacts.this, ChatActivity.class);
                                        intent.putExtra("hisUID", uContactList.get(position).getIdUser());
                                        startActivity(intent);
                                    }
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                if (searchItem.isVisible()||settingsItem.isVisible()){
                                    mMemberList.add(uContactList.get(position).getIdUser());
                                    Toast.makeText(Contacts.this, "Nombre de membre dans la tontine"+mMemberList.size(), Toast.LENGTH_SHORT).show();
                                    searchItem.setVisible(false);
                                    settingsItem.setVisible(false);
                                    newTontineItem.setVisible(true);

                                    if(request.equals(TAG_REQUEST_DASHBOARD_TONTINE)){
                                        ImageView mIvSelected = view.findViewById(R.id.selectedIv);
                                        if(mIvSelected.getVisibility() == View.VISIBLE){

                                        }else{
                                            mIvSelected.setVisibility(View.VISIBLE);
                                        }
                                    }else if(request.equals(TAG_REQUEST_MEMBRES)){

                                    }else if(request.equals(TAG_REQUEST_DASHBOARD_FORUM)){

                                    }
                                    //showEditProfileDialog(uContactList.get(position).getIdUser(), uContactList.get(position).getNameUser(),uContactList.get(position).getPhoneUser());
                                }else{

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
        });
    }

    private void addMemberIntoTontine(String idMembre, String nameMembre, String phoneMembre) {
    }

    private void showEditProfileDialog(final String idMembre, final String nameMembre, final String phoneMembre) {
        String[] options = {"Inviter ce contact", "Voir ce contact" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0){
                    addMemberIntoTontine(idMembre, nameMembre, phoneMembre);
                }else if (which == 1){
                    voirCeContact(idMembre);
                }
            }
        });
        builder.create().show();
    }

    private void voirCeContact(String idMembre) {
        
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

    private void showAvatarDialog() {
        String[] options = {"Camera", "Gallery"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Sélectionner une image à partir");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0 :
                        break;
                    case 1 :
                        break;
                    default:
                }
            }
        });
        builder.create().show();
    }

}