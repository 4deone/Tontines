package cm.deone.corp.tontines.interfaces;

import android.app.Activity;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

public interface IntTontine {
    void addTontine(final Activity activity, final ProgressBar progressBar, final String idUser, String nameMembre, String phoneMembre);
    void allTontines(final Activity activity, final RecyclerView recyclerview, final String uID);
    void searchTontines(final Activity activity, final RecyclerView recyclerview, final String uID, final String searchQuery);
    void freezeTontine();
    void deleteTontine();

}
