package cm.deone.corp.tontines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText mUserEmailEdt;
    private EditText mUserPasswordEdt;
    private EditText mUserConfirmPasswordEdt;
    private Button mRegisterbt;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUser();
    }

    private void checkUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser!=null){
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
    }

    private void registerNewUser() {
        mProgressBar.setVisibility(View.VISIBLE);

        String email, password, confirm;
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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(MainActivity.this, Profil.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}