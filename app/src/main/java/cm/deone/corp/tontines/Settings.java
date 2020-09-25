package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import cm.deone.corp.tontines.controler.ControlUser;

public class Settings extends AppCompatActivity {

    private String idUser;
    private Toolbar toolbar;
    private ImageView cover;
    private ImageView avatar;
    private ControlUser controlUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_edit) {
            //startActivity(new Intent(Dashboard.this, Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            initializeUI();
            idUser = mUser.getUid();
            getUser();
        }
    }

    private void getUser() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = "";
                String name = "";
                String avataru = "";
                String coveru = "";
                String phone =  "";
                String email = "";
                String cni = "";
                String deliveryCni = "";
                String ville = "";
                String dateCreation = "";
                boolean active = false;
                try{
                    id = snapshot.child("Users").child(idUser).child("idUser").getValue(String.class);
                    name = snapshot.child("Users").child(idUser).child("nameUser").getValue(String.class);
                    avataru = snapshot.child("Users").child(idUser).child("avatarUser").getValue(String.class);
                    coveru = snapshot.child("Users").child(idUser).child("coverUser").getValue(String.class);
                    phone = snapshot.child("Users").child(idUser).child("phoneUser").getValue(String.class);
                    email = snapshot.child("Users").child(idUser).child("emailUser").getValue(String.class);
                    cni = snapshot.child("Users").child(idUser).child("cniUser").getValue(String.class);
                    deliveryCni = snapshot.child("Users").child(idUser).child("deliveryCniUser").getValue(String.class);
                    ville = snapshot.child("Users").child(idUser).child("villeUser").getValue(String.class);
                    dateCreation = snapshot.child("Users").child(idUser).child("dateCreationUser").getValue(String.class);
                    active = (boolean)snapshot.child("Users").child(idUser).child("activeUser").getValue();
                }catch(Exception e){}
                toolbar.setTitle(name);
                controlUser.createNewUser(id, name, avataru, coveru, phone, email, cni, deliveryCni, ville, dateCreation, active);
                controlUser.readPicture(cover, coveru);
                controlUser.readPicture(avatar, avataru);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Settings.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeUI() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cover = findViewById(R.id.expandedImage);
        avatar = findViewById(R.id.avatarImage);
        this.controlUser = ControlUser.getInstance();
    }
}