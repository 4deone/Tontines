package cm.deone.corp.tontines;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

import cm.deone.corp.tontines.controler.ControlUser;
import cm.deone.corp.tontines.models.User;

public class Profil extends AppCompatActivity {

    private EditText mName;
    private EditText mPhone;
    private EditText mCni;
    private TextView mCniDeliveryDate;
    private ImageButton mChooseDeliveryDate;
    private EditText mCity;
    private Button mSaveProfil;
    private Spinner mCountryCode;
    private ProgressBar mProgressBar;
    private TelephonyManager tm;
    private String locale;
    private ControlUser controlUser;

    private FirebaseUser mUser;
    private int mYear;
    private int mMonth;
    private int mDay;

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
        mChooseDeliveryDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Profil.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                mCniDeliveryDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void checkUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Intent intent = new Intent(Profil.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initializeUI() {
        tm = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        locale = tm.getNetworkCountryIso();
        mName = findViewById(R.id.nameEdtv);
        mPhone = findViewById(R.id.phoneEdtv);
        mCni = findViewById(R.id.cniEdtv);
        mCniDeliveryDate = findViewById(R.id.cniDateTv);
        mChooseDeliveryDate = findViewById(R.id.pickDateImb);
        mCity = findViewById(R.id.villeEdtv);
        mSaveProfil = findViewById(R.id.buttonContinue);
        mCountryCode = findViewById(R.id.spCountry);
        mProgressBar = findViewById(R.id.progressBar);

        switch(locale){
            case "cm":
                mCountryCode.setSelection(1);
                break;
            case "fr":
                mCountryCode.setSelection(0);
                break;
            case "us":
                mCountryCode.setSelection(2);
                break;
            default:
        }
        this.controlUser = ControlUser.getInstance();
    }

    private void saveUserProfil() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String avatar = "https://firebasestorage.googleapis.com/v0/b/tontines-28611.appspot.com/o/user.png?alt=media&token=6332abda-fffe-4d95-ae44-b8b249e9e481";
        String cover = "https://firebasestorage.googleapis.com/v0/b/tontines-28611.appspot.com/o/terre.jpeg?alt=media&token=f7d584ab-2c7f-432d-a986-7d74f56c4bb5";
        String name = "";
        String indicatif = "";
        String phone = "";
        String city = "";
        String cni = "";
        String deliveryDateCni = "";
        String onlineUser = "online";
        String typingTo = "noOne";
        try{
            name = mName.getText().toString().trim();
            indicatif = mCountryCode.getSelectedItem().toString();
            phone = mPhone.getText().toString().trim();
            city = mCity.getText().toString().trim();
            cni = mCni.getText().toString().trim();
            deliveryDateCni = mCniDeliveryDate.getText().toString().trim();
        }catch (Exception e){
            e.printStackTrace();
        }
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

        if (TextUtils.isEmpty(cni)) {
            Toast.makeText(this, "Entrez votre numéro de CNI...", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(deliveryDateCni)) {
            Toast.makeText(this, "Entrez la date de validité de votre CNI...", Toast.LENGTH_LONG).show();
            return;
        }
        String telephone = getResources().getString(R.string.telephone, indicatif, phone);
        String rule = "user";

        controlUser.createNewUser(mUser.getUid(), name, avatar, cover, telephone, mUser.getEmail(), cni, deliveryDateCni, city, timestamp, rule, onlineUser, typingTo, true);
        controlUser.addUser(Profil.this, mProgressBar);
    }
}