package cm.deone.corp.tontines.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cm.deone.corp.tontines.Contacts;
import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.adapters.AdapterMembres;
import cm.deone.corp.tontines.adapters.AdapterTontines;
import cm.deone.corp.tontines.models.Membre;
import cm.deone.corp.tontines.models.Tontine;

public class MembresTontine extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Toolbar membresToolbar;
    private String idUser;
    private String idTontine;
    private RecyclerView rvTontineMembers;
    private FloatingActionButton fabAddMembre;
    private TextView tvNoMembers;

    List<Membre> membreList;

    public MembresTontine() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idTontine = getArguments().getString("idTontine");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_membres_tontine, container, false);
        checkUser(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.membres_tontine, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchMembres(query);
                }else {
                    allMembres();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchMembres(newText);
                }else {
                    allMembres();
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void searchMembres(final String searchQuery) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines").child(idTontine).child("Membres");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membreList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Membre membre = ds.getValue(Membre.class);
                    assert membre != null;
                    if (membre.getBureau().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            membre.getName().toLowerCase().contains(searchQuery.toLowerCase())){
                        membreList.add(membre);
                    }
                    rvTontineMembers.setAdapter(new AdapterMembres(getActivity(), membreList));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item clicks
        /*int id = item.getItemId();

        if (id == R.id.menu_action_reglement) {
            //do your function here
            Toast.makeText(getActivity(), "Reglement", Toast.LENGTH_SHORT).show();
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void checkUser(View view){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            idUser = mUser.getUid();
            initViews(view);
            getTontine();
            allMembres();
        }
    }

    private void allMembres() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines").child(idTontine).child("Membres");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membreList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Membre membre = ds.getValue(Membre.class);
                    membreList.add(membre);
                    if (membreList.isEmpty()) {
                        tvNoMembers.setVisibility(View.VISIBLE);
                        rvTontineMembers.setVisibility(View.GONE);
                    }else{
                        tvNoMembers.setVisibility(View.GONE);
                        rvTontineMembers.setVisibility(View.VISIBLE);
                        rvTontineMembers.setAdapter(new AdapterMembres(getActivity(), membreList));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getTontine() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = "";
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Tontine tontine = ds.child("Description").getValue(Tontine.class);
                    assert tontine != null;
                    if (tontine.getIdTontine().equals(idTontine)){
                        title = tontine.getNameTontine();
                        break;
                    }
                }
                membresToolbar.setTitle(title);
                membresToolbar.setSubtitle("Liste des membres");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view){
        membresToolbar = view.findViewById(R.id.membresToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(membresToolbar);
        membreList = new ArrayList<>();
        tvNoMembers = view.findViewById(R.id.tvNoMembers);
        rvTontineMembers = view.findViewById(R.id.recycleTontineMembers);
        rvTontineMembers.setHasFixedSize(true);
        rvTontineMembers.setLayoutManager(new LinearLayoutManager(getActivity()));
        fabAddMembre = view.findViewById(R.id.fabAddMember);
        fabAddMembre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Contacts.class));
            }
        });
    }
}