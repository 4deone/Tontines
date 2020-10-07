package cm.deone.corp.tontines.fragments;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import cm.deone.corp.tontines.Dashboard;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.models.User;

public class SettingsFragment extends PreferenceFragment {


    private static final String PREF_NAME = "key_full_name";
    private static final String PREF_PHONE = "key_telephone";
    private static final String PREF_EMAIL = "key_email_address";
    private static final String PREF_CNI = "key_cni";
    private static final String PREF_VILLE= "key_ville";
    private static final String PREF_NOTIFICATION= "key_app_notification";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        getUserPreference(findPreference("key_full_name"));
        getUserPreference(findPreference("key_telephone"));
        getUserPreference(findPreference("key_email_address"));
        getUserPreference(findPreference("key_cni"));
        getUserPreference(findPreference("key_ville"));
        getUserPreference(findPreference("key_app_notification"));
//
        bindSummaryValue(findPreference("key_full_name"));
        bindSummaryValue(findPreference("key_telephone"));
        bindSummaryValue(findPreference("key_cni"));
        bindSummaryValue(findPreference("key_ville"));
        bindSummaryValue(findPreference("key_app_notification"));

    }

    @Override
    public void onStop() {
        super.onStop();
        savePreferences(findPreference("key_full_name"), findPreference("key_telephone"),
                findPreference("key_email_address"), findPreference("key_cni"), findPreference("key_ville"),
                findPreference("key_app_notification"));
    }

    private void savePreferences(Preference key_full_name, Preference key_telephone,
                                 Preference key_email_address, Preference key_cni,
                                 Preference key_ville, Preference key_app_notification) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putString(PREF_NAME, key_full_name.getSummary().toString());
        editor.putString(PREF_PHONE, key_full_name.getSummary().toString());
        editor.putString(PREF_EMAIL, key_full_name.getSummary().toString());
        editor.putString(PREF_CNI, key_full_name.getSummary().toString());
        editor.putString(PREF_VILLE, key_full_name.getSummary().toString());
        //editor.putBoolean(PREF_NOTIFICATION, key_full_name.is);
        editor.apply();
    }

    private void getUserPreference(Preference preference) {
        PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), "");
    }

    private static void bindSummaryValue(Preference preference) {
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener(){
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if(preference instanceof ListPreference){
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(index > 0
                        ? listPreference.getEntries()[index]
                        : null);
            }else if(preference instanceof EditTextPreference){
                preference.setSummary(stringValue);
            }else if(preference instanceof RingtonePreference){
                if(TextUtils.isEmpty(stringValue)){
                    preference.setSummary("Silent");
                }else{
                    Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
                    if(ringtone == null){
                        preference.setSummary("Choose notification ringtone");
                    }else{
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }
            }
            return true;
        }
    };
}
