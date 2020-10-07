package cm.deone.corp.tontines.controler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Objects;

import cm.deone.corp.tontines.Dashboard;
import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.Profil;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntMedia;
import cm.deone.corp.tontines.interfaces.IntUser;
import cm.deone.corp.tontines.models.User;

public class ControlUser implements IntUser, IntMedia {
    private static  ControlUser instance = null;
    private User user;

    /**
     *
     */
    private ControlUser(){
        super();
    }

    /**
     *
     * @return
     */
    public static final ControlUser getInstance(){
        if(ControlUser.instance == null){
            ControlUser.instance = new ControlUser();
        }
        return ControlUser.instance;
    }

    /**
     *
     * @param idUser Chaine de caratère indiquant l'identifiant de l'utilisateur
     * @param nameUser
     * @param avatarUser
     * @param coverUser
     * @param phoneUser
     * @param emailUser
     * @param cniUser
     * @param deliveryCniUser
     * @param villeUser
     * @param dateCreationUser
     * @param activeUser
     */
    public void createNewUser(String idUser, String nameUser, String avatarUser, String coverUser,
                              String phoneUser, String emailUser, String cniUser, String deliveryCniUser,
                              String villeUser, String dateCreationUser, String ruleUser, boolean activeUser){
        user = new User(idUser, nameUser, avatarUser, coverUser, phoneUser, emailUser, cniUser, deliveryCniUser, villeUser, dateCreationUser, ruleUser, activeUser);
    }

    public void createNewUser(String emailUser){
        user = new User(emailUser);
    }

    @Override
    public void signUser(final Activity activity, final ProgressBar mProgressBar, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmailUser(), password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(activity, "Registration successful!", Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(activity, Profil.class);
                            activity.startActivity(intent);
                            activity.finish();
                        }else {
                            Toast.makeText(activity, "Registration failed! Please try again later", Toast.LENGTH_LONG).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void recoverUserPassword() {

    }

    @Override
    public void addUser(final Activity activity, final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, Object> hashNewUser = new HashMap<>();
        hashNewUser.put("idUser", user.getIdUser());
        hashNewUser.put("nameUser", user.getNameUser());
        hashNewUser.put("avatarUser", user.getAvatarUser());
        hashNewUser.put("coverUser", user.getCoverUser());
        hashNewUser.put("phoneUser", user.getPhoneUser());
        hashNewUser.put("emailUser", user.getEmailUser());
        hashNewUser.put("villeUser", user.getVilleUser());
        hashNewUser.put("ruleUser", user.getRuleUser());

        if (!TextUtils.isEmpty(user.getCniUser())) {
            hashNewUser.put("cniUser", user.getCniUser());
            if (!TextUtils.isEmpty(user.getDeliveryCniUser())) {
                hashNewUser.put("deliveryCniUser", user.getDeliveryCniUser());
            }else {return;}
            hashNewUser.put("activeUser", true);
        }else {
            hashNewUser.put("activeUser", false);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(user.getIdUser()).setValue(hashNewUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        activity.startActivity(new Intent(activity, Dashboard.class));
                        activity.finish();
                    }
                })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(activity, "Please enter city...", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void allUsers(Activity activity, RecyclerView recyclerview, String uID) {

    }

    @Override
    public void searchUsers(Activity activity, RecyclerView recyclerview, String uID, String searchQuery) {

    }

    @Override
    public void freezeUser() {

    }

    @Override
    public void deleteUser() {

    }

    @Override
    public void readPicture(ImageView imageView, String path) {
        try{
            Picasso.get().load(path).into(imageView);
        }catch(Exception e){
            Picasso.get().load(path).placeholder(R.drawable.ic_action_cover).into(imageView);
        }
    }

    @Override
    public void savePicture(final Activity activity, final ProgressDialog progressDialog, ImageView imageView, String type) {
        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "User/" + type +"_" + timeStamp;

        if (imageView.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            storageReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());

                    String downloadUri = Objects.requireNonNull(uriTask.getResult()).toString();
                    if (uriTask.isSuccessful()){
                        /*if (typeBien.equals("Terrain")){
                            uploadData(new Post(timeStamp, downloadUri, "0", operation, typeBien,
                                    lieudit, quartier, ville, new Budget(budget, "0"), new Superficie(superficie, "0"),
                                    "0", mProprietaire, mStatus, description, timeStamp,
                                    "0", "0", "0", mUser.getUid(), mUser.getName(), mUser.getImage()));
                        }else {
                            uploadData(new Post(timeStamp, downloadUri, "0", operation, typeBien,
                                    lieudit, quartier, ville, new Budget(budget, "0"), new Pieces("0", "0"),
                                    "noSecurity", "0", "noStanding", "noChauffage", "0",
                                    mProprietaire, mStatus, description, timeStamp, "0",
                                    "0", "0", mUser.getUid(), mUser.getName(), mUser.getImage()));
                        }*/
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            progressDialog.dismiss();
            Toast.makeText(activity, "", Toast.LENGTH_SHORT).show();
        }
    }
}
