package cm.deone.corp.tontines.models;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import cm.deone.corp.tontines.Dashboard;

public class User {
    private Activity activity;

    private String idUser;
    private String nameUser;
    private String phoneUser;
    private String photoUser;
    private String emailUser;
    private String cniUser;
    private String deliveryCniUser;
    private String villeUser;
    private String dateCreationUser;
    private boolean activeUser;

    public User() {
    }

    public User(Activity activity) {
        this.activity = activity;
    }

    public User(String idUser) {
        this.idUser = idUser;
    }

    public User(String nameUser, String phoneUser) {
        this.nameUser = nameUser;
        this.phoneUser = phoneUser;
    }

    public User(String idUser, String nameUser, String phoneUser, String photoUser, String emailUser, String cniUser,
                String deliveryCniUser, String villeUser, String dateCreationUser, boolean activeUser) {
        this.idUser = idUser;
        this.nameUser = nameUser;
        this.phoneUser = phoneUser;
        this.photoUser = photoUser;
        this.emailUser = emailUser;
        this.cniUser = cniUser;
        this.deliveryCniUser = deliveryCniUser;
        this.villeUser = villeUser;
        this.dateCreationUser = dateCreationUser;
        this.activeUser = activeUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getPhotoUser() {
        return photoUser;
    }

    public void setPhotoUser(String photoUser) {
        this.photoUser = photoUser;
    }

    public String getCniUser() {
        return cniUser;
    }

    public void setCniUser(String cniUser) {
        this.cniUser = cniUser;
    }

    public String getDateCreationUser() {
        return dateCreationUser;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public void setDateCreationUser(String dateCreationUser) {
        this.dateCreationUser = dateCreationUser;
    }

    public boolean isActiveUser() {
        return activeUser;
    }

    public void setActiveUser(boolean activeUser) {
        this.activeUser = activeUser;
    }

    public String getDeliveryCniUser() {
        return deliveryCniUser;
    }

    public void setDeliveryCniUser(String deliveryCniUser) {
        this.deliveryCniUser = deliveryCniUser;
    }

    public String getVilleUser() {
        return villeUser;
    }

    public void setVilleUser(String villeUser) {
        this.villeUser = villeUser;
    }

    public void createUser(){
        HashMap<String, Object> hashNewUser = new HashMap<>();
        hashNewUser.put("idUser", idUser);
        hashNewUser.put("nameUser", nameUser);
        hashNewUser.put("phoneUser", phoneUser);
        hashNewUser.put("emailUser", emailUser);
        hashNewUser.put("villeUser", villeUser);

        if (!TextUtils.isEmpty(cniUser)) {
            hashNewUser.put("cniUser", cniUser);
            if (!TextUtils.isEmpty(deliveryCniUser)) {
                hashNewUser.put("deliveryCniUser", deliveryCniUser);
            }else {return;}
            hashNewUser.put("activeUser", true);
        }else {
            hashNewUser.put("activeUser", false);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child(idUser).setValue(hashNewUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                activity.startActivity(new Intent(activity, Dashboard.class));
                activity.finish();
            }
        })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Please enter city...", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void deleteUser(){

    }

    public void freezeUser(){

    }

    public void unFreezeUser(){

    }
}
