package cm.deone.corp.tontines.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cm.deone.corp.tontines.Contacts;
import cm.deone.corp.tontines.GroupChatActivity;
import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.adapters.AdapterGroupLists;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.GroupChatList;

public class GroupList extends Fragment {

    private String myUID;
    private RecyclerView grouplistRv;
    private AdapterGroupLists adapterGroupLists;
    private ArrayList<GroupChatList> groupChatLists;

    public GroupList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        checkUserStatut();
        initViews(view);
        loadGroupChatsList();
        return view;
    }

    private void checkUserStatut() {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else{
            myUID = mUser.getUid();
        }
    }

    private void initViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        //FloatingActionButton grouplistFab = view.findViewById(R.id.grouplistFab);
        grouplistRv = view.findViewById(R.id.grouplistRv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        grouplistRv.setHasFixedSize(true);
        grouplistRv.setLayoutManager(linearLayoutManager);
    }

    private void loadGroupChatsList() {
        groupChatLists = new ArrayList<>();
        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("Groupes");
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    if (ds.child("Participants").child(myUID).exists()){
                        GroupChatList groupChatList = ds.getValue(GroupChatList.class);
                        groupChatLists.add(groupChatList);
                    }
                    adapterGroupLists = new AdapterGroupLists(getActivity(), groupChatLists);
                    grouplistRv.setAdapter(adapterGroupLists);
                    adapterGroupLists.setOnItemClickListener(new IntRvClickListner() {
                        @Override
                        public void onItemClick(View view, int position) {
                            Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                            intent.putExtra("gUID", groupChatLists.get(position).getGroupId());
                            startActivity(intent);
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // Show Group details
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChatsList(final String searchQuery) {
        groupChatLists = new ArrayList<>();
        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("Groupes");
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatLists.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    if (ds.child("Membres").child(myUID).exists()){
                        if (ds.child("groupTitle").toString().toLowerCase().contains(searchQuery.toLowerCase())){
                            GroupChatList groupChatList = ds.getValue(GroupChatList.class);
                            groupChatLists.add(groupChatList);
                        }
                    }
                    adapterGroupLists = new AdapterGroupLists(getActivity(), groupChatLists);
                    grouplistRv.setAdapter(adapterGroupLists);
                }
                adapterGroupLists.setOnItemClickListener(new IntRvClickListner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), GroupChatActivity.class);
                        intent.putExtra("gUID", groupChatLists.get(position).getGroupId());
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Show Group details
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchGroupChatsList(query);
                }else {
                    loadGroupChatsList();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchGroupChatsList(newText);
                }else {
                    loadGroupChatsList();
                }
                return false;
            }
        });
    }

    private void lastmessage(final String idUser) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.nav_chat, menu);
        menu.findItem(R.id.menu_action_edit_contact).setVisible(false);
        menu.findItem(R.id.menu_action_details_group).setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle menu item clicks
        int id = item.getItemId();

        if (id == R.id.menu_action_new_group) {
            Intent intent = new Intent(getActivity(), Contacts.class);
            intent.putExtra("REQUEST", "GROUPES");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}