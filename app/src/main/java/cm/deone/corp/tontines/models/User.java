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

    public void createUser(final Activity activity, FirebaseDatabase database){
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("idUser", idUser);
        hashMap.put("nameUser", nameUser);
        hashMap.put("phoneUser", phoneUser);
        hashMap.put("emailUser", emailUser);
        hashMap.put("villeUser", villeUser);

        if (!TextUtils.isEmpty(cniUser)) {
            hashMap.put("cniUser", cniUser);
            if (!TextUtils.isEmpty(deliveryCniUser)) {
                hashMap.put("deliveryCniUser", deliveryCniUser);
            }else {return;}
            hashMap.put("activeUser", true);
        }else {
            hashMap.put("activeUser", false);
        }

        DatabaseReference reference = database.getReference("Users");
        reference.child(idUser).setValue(hashMap)
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

    public void createTontine(final Activity activity, final HashMap<String, Object> hashMapDescription, final HashMap<String, Object> hashMapBureau, final HashMap<String, Object>hashMapMembres, final String idTontine, final String idUtilisateur){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tontines");
        reference.child(idTontine).child("Description").setValue(hashMapDescription)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference referenceBureau = FirebaseDatabase.getInstance().getReference("Tontines");
                        referenceBureau.child(idTontine).child("Bureau").setValue(hashMapBureau)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        DatabaseReference referenceMembres = FirebaseDatabase.getInstance().getReference("Tontines");
                                        referenceMembres.child(idTontine).child("Membres").setValue(hashMapMembres)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        activity.startActivity(new Intent(activity, Dashboard.class));
                                                        activity.finish();

                                                    }
                                                })  .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, "Error not saved nav_tontine...", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                    }
                                })  .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Error not saved nav_tontine...", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                })  .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error not saved nav_tontine...", Toast.LENGTH_LONG).show();
            }
        });
    }

}
