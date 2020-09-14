package cm.deone.corp.tontines.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cm.deone.corp.tontines.R;

public class StatusTontine extends Fragment {

    private String idTontine;
    private RecyclerView rvTontineMembers;
    private FloatingActionButton fabAddMembre;

    public StatusTontine() {
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
        //initViews(view);
        return view;
    }
}