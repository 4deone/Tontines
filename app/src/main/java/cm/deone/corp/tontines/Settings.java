package cm.deone.corp.tontines;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import androidx.annotation.Nullable;

import cm.deone.corp.tontines.fragments.SettingsFragment;

public class Settings extends PreferenceActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }
}