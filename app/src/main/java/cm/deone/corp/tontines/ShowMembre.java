package cm.deone.corp.tontines;

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
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static cm.deone.corp.tontines.outils.MesOutils.dateToString;

public class ShowMembre extends AppCompatActivity {

    private DatabaseReference reference;
    private String uID;
    private String tontineID;
    private String mID;
    private ImageView mCover;
    private TextView mName;
    private TextView mBureau;
    private TextView mPhone;
    private TextView mCni;
    private TextView mAciennete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_membre);
        checkUserStatus();
        initViews();
        reference.addValueEventListener(valMemberInfos);
    }

    @Override
    protected void onStop() {
        if (valMemberInfos != null) {
            reference.removeEventListener(valMemberInfos);
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_membre, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_edit) {
            //Lancer la page SettingsFragment
            return true;
        }
        if (id == R.id.menu_action_freeze) {
            //Lancer la page SettingsFragment
            return true;
        }
        if (id == R.id.menu_action_delete) {
            //Lancer la page SettingsFragment
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
        }
    }

    private ValueEventListener valMemberInfos = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String name = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("name").getValue(String.class);
            String bureau = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("bureau").getValue(String.class);
            String phone = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("phone").getValue(String.class);
            String timestamp = snapshot.child("Tontines").child(tontineID).child("Membres").child(mID).child("date").getValue(String.class);
            String cniNumero = snapshot.child("Users").child(mID).child("cniUser").getValue(String.class);
            String cniDelivery = snapshot.child("Users").child(mID).child("deliveryCniUser").getValue(String.class);

            String avatar = snapshot.child("Users").child(mID).child("avatarUser").getValue(String.class);
            String cover = snapshot.child("Users").child(mID).child("coverUser").getValue(String.class);

            try{
                Picasso.get().load(cover).into(mCover);
            }catch(Exception e){
                Picasso.get().load(cover).placeholder(R.drawable.ic_action_cover).into(mCover);
            }

            String date = dateToString("dd/MM/yyyy hh:mm:ss", timestamp);

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
    };

    private void initViews() {
        Intent intent = getIntent();
        mID = ""+intent.getStringExtra(this.getResources().getString(R.string.mID));
        tontineID = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        reference = FirebaseDatabase.getInstance().getReference();
        Toolbar toolbar = findViewById(R.id.showMemmberToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mCover = findViewById(R.id.expandedImage);
        mName = findViewById(R.id.tvMemberName);
        mBureau = findViewById(R.id.tvBureauMembre);
        mPhone = findViewById(R.id.tvPhone);
        mCni = findViewById(R.id.tvCni);
        mAciennete = findViewById(R.id.tvAnciennete);
    }

    private void manageSearchView(SearchView searchView) {

    }
}