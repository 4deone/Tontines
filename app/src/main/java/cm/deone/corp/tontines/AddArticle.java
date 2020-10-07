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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddArticle extends AppCompatActivity {

    private Spinner mSpType;
    private EditText mTitreArticle;
    private EditText mContentArticle;
    private Button mAddArticle;
    private ProgressBar mPbArticle;

    private String uID;
    private String idTontine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        checkUserStatus();
    }

    private void checkUserStatus() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(AddArticle.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            uID = mUser.getUid();
            initViews();
            mAddArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkViews();
                }
            });
        }
    }

    private void initViews() {
        Intent intent = getIntent();
        idTontine = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        mSpType = findViewById(R.id.spTypeArticle);
        mTitreArticle = findViewById(R.id.tontineNameEdtv);
        mContentArticle = findViewById(R.id.descriptionEdtv);
        mAddArticle = findViewById(R.id.buttonContinue);
        mPbArticle = findViewById(R.id.progressBar);
    }

    private void clearViews() {
        //mSpType ;
        mTitreArticle.setText("");
        mContentArticle.setText("");
    }

    private void checkViews() {
        String typeArticle = mSpType.getSelectedItem().toString();
        String titreArticle = mTitreArticle.getText().toString().trim();
        String contenuArticle = mContentArticle.getText().toString().trim();

        if (TextUtils.isEmpty(typeArticle)) {
            Toast.makeText(getApplicationContext(), "Entrez le type de l'article...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(titreArticle)) {
            Toast.makeText(getApplicationContext(), "Entrez le titre de l'article...", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(contenuArticle)) {
            Toast.makeText(getApplicationContext(), "Entrez le contenu de l'article...", Toast.LENGTH_SHORT).show();
            return;
        }
        mPbArticle.setVisibility(View.VISIBLE);
        String timestamp = String.valueOf(System.currentTimeMillis());
        saveArticle(timestamp, typeArticle, titreArticle, contenuArticle);
    }

    private void saveArticle(String timestamp, String typeArticle, String titreArticle, String contenuArticle) {
        HashMap<String, Object> hashMapArticle = new HashMap<>();
        hashMapArticle.put("idArticle", timestamp);
        hashMapArticle.put("typeArticle", typeArticle);
        hashMapArticle.put("titreArticle", titreArticle);
        hashMapArticle.put("contenuArticle", contenuArticle);
        hashMapArticle.put("dateArticle", timestamp);
        DatabaseReference refReglement = FirebaseDatabase.getInstance().getReference("Tontines").child(idTontine).child("Reglement");
        refReglement.child(timestamp).setValue(hashMapArticle)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddArticle.this, "Success add...", Toast.LENGTH_LONG).show();
                        mPbArticle.setVisibility(View.GONE);
                        clearViews();
                    }
                })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddArticle.this, "Error not saved article...", Toast.LENGTH_LONG).show();
                mPbArticle.setVisibility(View.GONE);
            }
        });
    }

}