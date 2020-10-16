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

import cm.deone.corp.tontines.ChatActivity;
import cm.deone.corp.tontines.Contacts;
import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.adapters.AdapterChatLists;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Chat;
import cm.deone.corp.tontines.models.Chatlist;
import cm.deone.corp.tontines.models.User;

public class ChatlistFrag extends Fragment {

    private String myUID;
    private RecyclerView mChatlistRv;
    private AdapterChatLists adapterChatLists;
    private List<User> userList;
    private List<Chatlist> chatlists;

    public ChatlistFrag() {
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
        checkUserStatut();
        initViews(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.nav_chat, menu);
        menu.findItem(R.id.menu_action_edit_contact).setVisible(false);
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
        Toolbar mChatlistToolbar = view.findViewById(R.id.chatlistToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mChatlistToolbar);
        FloatingActionButton mChatlistFab = view.findViewById(R.id.chatlistFab);
        mChatlistRv = view.findViewById(R.id.chatlistRv);
        chatlists = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        mChatlistRv.setHasFixedSize(true);
        mChatlistRv.setLayoutManager(linearLayoutManager);

        mChatlistFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Contacts.class);
                intent.putExtra("REQUEST", "MESSAGES");
                startActivity(intent);
            }
        });

        DatabaseReference refChatList = FirebaseDatabase.getInstance().getReference("Chatlist").child(myUID);
        refChatList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatlists.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    Chatlist chatlist = ds.getValue(Chatlist.class);
                    chatlists.add(chatlist);
                }
                loadsChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadsChats() {
        userList = new ArrayList<>();
        DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference("Users");
        refUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds:snapshot.getChildren()){
                    User user = ds.getValue(User.class);
                    for (Chatlist chatlist: chatlists){
                        if (user.getIdUser() != null && user.getIdUser().equals(chatlist.getId())){
                            userList.add(user);
                            break;
                        }
                    }
                    adapterChatLists = new AdapterChatLists(getActivity(), userList);
                    mChatlistRv.setAdapter(adapterChatLists);
                }
                adapterChatLists.setOnItemClickListener(new IntRvClickListner() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("hisUID", userList.get(position).getIdUser());
                        getActivity().startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                });
                // set Last message
                for (int i=0; i<userList.size(); i++){
                    lastmessage(userList.get(i).getIdUser());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastmessage(final String idUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String theLastMessage = "default";
                for(DataSnapshot ds:snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if (chat==null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String receiver = chat.getReceiver();
                    if (sender == null || receiver == null){
                        continue;
                    }
                    if (chat.getReceiver().equals(myUID) && chat.getSender().equals(idUser)
                    || chat.getReceiver().equals(idUser) && chat.getSender().equals(myUID)){
                        theLastMessage = chat.getMessage();
                    }
                }
                adapterChatLists.setLastMessageMap(idUser, theLastMessage);
                adapterChatLists.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}