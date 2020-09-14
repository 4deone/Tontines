package cm.deone.corp.tontines.models;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cm.deone.corp.tontines.adapters.AdapterTontines;

public class Tontine {
    private String idTontine;
    private String nameTontine;
    private String deviseTontine;
    private String descriptionTontine;
    private String dateCreationTontine;
    private boolean statusTontine; // true = public --- false = privé
    private boolean activeTontine;

    public Tontine() {
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

    private void createTontine(){

    }

    private void freezeTontine(){

    }

    private void deleteTontine(){

    }

    public void allTontines(final Activity activity, final RecyclerView recyclerview, final String uID){
        // L'objectif est d'afficher les tontines dont l'utilisateur est membre pour celle qui sont privé et toutes les nav_tontine public
        final List<Tontine> tontineList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontineList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Tontine tontine = ds.child("Description").getValue(Tontine.class);
                    assert tontine != null;
                    if (tontine.isActiveTontine()){
                        tontineList.add(tontine);
                    }
                    recyclerview.setAdapter(new AdapterTontines(activity, tontineList));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void searchTontines(final Activity activity, final RecyclerView recyclerview, final String uID, final String searchQuery){

        final List<Tontine> tontines = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontines.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Tontine tontine = ds.child("Description").getValue(Tontine.class);
                    assert tontine != null;
                    if (tontine.getNameTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            tontine.getDeviseTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            tontine.getDescriptionTontine().toLowerCase().contains(searchQuery.toLowerCase()) ){
                        tontines.add(tontine);
                    }

                    recyclerview.setAdapter(new AdapterTontines(activity, tontines));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, ""+databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
