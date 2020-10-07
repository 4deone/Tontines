package cm.deone.corp.tontines;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cm.deone.corp.tontines.fragments.Status;
import cm.deone.corp.tontines.fragments.Reglement;
import cm.deone.corp.tontines.fragments.Caisse;
import cm.deone.corp.tontines.fragments.Home;
import cm.deone.corp.tontines.fragments.Membres;

public class ShowTontine extends AppCompatActivity {

    private String idTontine;
    private String mRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tontine);
        Intent intent = getIntent();
        idTontine = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        mRole = ""+intent.getStringExtra("mRole");
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
        navigationView.getMenu().clear(); //clear old inflated items.
        if(mRole.equals("membre")){
            navigationView.inflateMenu(R.menu.nav_tontine_user);
        }else{
            navigationView.inflateMenu(R.menu.nav_tontine);
        }
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        pushFragment(new Home());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.description:
                    pushFragment(new Home());
                    return true;
                case R.id.membres:
                    pushFragment(new Membres());
                    return true;
                case R.id.caisse:
                    pushFragment(new Caisse());
                    return true;
                case R.id.status:
                    pushFragment(new Status());
                    return true;
                case R.id.reglement:
                    pushFragment(new Reglement());
                    return true;
                default:
            }
            return false;
        }
    };

    protected void pushFragment(Fragment fragment){
        if (fragment == null)
            return;

        Bundle bundle = new Bundle();
        bundle.putString("idTontine", idTontine);
        fragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_fragment, fragment, "");
        ft.commit();
    }

}