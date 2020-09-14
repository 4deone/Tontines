package cm.deone.corp.tontines;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import cm.deone.corp.tontines.models.User;

public class Contacts extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView rvContacts;
    private AdapterContacts adapterContacts;
    private List<User> contactList;
    private List<User> uContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        initViews();
        contactsCompareDatabase();
    }

    private void initViews(){
        contactList = loadAllContacts();
        uContactList = new ArrayList<>();
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
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    for (int count = 0; count < contactList.size(); count++){
                        if (contactList.get(count).getPhoneUser().equals(user.getPhoneUser())) {
                            uContactList.add(user);
                            break;
                        }
                    }
                    rvContacts.setAdapter(new AdapterContacts(Contacts.this, uContactList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void findInList(User user) {

    }

    private List<User> loadAllContacts(){
        List<User> uContacts = new ArrayList<>();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            // Cleanup the phone number
            //phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");
            phoneNumber = phoneNumber.replaceAll(" ", "");
            //phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");

            // Add in the list
            User uContact = new User(name,phoneNumber);
            uContacts.add(uContact);
        }
        phones.close();
        return uContacts;
    }

}