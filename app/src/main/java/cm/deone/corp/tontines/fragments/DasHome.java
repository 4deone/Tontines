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
import cm.deone.corp.tontines.Settings;
import cm.deone.corp.tontines.ShowTontine;
import cm.deone.corp.tontines.adapters.AdapterTontines;
import cm.deone.corp.tontines.controler.ControlTontine;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;


public class DasHome extends Fragment {

    private FirebaseDatabase database;
    private String idUser;
    private FloatingActionButton mFabTontine;
    private RecyclerView mRecyclerView;
    private List<Tontine> tontineList;
    private ControlTontine controlTontine;
    private Toolbar mDasHomeTb;

    public DasHome() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashome, container, false);
        checkUserStatut(view);
        mFabTontine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Contacts.class);
                intent.putExtra("REQUEST", "TONTINES");
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.dashboard, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchTontines(query);
                }else {
                    allTontines();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchTontines(newText);
                }else {
                    allTontines();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item clicks
        int id = item.getItemId();

        if (id == R.id.menu_action_settings) {
            startActivity(new Intent(getActivity(), Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatut(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            idUser = mUser.getUid();
            initViews(view);
            showAllTontines();
        }
    }

    private void showAllTontines() {
        DatabaseReference ref = database.getReference(this.getResources().getString(R.string.Users)).child(idUser);
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                if (user.isActiveUser()) {
                    allTontines();
                    mFabTontine.setVisibility(View.VISIBLE);
                }else{
                    mFabTontine.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        mDasHomeTb = view.findViewById(R.id.dasboardToolbar);
        mDasHomeTb.setTitle("DashBoard");
        mDasHomeTb.setSubtitle("Mes tontines.");
        ((AppCompatActivity)getActivity()).setSupportActionBar(mDasHomeTb);

        database = FirebaseDatabase.getInstance();
        mRecyclerView = view.findViewById(R.id.recycleTontine);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mFabTontine = view.findViewById(R.id.faButton);
        this.controlTontine = ControlTontine.getInstance();
    }

    private void allTontines() {
        tontineList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontineList.clear();
                for (final DataSnapshot ds: dataSnapshot.getChildren()){
                    Tontine mTontine = ds.child("Description").getValue(Tontine.class);
                    //String idMembre = ds.child(activity.getResources().getString(R.string.Membres)).child(uID).getKey();
                    assert mTontine != null;
                    //assert idMembre != null;
                    if (mTontine.isActiveTontine()&& ds.child("Membres").child(idUser).exists()){
                        tontineList.add(mTontine);
                    }
                    AdapterTontines adapterTontines = new AdapterTontines(getActivity(), tontineList);
                    mRecyclerView.setAdapter(adapterTontines);
                    adapterTontines.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getActivity(), ShowTontine.class);
                            intent.putExtra("idTontine", tontineList.get(position).getIdTontine());
                            intent.putExtra("mRole", ds.child("Membres").child(idUser).child("bureau").getValue(String.class));
                            getActivity().startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            Toast.makeText(getActivity(), ""+tontineList.get(position).getDescriptionTontine(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchTontines(final String searchQuery) {
        tontineList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tontineList.clear();
                for (final DataSnapshot ds:dataSnapshot.getChildren()){
                    Tontine mTontine = ds.child("Description").getValue(Tontine.class);
                    String idMembre = ds.child("Membres").getKey();
                    assert mTontine != null;
                    assert idMembre != null;
                    if ((mTontine.getNameTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            mTontine.getDeviseTontine().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            mTontine.getDescriptionTontine().toLowerCase().contains(searchQuery.toLowerCase())) &&
                            ds.child(getActivity().getResources().getString(R.string.Membres)).child(idUser).exists() &&
                            mTontine.isActiveTontine()){
                        tontineList.add(mTontine);
                    }
                    AdapterTontines adapterTontines = new AdapterTontines(getActivity(), tontineList);
                    mRecyclerView.setAdapter(adapterTontines);
                    adapterTontines.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getActivity(), ShowTontine.class);
                            intent.putExtra("idTontine", tontineList.get(position).getIdTontine());
                            intent.putExtra("mRole", ds.child("Membres").child(idUser).child("bureau").getValue(String.class));
                            getActivity().startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            Toast.makeText(getActivity(), ""+tontineList.get(position).getDescriptionTontine(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}