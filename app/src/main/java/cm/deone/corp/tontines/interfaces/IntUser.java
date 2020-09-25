package cm.deone.corp.tontines.interfaces;

import android.app.Activity;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

public interface IntUser {
    void signUser(final Activity activity, final ProgressBar mProgressBar, String password);
    void recoverUserPassword();
    void addUser(final Activity activity, final ProgressBar progressBar);
    void allUsers(final Activity activity, final RecyclerView recyclerview, final String uID);
    void searchUsers(final Activity activity, final RecyclerView recyclerview, final String uID, final String searchQuery);
    void freezeUser();
    void deleteUser();
}
