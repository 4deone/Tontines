package cm.deone.corp.tontines.tontine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;

public class AddTontine extends AppCompatActivity {

    private EditText mTontineName;
    private EditText mTontineDevise;
    private EditText mTontineDescription;
    private CheckBox mPrivateCb;
    private CheckBox mConditionCb;
    private Button mTontineCreate;
    private ProgressBar mProgressBar;
    private Tontine tontine;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tontine);
        checkUser();
    }

    private void checkUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(AddTontine.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            initializeUI();
            idUser = mUser.getUid();
            mTontineCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConditionCb.isChecked()){

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(idUser);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("nameUser").getValue(String.class);

                                String nameTontine = mTontineName.getText().toString().trim();
                                String deviseTontine = mTontineDevise.getText().toString().trim();
                                String descriptionTontine = mTontineDescription.getText().toString().trim();

                                tontine.setNameTontine(nameTontine);
                                tontine.setNameTontine(deviseTontine);
                                tontine.setNameTontine(descriptionTontine);
                                tontine.setStatusTontine(mPrivateCb.isChecked());
                                tontine.setActiveTontine(true);

                                tontine.createTontine(idUser, name);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(AddTontine.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(AddTontine.this, "accept user conditions!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void initializeUI() {
        mTontineName = findViewById(R.id.tontineNameEdtv);
        mTontineDevise = findViewById(R.id.deviseEdtv);
        mTontineDescription = findViewById(R.id.descriptionEdtv);
        mPrivateCb = findViewById(R.id.privateCb);
        mConditionCb = findViewById(R.id.conditionCb);
        mTontineCreate = findViewById(R.id.buttonContinue);
        mProgressBar = findViewById(R.id.progressBar);
        tontine = new Tontine(this);
    }
}