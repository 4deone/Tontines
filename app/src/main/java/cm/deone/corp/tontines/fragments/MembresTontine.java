package cm.deone.corp.tontines.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import cm.deone.corp.tontines.adapters.AdapterTontines;
import cm.deone.corp.tontines.models.Membre;
import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;

public class MembresTontine extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

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
            allMembres();
        }
    }

    private void allMembres() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tontines");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                membreList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    /*Tontine nav_tontine = ds.child("Membres").getValue(Tontine.class);
                    if (nav_tontine.isActiveTontine() && nav_tontine.getFondateurTontine().equals(uID)){
                        tontineList.add(nav_tontine);
                    }
                    rvTontineMembers.setAdapter(new AdapterTontines(activity, tontineList));*/

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews(View view){
        membreList = new ArrayList<>();
        tvNoMembers = view.findViewById(R.id.tvNoMembers);
        rvTontineMembers = view.findViewById(R.id.recycleTontineMembers);
        fabAddMembre = view.findViewById(R.id.fabAddMember);
        fabAddMembre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Contacts.class));
            }
        });
    }
}