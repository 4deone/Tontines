package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm.deone.corp.tontines.controler.ControlUser;

public class MainActivity extends AppCompatActivity {

    private EditText mUserEmailEdt;
    private EditText mUserPasswordEdt;
    private EditText mUserConfirmPasswordEdt;
    private Button mRegisterbt;
    private ProgressBar mProgressBar;
    private ControlUser controlUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();
    }

    private void checkUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser !=null){
            Intent intent = new Intent(MainActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        }else{
            initializeUI();
            mRegisterbt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerNewUser();
                }
            });
        }
    }

    private void initializeUI() {
        mUserEmailEdt = findViewById(R.id.emailEdtv);
        mUserPasswordEdt = findViewById(R.id.passwordEdtv);
        mUserConfirmPasswordEdt = findViewById(R.id.passwordConfirmEdtv);
        mRegisterbt = findViewById(R.id.buttonContinue);
        mProgressBar = findViewById(R.id.progressBar);
        this.controlUser = ControlUser.getInstance();
    }

    private void registerNewUser() {
        mProgressBar.setVisibility(View.VISIBLE);

        String email;
        String password;
        String confirm;

        email = mUserEmailEdt.getText().toString().trim();
        password = mUserPasswordEdt.getText().toString().trim();
        confirm = mUserConfirmPasswordEdt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(getApplicationContext(), "Please confirm your password!", Toast.LENGTH_LONG).show();
            return;
        }

        controlUser.createNewUser(email);
        controlUser.signUser(MainActivity.this, mProgressBar, password);
    }
}