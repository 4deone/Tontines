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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm.deone.corp.tontines.Contacts;
import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.Settings;

public class Chatlist extends Fragment {

    private String idUser;
    private RecyclerView mChatlistRv;
    private Toolbar mChatlistToolbar;
    private FloatingActionButton mChatlistFab;

    public Chatlist() {
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
        View view = inflater.inflate(R.layout.fragment_chatlist, container, false);
        checkUserStatut(view);
        mChatlistFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Contacts.class);
                intent.putExtra("REQUEST", "MESSAGES");
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
                    //controlTontine.searchTontines(getActivity(), mRecyclerView, idUser, query);
                }else {
                    //controlTontine.allTontines(getActivity(), mRecyclerView, idUser);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    //controlTontine.searchTontines(getActivity(), mRecyclerView, idUser, newText);
                }else {
                    //controlTontine.allTontines(getActivity(), mRecyclerView, idUser);
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

        }
    }

    private void initViews(View view) {
        mChatlistToolbar = view.findViewById(R.id.chatlistToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mChatlistToolbar);
        mChatlistRv = view.findViewById(R.id.chatlistRv);
        mChatlistFab = view.findViewById(R.id.chatlistFab);
    }

}