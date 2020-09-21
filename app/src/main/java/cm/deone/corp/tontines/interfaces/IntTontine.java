package cm.deone.corp.tontines.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface IntTontine {
    void createTontine(final String idUtilisateur, String name, String phone);
    void allTontines(final RecyclerView recyclerview, final String uID);
    void searchTontines(final RecyclerView recyclerview, final String uID, final String searchQuery);
}
