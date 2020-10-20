package cm.deone.corp.tontines;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import cm.deone.corp.tontines.interfaces.IntSystem;

public class SettingsActivity extends AppCompatActivity implements IntSystem {

    private static final String TOPIC_TONTINE_NOTIFICATION = "TONTINE";
    private static final String TOPIC_CHAT_NOTIFICATION = "CHAT";
    private static final String TOPIC_ARTICLE_NOTIFICATION = "ARTICLE";

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;

    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private FirebaseUser mUser;
    private ImageView avatar;
    private ImageView editProfil;
    private TextView userName;
    private TextView userCni;
    private TextView userPhone;
    private TextView userEmail;
    private TextView userCity;
    private ProgressDialog pd;
    private SwitchCompat postNotificationSw;
    private SwitchCompat enableNotificationChat;
    private SwitchCompat enableNotificationArticle;
    private String idUser;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_one);
        checkUser();
        initializeViews();
        getUserInformations();
        boolean isTontineEnable = sharedPreferences.getBoolean(""+TOPIC_TONTINE_NOTIFICATION, false);
        boolean isChatEnable = sharedPreferences.getBoolean(""+TOPIC_CHAT_NOTIFICATION, false);
        boolean isArticleEnable = sharedPreferences.getBoolean(""+TOPIC_ARTICLE_NOTIFICATION, false);
        if (isTontineEnable) {
            postNotificationSw.setChecked(true);
        }else{
            postNotificationSw.setChecked(false);
        }
        if (isChatEnable) {
            enableNotificationChat.setChecked(true);
        }else{
            enableNotificationChat.setChecked(false);
        }
        if (isArticleEnable) {
            enableNotificationArticle.setChecked(true);
        }else{
            enableNotificationArticle.setChecked(false);
        }
        postNotificationSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                editor.putBoolean(""+TOPIC_TONTINE_NOTIFICATION, isChecked);
                editor.apply();
                if (isChecked){
                    suscribeNotification();
                }else{
                    unsuscribeNotification();
                }
            }
        });
        enableNotificationChat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                editor.putBoolean(""+TOPIC_CHAT_NOTIFICATION, isChecked);
                editor.apply();
                if (isChecked){
                    suscribeNotificationChat();
                }else{
                    unsuscribeNotificationChat();
                }
            }
        });
        enableNotificationArticle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor = sharedPreferences.edit();
                editor.putBoolean(""+TOPIC_ARTICLE_NOTIFICATION, isChecked);
                editor.apply();
                if (isChecked){
                    suscribeNotificationArticle();
                }else{
                    unsuscribeNotificationArticle();
                }
            }
        });
        editProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialogProfil();
            }
        });
    }

    private void unsuscribeNotificationArticle() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_ARTICLE_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive article notifications";
                        if(!task.isSuccessful()){
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void suscribeNotificationArticle() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_ARTICLE_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive article notifications";
                        if(!task.isSuccessful()){
                            msg = "Unsubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsuscribeNotificationChat() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_CHAT_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive chat notifications";
                        if(!task.isSuccessful()){
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void suscribeNotificationChat() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_CHAT_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive chat notifications";
                        if(!task.isSuccessful()){
                            msg = "Unsubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void suscribeNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_TONTINE_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will not receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "Unsubscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void unsuscribeNotification() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_TONTINE_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "You will receive post notifications";
                        if(!task.isSuccessful()){
                            msg = "Subscription failed";
                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditDialogProfil() {
        String[] options = {"Edit avatar", "Edit full name", "Edit phone", "Edit CNI", "Edit ville"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionner une option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0 :
                        pd.setMessage("Mise à jour de votre avatar");
                        showAvatarDialog();
                        break;
                    case 1 :
                        pd.setMessage("Mise à jour de votre nom");
                        //showNameDialog();
                        break;
                    case 2 :
                        pd.setMessage("Mise à jour de votre téléphone");
                        break;
                    case 3 :
                        pd.setMessage("Mise à jour de votre cCNI");
                        break;
                    case 4 :
                        pd.setMessage("Mise à jour de votre ville");
                        break;
                    default:
                }
            }
        });
        builder.create().show();
    }

    private void showAvatarDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionner une image à partir");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which){
                    case 0 :
                        break;
                    case 1 :
                        break;
                    default:
                }
            }
        });
        builder.create().show();
    }

    private void getUserInformations(){
        Query query = reference.orderByChild("emailUser").equalTo(mUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String name = ds.child("nameUser").getValue(String.class);
                    String cni = ds.child("cniUser").getValue(String.class);
                    String delivery = ds.child("deliveryCniUser").getValue(String.class);
                    String phone = ds.child("phoneUser").getValue(String.class);
                    String email = ds.child("emailUser").getValue(String.class);
                    String city = ds.child("villeUser").getValue(String.class);
                    String image = ds.child("avatarUser").getValue(String.class);
                    userName.setText(name);
                    userCni.setText(getResources().getString(R.string.formatCNI, cni, delivery));
                    userPhone.setText(phone);
                    userEmail.setText(email);
                    userCity.setText(city);
                    try {
                        Picasso.get().load(image).into(avatar);
                    }catch(Exception e){
                        Picasso.get().load(image).placeholder(R.drawable.ic_action_cover).into(avatar);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void checkUser() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            idUser = mUser.getUid();
        }
    }

    @Override
    public void initializeViews() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");
        postNotificationSw = findViewById(R.id.enableNotificationSw);
        enableNotificationChat = findViewById(R.id.enableNotificationChat);
        enableNotificationArticle = findViewById(R.id.enableNotificationArticle);
        userCity = findViewById(R.id.villeTv);
        userEmail = findViewById(R.id.emailUserTv);
        userCni = findViewById(R.id.cniTv);
        userPhone = findViewById(R.id.telephoneTv);
        userName = findViewById(R.id.usernameTv);
        editProfil = findViewById(R.id.editProfilIv);
        avatar = findViewById(R.id.avatarUserIv);
        sharedPreferences = getSharedPreferences("Notification_TT", MODE_PRIVATE);
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(SettingsActivity.this, "Please enable camera & storage permissions.",
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
                        Toast.makeText(SettingsActivity.this, "Please enable storage permissions.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                //uploadProfileCoverPhoto(image_uri);
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*private void uploadProfileCoverPhoto(Uri uri) {
        progressDialog.show();
        String filePathAndName = storagePath+ ""+ profileOrCoverPhoto +"_"+ user.getUid();

        StorageReference storageReference1 = storageReference.child(filePathAndName);
        storageReference1.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                final Uri downloadUri = uriTask.getResult();

                if (uriTask.isSuccessful()){
                    HashMap<String, Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto, downloadUri.toString());

                    reference.child(user.getUid()).updateChildren(results).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Images Updated...", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, "Error Updating Images...", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (profileOrCoverPhoto.equals("image")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("posts");
                        Query query = ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    String child = ds.getKey();
                                    assert child != null;
                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //Update udp in current users comment on post  Voir video 0:12:32
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    String child = ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1 = ""+dataSnapshot.child(child).getKey();
                                        Query child2 = FirebaseDatabase.getInstance().getReference("Posts").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds:dataSnapshot.getChildren()){
                                                    String child = ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SettingsActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(SettingsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }*/

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description");
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

}