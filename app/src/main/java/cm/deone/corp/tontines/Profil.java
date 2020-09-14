package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import cm.deone.corp.tontines.models.User;

public class Profil extends AppCompatActivity {

    private EditText mName;
    private EditText mPhone;
    private EditText mCni;
    private EditText mCniDeliveryDate;
    private EditText mCity;
    private Button mSaveProfil;
    private Spinner mCountryCode;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        checkUser();
        initializeUI();
        mSaveProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfil();
            }
        });
    }

    private void checkUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Intent intent = new Intent(Profil.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeUI() {
        mName = findViewById(R.id.nameEdtv);
        mPhone = findViewById(R.id.phoneEdtv);
        mCni = findViewById(R.id.cniEdtv);
        mCniDeliveryDate = findViewById(R.id.cniDateEdtv);
        mCity = findViewById(R.id.villeEdtv);
        mSaveProfil = findViewById(R.id.buttonContinue);
        mCountryCode = findViewById(R.id.spCountry);
        mProgressBar = findViewById(R.id.progressBar);
    }

    private void saveUserProfil() {
        Toast.makeText(getApplicationContext(), "Save user...", Toast.LENGTH_LONG).show();
        String name = mName.getText().toString().trim();
        String phone = mPhone.getText().toString().trim();
        String city = mCity.getText().toString().trim();
        String cni = mCni.getText().toString().trim();
        String deliveryDateCni = mCniDeliveryDate.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter name...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please enter phone...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Please enter city...", Toast.LENGTH_LONG).show();
            return;
        }
        User user = new User();
        user.setIdUser(mUser.getUid());
        user.setEmailUser(mUser.getEmail());
        user.setNameUser(name);
        user.setPhoneUser(phone);
        user.setVilleUser(city);
        if (!TextUtils.isEmpty(cni)) {
            user.setCniUser(cni);
        }
        if (!TextUtils.isEmpty(deliveryDateCni)) {
            user.setDeliveryCniUser(deliveryDateCni);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        user.createUser(this, database);
    }
}