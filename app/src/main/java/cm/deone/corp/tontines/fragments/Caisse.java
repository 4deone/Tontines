package cm.deone.corp.tontines.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;

public class Caisse extends Fragment {

    private String idUser;
    private String idTontine;

    public Caisse() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idTontine = getArguments().getString("idTontine");
        }else {
            Toast.makeText(getActivity(), "Argument null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_caisse, container, false);
        checkUserStatut(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.home_tontine, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item clicks
        int id = item.getItemId();

        if (id == R.id.menu_action_bureau) {
            //do your function here
            Toast.makeText(getActivity(), "Bureau", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.menu_action_settings) {
            //do your function here
            Toast.makeText(getActivity(), "SettingsFragment", Toast.LENGTH_SHORT).show();
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
        }
    }

    private void initViews(View view) {
        /*homeToolbar = view.findViewById(R.id.homeToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(homeToolbar);
        tv = view.findViewById(R.id.tvTest);*/
    }
}