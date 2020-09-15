package cm.deone.corp.tontines.models;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cm.deone.corp.tontines.Dashboard;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.adapters.AdapterTontines;

public class Tontine {

    private Activity activity;
    private String idTontine;
    private String nameTontine;
    private String deviseTontine;
    private String descriptionTontine;
    private String dateCreationTontine;
    private boolean statusTontine; // true = public --- false = privé
    private boolean activeTontine;

    public Tontine() {
    }

    public Tontine(Activity activity) {
        this.activity = activity;
    }

    public Tontine(String idTontine, String nameTontine, String deviseTontine, String descriptionTontine,
                   String dateCreationTontine, boolean statusTontine, boolean activeTontine) {
        this.idTontine = idTontine;
        this.nameTontine = nameTontine;
        this.deviseTontine = deviseTontine;
        this.descriptionTontine = descriptionTontine;
        this.dateCreationTontine = dateCreationTontine;
        this.statusTontine = statusTontine;
        this.activeTontine = activeTontine;
    }

    public boolean isStatusTontine() {
        return statusTontine;
    }

    public void setStatusTontine(boolean statusTontine) {
        this.statusTontine = statusTontine;
    }

    public String getDeviseTontine() {
        return deviseTontine;
    }

    public void setDeviseTontine(String deviseTontine) {
        this.deviseTontine = deviseTontine;
    }

    public String getIdTontine() {
        return idTontine;
    }

    public void setIdTontine(String idTontine) {
        this.idTontine = idTontine;
    }

    public String getNameTontine() {
        return nameTontine;
    }

    public void setNameTontine(String nameTontine) {
        this.nameTontine = nameTontine;
    }

    public String getDescriptionTontine() {
        return descriptionTontine;
    }

    public void setDescriptionTontine(String descriptionTontine) {
        this.descriptionTontine = descriptionTontine;
    }

    public String getDateCreationTontine() {
        return dateCreationTontine;
    }

    public void setDateCreationTontine(String dateCreationTontine) {
        this.dateCreationTontine = dateCreationTontine;
    }

    public boolean isActiveTontine() {
        return activeTontine;
    }

    public void setActiveTontine(boolean activeTontine) {
        this.activeTontine = activeTontine;
    }

    private void freezeTontine(){

    }

    private void deleteTontine(){

    }

    public void createTontine(final String idUtilisateur, String name){
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMapDescription = new HashMap<>();
        hashMapDescription.put("idTontine", timestamp);
        hashMapDescription.put("nameTontine", nameTontine);
        hashMapDescription.put("deviseTontine", deviseTontine);
        hashMapDescription.put("descriptionTontine", descriptionTontine);
        hashMapDescription.put("dateCreationTontine", timestamp);
        hashMapDescription.put("statusTontine", statusTontine);
        hashMapDescription.put("activeTontine", activeTontine);
        final HashMap<String, Object> hashMapMembres = new HashMap<>();
        hashMapMembres.put("date", timestamp);
        hashMapMembres.put("bureau", "fondateur");
        hashMapMembres.put("name", name);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
        reference.child(idTontine).child(activity.getResources().getString(R.string.Description)).setValue(hashMapDescription)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference referenceMembres = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
                        referenceMembres.child(idTontine).child(activity.getResources().getString(R.string.Membres)).child(idUtilisateur).setValue(hashMapMembres)
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

    public void allTontines(final RecyclerView recyclerview, final String uID){
        final List<Tontine> tontineList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontineList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Tontine tontine = ds.child(activity.getResources().getString(R.string.Description)).getValue(Tontine.class);
                    String idMembre = ds.child(activity.getResources().getString(R.string.Membres)).getKey();
                    assert tontine != null;
                    assert idMembre != null;
                    if (tontine.isActiveTontine()&&idMembre.equals(uID)){
                        tontineList.add(tontine);
                    }
                    recyclerview.setAdapter(new AdapterTontines(activity, tontineList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, ""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void searchTontines(final RecyclerView recyclerview, final String uID, final String searchQuery){

        final List<Tontine> tontines = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontines.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Tontine tontine = ds.child(activity.getResources().getString(R.string.Description)).getValue(Tontine.class);
                    String idMembre = ds.child(activity.getResources().getString(R.string.Membres)).getKey();
                    assert tontine != null;
                    assert idMembre != null;
                    if ((tontine.getNameTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            tontine.getDeviseTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            tontine.getDescriptionTontine().toLowerCase().contains(searchQuery.toLowerCase()))&&idMembre.equals(uID)){
                        tontines.add(tontine);
                    }
                    recyclerview.setAdapter(new AdapterTontines(activity, tontines));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, ""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
