package cm.deone.corp.tontines.reglement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import cm.deone.corp.tontines.MainActivity;
import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.adapters.AdapterReglement;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Article;

public class ShowReglement extends AppCompatActivity {

    private DatabaseReference database;
    private String idUser;
    private String idTontine;
    private AdapterReglement adapterReglement;
    private RecyclerView rvTontineArticles;
    private TextView tvNoArticles;

    List<Article> articleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reglement);
        checkUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_reglement, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        manageSearchView(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_add) {
            Intent intent = new Intent(ShowReglement.this, AddArticle.class);
            intent.putExtra("idTontine", idTontine);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUser() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(ShowReglement.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            idUser = mUser.getUid();
            initViews();
            allArticles();
        }
    }

    private void initViews() {
        Toolbar articleToolbar = findViewById(R.id.articleToolbar);
        articleToolbar.setTitle("Règlement");
        setSupportActionBar(articleToolbar);
        Intent intent = getIntent();
        idTontine = ""+intent.getStringExtra(this.getResources().getString(R.string.idTontine));
        database = FirebaseDatabase.getInstance().getReference();
        articleList = new ArrayList<>();
        tvNoArticles = findViewById(R.id.tvNoArticle);
        rvTontineArticles = findViewById(R.id.rvArticles);
        rvTontineArticles.setHasFixedSize(true);
        rvTontineArticles.setLayoutManager(new LinearLayoutManager(ShowReglement.this));
    }

    private void manageSearchView(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    searchArticles(query);
                }else {
                    allArticles();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    searchArticles(newText);
                }else {
                    allArticles();
                }
                return false;
            }
        });
    }

    private void allArticles() {
        database.child(this.getResources().getString(R.string.Tontines)).child(idTontine)
                .child(this.getResources().getString(R.string.reglement)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear();
                for(final DataSnapshot ds:dataSnapshot.getChildren()){
                    Article article = ds.getValue(Article.class);
                    articleList.add(article);
                    if (articleList.isEmpty()) {
                        tvNoArticles.setVisibility(View.VISIBLE);
                        rvTontineArticles.setVisibility(View.GONE);
                    }else{
                        tvNoArticles.setVisibility(View.GONE);
                        rvTontineArticles.setVisibility(View.VISIBLE);
                        adapterReglement = new AdapterReglement(ShowReglement.this, articleList);
                        rvTontineArticles.setAdapter(adapterReglement);
                        adapterReglement.setOnItemClickListener(new IntRvClickListner() {
                            @Override
                            public void onItemClick(int position) {
                                /*Intent intent = new Intent(getActivity(), ShowMembre.class);
                                intent.putExtra("mID", ds.getKey());
                                intent.putExtra("idTontine", idTontine);
                                getActivity().startActivity(intent);*/
                            }

                            @Override
                            public void onLongItemClick(int position) {
                                //Toast.makeText(ShowReglement.this, ""+membreList.get(position).getPhone(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShowReglement.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchArticles(final String searchQuery) {
        database.child(this.getResources().getString(R.string.Tontines)).child(idTontine)
                .child(this.getResources().getString(R.string.reglement)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                articleList.clear();
                for(final DataSnapshot ds:dataSnapshot.getChildren()){
                    Article article = ds.getValue(Article.class);
                    if (article.getTypeArticle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            article.getTitreArticle().toLowerCase().contains(searchQuery.toLowerCase())||
                            article.getContenuArticle().toLowerCase().contains(searchQuery.toLowerCase())){
                        articleList.add(article);
                    }
                    if (articleList.isEmpty()) {
                        tvNoArticles.setVisibility(View.VISIBLE);
                        rvTontineArticles.setVisibility(View.GONE);
                    }else{
                        tvNoArticles.setVisibility(View.GONE);
                        rvTontineArticles.setVisibility(View.VISIBLE);
                        adapterReglement = new AdapterReglement(ShowReglement.this, articleList);
                        rvTontineArticles.setAdapter(adapterReglement);
                        adapterReglement.setOnItemClickListener(new IntRvClickListner() {
                            @Override
                            public void onItemClick(int position) {
                                /*Intent intent = new Intent(getActivity(), ShowMembre.class);
                                intent.putExtra("mID", ds.getKey());
                                intent.putExtra("idTontine", idTontine);
                                getActivity().startActivity(intent);*/
                            }

                            @Override
                            public void onLongItemClick(int position) {
                                //Toast.makeText(ShowReglement.this, ""+membreList.get(position).getPhone(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ShowReglement.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}