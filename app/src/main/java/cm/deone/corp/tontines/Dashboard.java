package cm.deone.corp.tontines;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import cm.deone.corp.tontines.fragments.ChatlistFrag;
import cm.deone.corp.tontines.fragments.DasHome;
import cm.deone.corp.tontines.fragments.GroupList;
import cm.deone.corp.tontines.notifications.Token;

public class Dashboard extends AppCompatActivity {

    private static final int READ_CONTACT_REQUEST_CODE = 100;
    private String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        checkUserStatus();
        initViews();
        checkPermission();
        UpdateToken();
    }

    @Override
    protected void onResume() { //// 22:15  sur la video
        checkUserStatus();
        super.onResume();
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

    private void checkUserStatus(){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null){
            myUID = mUser.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID", myUID);
            editor.apply();
        }else {
            startActivity(new Intent(Dashboard.this, MainActivity.class));
            finish();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(Dashboard.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(Dashboard.this, new String[] {Manifest.permission.READ_CONTACTS}, Dashboard.READ_CONTACT_REQUEST_CODE);
        }
    }

    private void initViews() {
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation_dashboard);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.nav_tontine_dashboard);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        pushFragment(new DasHome());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.HomeDashboard:
                    pushFragment(new DasHome());
                    return true;
                case R.id.forumDaoshboard:
                    pushFragment(new ChatlistFrag());
                    return true;
                case R.id.groupDaoshboard:
                    pushFragment(new GroupList());
                    return true;
                default:
            }
            return false;
        }
    };

    protected void pushFragment(Fragment fragment){
        if (fragment == null)
            return;

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_fragment_dashboard, fragment, "");
        ft.commit();
    }

    private void UpdateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
    }

}