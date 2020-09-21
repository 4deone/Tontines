package cm.deone.corp.tontines.membres;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;

public class ShowMembre extends AppCompatActivity {

    private String uID;
    private String tontineID;
    private String mID;
    private Toolbar toolbar;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mBureau;
    private TextView mPhone;
    private TextView mCni;
    private TextView mAciennete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_membre);
        Intent intent = getIntent();
        mID = ""+intent.getStringExtra(this.getResources().getString(R.string.mID));
        tontineID = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        checkUserStatus();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_membre, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    private void manageSearchView(SearchView searchView) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_edit) {
            //Lancer la page Settings
            return true;
        }
        if (id == R.id.menu_action_freeze) {
            //Lancer la page Settings
            return true;
        }
        if (id == R.id.menu_action_delete) {
            //Lancer la page Settings
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(ShowMembre.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            uID = mUser.getUid();
            initViews();
            getMemberInfos();
        }
    }

    private void getMemberInfos() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("name").getValue(String.class);
                String bureau = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("bureau").getValue(String.class);
                String phone = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("phone").getValue(String.class);
                String timestamp = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("date").getValue(String.class);
                String cniNumero = snapshot.child("Users").child(mID).child("cniUser").getValue(String.class);
                String cniDelivery = snapshot.child("Users").child(mID).child("deliveryCniUser").getValue(String.class);

                SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                Calendar calendar = Calendar.getInstance();
                assert timestamp != null;
                calendar.setTimeInMillis(Long.parseLong(timestamp));
                String date = formater.format(calendar.getTime());

                mName.setText(name);
                mBureau.setText(bureau);
                mPhone.setText(phone);
                mAciennete.setText(ShowMembre.this.getResources().getString(R.string.member_anciennete, date));
                mCni.setText(ShowMembre.this.getResources().getString(R.string.member_cni, cniNumero, cniDelivery));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowMembre.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initViews() {
        toolbar = findViewById(R.id.showMemmberToolbar);
        //toolbar.setTitle("DashBoard");
        setSupportActionBar(toolbar);
        mAvatar = findViewById(R.id.imContact);
        mName = findViewById(R.id.tvMemberName);
        mBureau = findViewById(R.id.tvBureauMembre);
        mPhone = findViewById(R.id.tvPhone);
        mCni = findViewById(R.id.tvCni);
        mAciennete = findViewById(R.id.tvAnciennete);
    }
}