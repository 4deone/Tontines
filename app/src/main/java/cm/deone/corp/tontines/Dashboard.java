package cm.deone.corp.tontines;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import cm.deone.corp.tontines.models.Tontine;
import cm.deone.corp.tontines.models.User;

public class Dashboard extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private String idUser;
    private Tontine tontine;

    private FloatingActionButton mFloatingActionButtonab;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        checkUser();
        initializeUI();
        tontine.allTontines(this, mRecyclerView);
        mFloatingActionButtonab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(idUser);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.isActiveUser()){
                            Intent intent = new Intent(Dashboard.this, AddTontine.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(Dashboard.this, "Denied operation!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.settings:
                //Lancer la page Settings
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkUser(){
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser==null){
            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            idUser = mUser.getUid();
        }
    }

    private void initializeUI() {
        tontine = new Tontine();
        mRecyclerView = findViewById(R.id.recycleTontine);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mFloatingActionButtonab = findViewById(R.id.faButton);
    }
}