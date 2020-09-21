package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
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

import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;
import cm.deone.corp.tontines.tontine.AddTontine;

public class Dashboard extends AppCompatActivity {

    private String idUser;
    private Tontine tontine;
    private FloatingActionButton mFabTontine;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        checkUser();
        mFabTontine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this, AddTontine.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_settings) {
            //Lancer la page Settings
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    tontine.searchTontines(mRecyclerView, idUser, query);
                }else {
                    tontine.allTontines(mRecyclerView, idUser);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    tontine.searchTontines(mRecyclerView, idUser, newText);
                }else {
                    tontine.allTontines(mRecyclerView, idUser);
                }
                return false;
            }
        });
    }

    private void initializeUI() {
        Toolbar dashboardToolbar = findViewById(R.id.dasboardToolbar);
        dashboardToolbar.setTitle("DashBoard");
        dashboardToolbar.setSubtitle("Mes tontines.");
        setSupportActionBar(dashboardToolbar);
        tontine = new Tontine(Dashboard.this);
        mRecyclerView = findViewById(R.id.recycleTontine);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
        mFabTontine = findViewById(R.id.faButton);

    }

    private void checkUser(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            initializeUI();
            idUser = mUser.getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(this.getResources().getString(R.string.Users)).child(idUser);
            ref.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    if (user.isActiveUser()) {
                        tontine.allTontines(mRecyclerView, idUser);
                        mFabTontine.setVisibility(View.VISIBLE);
                    }else{
                        mFabTontine.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Dashboard.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}