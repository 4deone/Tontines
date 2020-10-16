package cm.deone.corp.tontines;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cm.deone.corp.tontines.notifications.Data;
import cm.deone.corp.tontines.notifications.Sender;
import cm.deone.corp.tontines.notifications.Token;

public class AddGroup extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private RequestQueue requestQueue;
    private boolean notify = false;

    private String myUID;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;

    private FirebaseAuth firebaseAuth;

    private ImageView mGroupAvatar;
    private EditText mGroupTitle;
    private EditText mGroupDescription;
    private CheckBox mGroupIsTontine;
    private FloatingActionButton mCreateGroup;

    private ProgressDialog progressDialog;

    private ArrayList<String> membreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        checkUser();
        initViews();
        mGroupAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGroupAvatarPickDialog();
            }
        });
        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroup();
            }
        });
    }

    private void startCreatingGroup() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Creating group...");

        final String gTitre = mGroupTitle.getText().toString().trim();
        final String gDescription = mGroupDescription.getText().toString().trim();
        boolean isTontine = mGroupIsTontine.isChecked();

        if (TextUtils.isEmpty(gTitre)){
            Toast.makeText(AddGroup.this, "Ajoutez un titre à ce groupe!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTontine && TextUtils.isEmpty(gDescription)){
            Toast.makeText(AddGroup.this, "Ajoutez une description à cette tontine!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final String gTimestamp = String.valueOf(System.currentTimeMillis());

        if(image_uri == null){
            createGroup(
                    ""+gTimestamp,
                    ""+gTitre,
                    ""+gDescription,
                    "");
        }else{
            String fileNameAndPath = "Group_Imgs/" + "image" + gTimestamp;
            StorageReference storageReferenceGroup = FirebaseStorage.getInstance().getReference(fileNameAndPath);
            storageReferenceGroup.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    Uri downloadUri = uriTask.getResult();
                    if (uriTask.isSuccessful()){
                        createGroup(
                                ""+gTimestamp,
                                ""+gTitre,
                                ""+gDescription,
                                ""+downloadUri.toString());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(AddGroup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void createGroup(final String gTimestamp, final String gTitle, String gDescription, String gIcon) {
        final DatabaseReference refParticipants = FirebaseDatabase.getInstance().getReference("Groupes");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+gTimestamp);
        hashMap.put("groupTitle", ""+gTitle);
        hashMap.put("groupDescription", ""+gDescription);
        hashMap.put("groupIcon", ""+gIcon);
        hashMap.put("timestamp", ""+gTimestamp);
        hashMap.put("creatBy", firebaseAuth.getCurrentUser().getUid());

        DatabaseReference refCreatGroup = FirebaseDatabase.getInstance().getReference("Groupes");
        refCreatGroup.child(gTimestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (final String item:membreList){
                            notify = true;
                            HashMap<String, String> hashMapGroupMember = new HashMap<>();
                            hashMapGroupMember.put("uid", item);
                            if (item.equals(firebaseAuth.getCurrentUser().getUid())){
                                hashMapGroupMember.put("role", "creator");
                            }else{
                                hashMapGroupMember.put("role", "participant");
                            }
                            hashMapGroupMember.put("timestamp", gTimestamp);

                            refParticipants.child(gTimestamp).child("Participants").child(item)
                                    .setValue(hashMapGroupMember)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (notify){
                                                sendNotifications(item, gTitle, "Vous avez été ajouté à un groupe ");
                                            }
                                            notify = false;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(AddGroup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        resetViews();
                        progressDialog.dismiss();
                        Toast.makeText(AddGroup.this, "Group created successfully...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddGroup.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendNotifications(final String hisUID, final String nameUser, final String message) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Query queryRef = ref.orderByKey().equalTo(hisUID);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myUID, ""+message, ""+nameUser, hisUID, R.drawable.ic_notif);
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {

                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAAaqKJdP0:APA91bFcvbly22wYH5G2lUq3dzXKc5WmpD2oIC7bA79q2li3OONMW0gZUZsmSf7rWYTPDamKfttV8tzo7FlUBcGkGRxfwcVfz5oy3fWKK5yyQ4T4H5y8gjtQK-GYy1DKD_7bVbQYjr2t");

                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest); /// 12:28
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddGroup.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetViews() {
        mGroupTitle.setText(null);
        mGroupTitle.setHint("Titre du groupe");
        mGroupDescription.setText(null);
        mGroupDescription.setHint("Description du groupe");
        mGroupAvatar.setImageResource(R.drawable.ic_action_members);
    }

    private void showGroupAvatarPickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionner une image:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0 :
                        if (!checkCameraPermissions()){
                            requestCameraPermissions();
                        }else{
                            pickFromCamera();
                        }
                        break;
                    case 1 :
                        if (!checkStoragePermissions()){
                            requestStoragePermissions();
                        }else{
                            pickFromGallery();
                        }
                        break;
                    default:
                }
            }
        });
        builder.create().show();
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermissions(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void initViews() {
        membreList = (ArrayList<String>) getIntent().getSerializableExtra("membreList");
        //Toast.makeText(AddGroup.this, "Nombre de membre : "+membreList.size()+ ". ID = "+membreList.get(1),Toast.LENGTH_SHORT).show();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Nouveau groupe");
        setSupportActionBar(toolbar);
        mGroupAvatar = findViewById(R.id.groupAvatarIv);
        mGroupTitle = findViewById(R.id.groupTitleEt);
        mGroupDescription = findViewById(R.id.groupDescriptionEt);
        mGroupIsTontine = findViewById(R.id.isTontineCb);
        mCreateGroup = findViewById(R.id.createGroupFab);

        requestQueue = Volley.newRequestQueue(this);

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void checkUser(){
        firebaseAuth =FirebaseAuth.getInstance();
        FirebaseUser fUser = firebaseAuth.getCurrentUser();
        if(fUser == null){
            startActivity(new Intent(AddGroup.this, MainActivity.class));
            finish();
        }else{
            myUID = fUser.getUid();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(AddGroup.this, "Please enable camera & storage permissions.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(AddGroup.this, "Please enable storage permissions.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                mGroupAvatar.setImageURI(image_uri);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                mGroupAvatar.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}