package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddArticle extends AppCompatActivity {

    private DatabaseReference reference;
    private Spinner mSpType;
    private EditText mTitreArticle;
    private EditText mContentArticle;
    private Button mAddArticle;
    private ProgressBar mPbArticle;

    private String myUID;
    private String myName;
    private String idTontine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_article);
        checkUserStatus();
        initViews();
        mAddArticle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkViews();
            }
        });
        reference.orderByKey().equalTo(myUID).addValueEventListener(valUserName);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void checkUserStatus() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(AddArticle.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            myUID = mUser.getUid();
        }
    }

    private void initViews() {
        Intent intent = getIntent();
        idTontine = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        reference = FirebaseDatabase.getInstance().getReference("Users");
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

    private void saveArticle(final String timestamp, final String typeArticle, final String titreArticle, final String contenuArticle) {
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
                        prepareNotification(
                                ""+timestamp,
                                ""+myName+" added new article in the "+typeArticle,
                                ""+titreArticle+"\n"+contenuArticle,
                                "ArticleNotification",
                                "ARTICLE");
                    }
                })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddArticle.this, "Error not saved article...", Toast.LENGTH_LONG).show();
                mPbArticle.setVisibility(View.GONE);
            }
        });
    }

    private void prepareNotification(String idArticle, String title, String description, String notificationType, String notificationTopic){
        String NOTIFICATION_TOPIC = "/topics/"+notificationTopic;
        String NOTIFICATION_TITLE = title;
        String NOTIFICATION_MESSAGE = description;
        String NOTIFICATION_TYPE = notificationType;

        JSONObject notificationJo = new JSONObject();
        JSONObject notificationBodyJo = new JSONObject();

        try {
            //What to send
            notificationBodyJo.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJo.put("sender", myUID);
            notificationBodyJo.put("idArticle", idArticle);
            notificationBodyJo.put("titreArticle", NOTIFICATION_TITLE);
            notificationBodyJo.put("descriptionArticle", NOTIFICATION_MESSAGE);
            //Where to send
            notificationJo.put("to", NOTIFICATION_TOPIC);
            notificationJo.put("data", notificationBodyJo);

        }catch(JSONException e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        sendArticleNotification(notificationJo);
    }

    private void sendArticleNotification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("FCM_RESPONSE", "onResponse: "+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddArticle.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key=AAAAaqKJdP0:APA91bFcvbly22wYH5G2lUq3dzXKc5WmpD2oIC7bA79q2li3OONMW0gZUZsmSf7rWYTPDamKfttV8tzo7FlUBcGkGRxfwcVfz5oy3fWKK5yyQ4T4H5y8gjtQK-GYy1DKD_7bVbQYjr2t");

                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private ValueEventListener valUserName = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for(DataSnapshot ds:snapshot.getChildren()){
                myName = ds.child("nameUser").getValue(String.class);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(AddArticle.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}