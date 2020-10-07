package cm.deone.corp.tontines;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
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

    private DatabaseReference reference;
    private AdapterContacts adapterContacts;
    private RecyclerView rvContacts;
    private TextView mNoContacts;
    private List<User> contactList;
    private List<User> uContactList;
    private Toolbar contactToolbar;

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
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(){
        contactToolbar = findViewById(R.id.contactToolbar);
        contactToolbar.setTitle("Mes contacts");
        setSupportActionBar(contactToolbar);
        contactList = loadAllContacts(Contacts.this);
        uContactList = new ArrayList<>();
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
                    adapterContacts = new AdapterContacts(Contacts.this, uContactList);
                    rvContacts.setAdapter(adapterContacts);
                    adapterContacts.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(int position) {
                            Toast.makeText(Contacts.this, ""+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLongItemClick(int position) {
                            showEditProfileDialog(uContactList.get(position).getIdUser(), uContactList.get(position).getNameUser(),uContactList.get(position).getPhoneUser());
                        }
                    });
                }
                /*int countContact = 0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for (int count = 0; count < contactList.size(); count++){
                        assert user != null;
                        if (contactList.get(count).getPhoneUser().equals(user.getPhoneUser())) {
                            uContactList.add(user);
                            countContact++;
                            break;
                        }
                    }
                    contactToolbar.setSubtitle(""+countContact+" contacts");
                    adapterContacts = new AdapterContacts(Contacts.this, uContactList);
                    rvContacts.setAdapter(adapterContacts);
                    adapterContacts.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(int position) {
                            Toast.makeText(Contacts.this, ""+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLongItemClick(int position) {
                            showEditProfileDialog(uContactList.get(position).getIdUser(), uContactList.get(position).getNameUser(),uContactList.get(position).getPhoneUser());
                        }
                    });
                }*/
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
                        public void onItemClick(int position) {
                            Toast.makeText(Contacts.this, ""+uContactList.get(position).getNameUser(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onLongItemClick(int position) {
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

}