package cm.deone.corp.tontines.controler;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
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
import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.ShowTontine;

public final class ControlTontine implements IntTontine {
    private static  ControlTontine instance = null;
    private Tontine tontine;

    /**
     *
     */
    private ControlTontine(){
        super();
    }

    /**
     *
     * @return
     */
    public static final ControlTontine getInstance(){
        if(ControlTontine.instance == null){
            ControlTontine.instance = new ControlTontine();
        }
        return ControlTontine.instance;
    }

    /**
     *
     * @param idTontine
     * @param nameTontine
     * @param coverTontine
     * @param deviseTontine
     * @param descriptionTontine
     * @param dateCreationTontine
     * @param statusTontine
     * @param activeTontine
     */
     public void createNewTontine(String idTontine, String nameTontine, String coverTontine,
                          String deviseTontine, String descriptionTontine,
                          String dateCreationTontine, boolean statusTontine, boolean activeTontine){
        tontine = new Tontine(idTontine, nameTontine, coverTontine, deviseTontine, descriptionTontine, dateCreationTontine, statusTontine, activeTontine);
     }

    /**
     *
     * @param activity
     * @param idUser
     * @param name
     * @param phone
     */
    @Override
    public void addTontine(final Activity activity, final ProgressBar progressBar, final String idUser, String name, String phone) {
        HashMap<String, Object> hashMapDescription = new HashMap<>();
        hashMapDescription.put("idTontine", tontine.getIdTontine());
        hashMapDescription.put("nameTontine", tontine.getNameTontine());
        hashMapDescription.put("deviseTontine", tontine.getDeviseTontine());
        hashMapDescription.put("descriptionTontine", tontine.getDescriptionTontine());
        hashMapDescription.put("dateCreationTontine", tontine.getIdTontine());
        hashMapDescription.put("statusTontine", tontine.isStatusTontine());
        hashMapDescription.put("activeTontine", tontine.isActiveTontine());
        final HashMap<String, Object> hashMapMembres = new HashMap<>();
        hashMapMembres.put("date", tontine.getIdTontine());
        hashMapMembres.put("bureau", "fondateur");
        hashMapMembres.put("name", name);
        hashMapMembres.put("phone", phone);
        hashMapMembres.put("rule", "1111-1111-1111-1111"); //tontine-membre-operation-reglement

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
        reference.child(tontine.getIdTontine()).child(activity.getResources().getString(R.string.Description)).setValue(hashMapDescription)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DatabaseReference referenceMembres = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
                        referenceMembres.child(tontine.getIdTontine()).child(activity.getResources().getString(R.string.Membres)).child(idUser).setValue(hashMapMembres)
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

    /**
     *
     * @param activity
     * @param recyclerview
     * @param uID
     */
    @Override
    public void allTontines(final Activity activity, final RecyclerView recyclerview, final String uID) {
        final List<Tontine> tontineList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontineList.clear();
                for (final DataSnapshot ds: dataSnapshot.getChildren()){
                    Tontine mTontine = ds.child(activity.getResources().getString(R.string.Description)).getValue(Tontine.class);
                    //String idMembre = ds.child(activity.getResources().getString(R.string.Membres)).child(uID).getKey();
                    assert mTontine != null;
                    //assert idMembre != null;
                    if (mTontine.isActiveTontine()&& ds.child(activity.getResources().getString(R.string.Membres)).child(uID).exists()){
                        tontineList.add(mTontine);
                    }
                    AdapterTontines adapterTontines = new AdapterTontines(activity, tontineList);
                    recyclerview.setAdapter(adapterTontines);
                    adapterTontines.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(activity, ShowTontine.class);
                            intent.putExtra("idTontine", tontineList.get(position).getIdTontine());
                            intent.putExtra("mRole", ds.child(activity.getResources().getString(R.string.Membres)).child(uID).child("bureau").getValue(String.class));
                            activity.startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(int position) {
                            Toast.makeText(activity, ""+tontineList.get(position).getDescriptionTontine(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, ""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *
     * @param activity
     * @param recyclerview
     * @param uID
     * @param searchQuery
     */
    @Override
    public void searchTontines(final Activity activity, final RecyclerView recyclerview, final String uID, final String searchQuery) {
        final List<Tontine> tontineList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(activity.getResources().getString(R.string.Tontines));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontineList.clear();
                for (final DataSnapshot ds:dataSnapshot.getChildren()){
                    Tontine mTontine = ds.child(activity.getResources().getString(R.string.Description)).getValue(Tontine.class);
                    String idMembre = ds.child(activity.getResources().getString(R.string.Membres)).getKey();
                    assert mTontine != null;
                    assert idMembre != null;
                    if ((mTontine.getNameTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            mTontine.getDeviseTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            mTontine.getDescriptionTontine().toLowerCase().contains(searchQuery.toLowerCase())) &&
                            ds.child(activity.getResources().getString(R.string.Membres)).child(uID).exists() &&
                            mTontine.isActiveTontine()){
                        tontineList.add(mTontine);
                    }
                    AdapterTontines adapterTontines = new AdapterTontines(activity, tontineList);
                    recyclerview.setAdapter(adapterTontines);
                    adapterTontines.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(activity, ShowTontine.class);
                            intent.putExtra("idTontine", tontineList.get(position).getIdTontine());
                            intent.putExtra("mRole", ds.child(activity.getResources().getString(R.string.Membres)).child(uID).child("bureau").getValue(String.class));
                            activity.startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(int position) {
                            Toast.makeText(activity, ""+tontineList.get(position).getDescriptionTontine(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(activity, ""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void freezeTontine() {

    }

    @Override
    public void deleteTontine() {

    }
}
