package cm.deone.corp.tontines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;

public class AddTontine extends AppCompatActivity {

    private EditText mTontineName;
    private EditText mTontineDevise;
    private EditText mTontineDescription;
    private CheckBox mPrivateCb;
    private Button mTontineCreate;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tontine);
        checkUser();
    }

    private void checkUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Intent intent = new Intent(AddTontine.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            initializeUI();
            mTontineCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameTontine = mTontineName.getText().toString().trim();
                    String deviseTontine = mTontineDevise.getText().toString().trim();
                    String descriptionTontine = mTontineDescription.getText().toString().trim();
                    boolean privateTontine = mPrivateCb.isChecked();
                    boolean activateTontine = true;
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    //registerNewUser();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("idTontine", timestamp);
                    hashMap.put("nameTontine", nameTontine);
                    hashMap.put("deviseTontine", deviseTontine);
                    hashMap.put("descriptionTontine", descriptionTontine);
                    hashMap.put("dateCreationTontine", timestamp);
                    hashMap.put("statusTontine", privateTontine);
                    hashMap.put("activeTontine", activateTontine);
                    user.createTontine(AddTontine.this, hashMap);
                }
            });
        }
    }

    private void initializeUI() {
        mTontineName = findViewById(R.id.tontineNameEdtv);
        mTontineDevise = findViewById(R.id.deviseEdtv);
        mTontineDescription = findViewById(R.id.descriptionEdtv);
        mPrivateCb = findViewById(R.id.privateCb);
        mTontineCreate = findViewById(R.id.buttonContinue);
        mProgressBar = findViewById(R.id.progressBar);
        user = new User();
    }
}