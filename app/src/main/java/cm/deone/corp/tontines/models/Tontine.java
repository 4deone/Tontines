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
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.interfaces.IntTontine;
import cm.deone.corp.tontines.tontine.ShowTontine;

public class Tontine {

    private String idTontine;
    private String nameTontine;
    private String coverTontine;
    private String deviseTontine;
    private String descriptionTontine;
    private String dateCreationTontine;
    private boolean statusTontine; // true = public --- false = privé
    private boolean activeTontine;

    public Tontine() {
    }

    public Tontine(String idTontine, String nameTontine, String coverTontine, String deviseTontine,
                   String descriptionTontine, String dateCreationTontine, boolean statusTontine, boolean activeTontine) {
        this.idTontine = idTontine;
        this.nameTontine = nameTontine;
        this.coverTontine = coverTontine;
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

    public String getCoverTontine() {
        return coverTontine;
    }

    public void setCoverTontine(String coverTontine) {
        this.coverTontine = coverTontine;
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
}
