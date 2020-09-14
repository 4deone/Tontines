package cm.deone.corp.tontines;

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

import java.util.HashMap;

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

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String idUser;
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
            idUser = mUser.getUid();
            mTontineCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConditionCb.isChecked()){
                        String nameTontine = mTontineName.getText().toString().trim();
                        String deviseTontine = mTontineDevise.getText().toString().trim();
                        String descriptionTontine = mTontineDescription.getText().toString().trim();
                        boolean privateTontine = mPrivateCb.isChecked();
                        boolean activateTontine = true;
                        String timestamp = String.valueOf(System.currentTimeMillis());

                        HashMap<String, Object> hashMapDescription = new HashMap<>();
                        hashMapDescription.put("idTontine", timestamp);
                        hashMapDescription.put("nameTontine", nameTontine);
                        hashMapDescription.put("deviseTontine", deviseTontine);
                        hashMapDescription.put("descriptionTontine", descriptionTontine);
                        hashMapDescription.put("dateCreationTontine", timestamp);
                        hashMapDescription.put("statusTontine", privateTontine);
                        hashMapDescription.put("activeTontine", activateTontine);

                        HashMap<String, Object> hashMapBureau = new HashMap<>();
                        hashMapBureau.put("fondateur", idUser);

                        HashMap<String, Object> hashMapMembres = new HashMap<>();
                        hashMapMembres.put(idUser, timestamp);

                        user.createTontine(AddTontine.this, hashMapDescription, hashMapBureau, hashMapMembres, timestamp, idUser);
                    }else {
                        Toast.makeText(AddTontine.this, "Il faut accepter les conditions d'utilistion.", Toast.LENGTH_SHORT).show();
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
        user = new User();
    }
}