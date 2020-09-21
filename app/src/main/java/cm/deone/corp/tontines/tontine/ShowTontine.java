package cm.deone.corp.tontines.tontine;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.fragments.HomeTontine;
import cm.deone.corp.tontines.fragments.MembresTontine;

public class ShowTontine extends AppCompatActivity {

    private String idTontine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tontine);
        Intent intent = getIntent();
        idTontine = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        checkUserStatus();
    }

    private void checkUserStatus(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null){
            initViews();
        }else {
            startActivity(new Intent(ShowTontine.this, MainActivity.class));
            finish();
        }
    }

    private void initViews() {
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        Bundle bundle1 = new Bundle();
        bundle1.putString("idTontine", idTontine);
        HomeTontine homeTontine = new HomeTontine();
        homeTontine.setArguments(bundle1);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_fragment, homeTontine, "");
        fragmentTransaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.description:
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("idTontine", idTontine);
                    HomeTontine homeTontine = new HomeTontine();
                    homeTontine.setArguments(bundle1);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_fragment, homeTontine, "");
                    fragmentTransaction.commit();
                    return true;
                case R.id.membres:
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("idTontine", idTontine);
                    MembresTontine membresTontine = new MembresTontine();
                    membresTontine.setArguments(bundle2);
                    FragmentTransaction fragmentTransactionOne = getSupportFragmentManager().beginTransaction();
                    fragmentTransactionOne.replace(R.id.content_fragment, membresTontine, "");
                    fragmentTransactionOne.commit();
                    return true;
                default:
            }
            return false;
        }
    };
}