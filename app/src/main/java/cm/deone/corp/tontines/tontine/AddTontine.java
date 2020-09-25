package cm.deone.corp.tontines.tontine;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.controler.ControlTontine;

public class AddTontine extends AppCompatActivity {

    private EditText mTontineName;
    private EditText mTontineDevise;
    private EditText mTontineDescription;
    private CheckBox mPrivateCb;
    private CheckBox mConditionCb;
    private Button mAddTontineBt;
    private ProgressBar mProgressBar;
    private ControlTontine controlTontine;
    private String idUser;
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tontine);
        checkUser();
    }

    /**
     *
     */
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
            mAddTontineBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mConditionCb.isChecked()){
                        addNewTontine();
                    }else {
                        Toast.makeText(AddTontine.this, "accepter les conditions d'utilisation!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mConditionCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        String name = mTontineName.getText().toString().trim();
                        String devise = mTontineDevise.getText().toString().trim();
                        String description = mTontineDescription.getText().toString().trim();
                        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(devise)||TextUtils.isEmpty(description)){
                            mAddTontineBt.setEnabled(false);
                            Toast.makeText(AddTontine.this, "Vous devez d'abord remplir les champs de saisie!", Toast.LENGTH_SHORT).show();
                            mConditionCb.setChecked(false);
                        }else{
                            mAddTontineBt.setEnabled(true);
                        }
                    }
                }
            });
            mTontineName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddTontine.this,
                            new DatePickerDialog.OnDateSetListener() {

                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {

                                    mTontineName.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
        }
    }

    /**
     *
     */
    private void addNewTontine() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(idUser);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                String coverTontine = "https://firebasestorage.googleapis.com/v0/b/tontines-28611.appspot.com/o/terre.jpeg?alt=media&token=f7d584ab-2c7f-432d-a986-7d74f56c4bb5";
                boolean statusTontine =  false;
                String nameTontine = "";
                String deviseTontine =  "";
                String descriptionTontine =  "";
                String name =  "";
                String phone =  "";
                try{
                    name = dataSnapshot.child("nameUser").getValue(String.class);
                    phone = dataSnapshot.child("phoneNumber").getValue(String.class);
                    nameTontine = mTontineName.getText().toString().trim();
                    deviseTontine = mTontineDevise.getText().toString().trim();
                    descriptionTontine = mTontineDescription.getText().toString().trim();
                    statusTontine =  mPrivateCb.isChecked();
                }catch(Exception e){}

                if (TextUtils.isEmpty(nameTontine)) {
                    Toast.makeText(getApplicationContext(), "Entrez le nom de la tontine...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(deviseTontine)) {
                    Toast.makeText(getApplicationContext(), "Entrez la dévise de la tontine...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(descriptionTontine)) {
                    Toast.makeText(getApplicationContext(), "Entrez la description de la tontine...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Impossible de trouver le nom de l'utilisateur...", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Impossible de trouver le téléphone de l'utilisateur...", Toast.LENGTH_SHORT).show();
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                controlTontine.createNewTontine(timestamp,nameTontine, coverTontine, deviseTontine, descriptionTontine,timestamp, statusTontine, true);
                controlTontine.addTontine(AddTontine.this, mProgressBar, idUser, name, phone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddTontine.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Initialisation des objets graphiques
     */
    private void initializeUI() {
        mTontineName = findViewById(R.id.tontineNameEdtv);
        mTontineDevise = findViewById(R.id.deviseEdtv);
        mTontineDescription = findViewById(R.id.descriptionEdtv);
        mPrivateCb = findViewById(R.id.privateCb);
        mConditionCb = findViewById(R.id.conditionCb);
        mAddTontineBt = findViewById(R.id.buttonContinue);
        mProgressBar = findViewById(R.id.progressBar);
        this.controlTontine = ControlTontine.getInstance();
    }
}