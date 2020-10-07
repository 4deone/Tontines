package cm.deone.corp.tontines;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

import cm.deone.corp.tontines.controler.ControlTontine;
import cm.deone.corp.tontines.interfaces.IntSystem;
import cm.deone.corp.tontines.models.User;

public class Dashboard extends AppCompatActivity implements IntSystem {
    private static final int READ_CONTACT_REQUEST_CODE = 100;
    private FirebaseDatabase database;
    private String idUser;
    private FloatingActionButton mFabTontine;
    private RecyclerView mRecyclerView;
    private ControlTontine controlTontine;

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
    public void checkUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            idUser = mUser.getUid();
            checkPermission();
            initializeViews();
            showAllTontines();
        }
    }

    @Override
    public void initializeViews() {
        Toolbar dashboardToolbar = findViewById(R.id.dasboardToolbar);
        dashboardToolbar.setTitle("DashBoard");
        dashboardToolbar.setSubtitle("Mes tontines.");
        setSupportActionBar(dashboardToolbar);
        database = FirebaseDatabase.getInstance();
        mRecyclerView = findViewById(R.id.recycleTontine);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Dashboard.this));
        mFabTontine = findViewById(R.id.faButton);
        this.controlTontine = ControlTontine.getInstance();
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
            startActivity(new Intent(Dashboard.this, Settings.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CONTACT_REQUEST_CODE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // Showing the toast message
                Toast.makeText(Dashboard.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(Dashboard.this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    controlTontine.searchTontines(Dashboard.this, mRecyclerView, idUser, query);
                }else {
                    controlTontine.allTontines(Dashboard.this, mRecyclerView, idUser);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    controlTontine.searchTontines(Dashboard.this, mRecyclerView, idUser, newText);
                }else {
                    controlTontine.allTontines(Dashboard.this, mRecyclerView, idUser);
                }
                return false;
            }
        });
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(Dashboard.this, new String[] {Manifest.permission.READ_CONTACTS}, Dashboard.READ_CONTACT_REQUEST_CODE);
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
                    controlTontine.allTontines(Dashboard.this, mRecyclerView, idUser);
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



    private void retrievePreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

}