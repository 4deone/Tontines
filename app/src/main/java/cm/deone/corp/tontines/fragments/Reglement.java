package cm.deone.corp.tontines.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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


public class Reglement extends Fragment {

    private DatabaseReference database;
    private String idUser;
    private String idTontine;
    private AdapterReglement adapterReglement;
    private RecyclerView rvTontineArticles;
    private TextView tvNoArticles;

    List<Article> articleList;

    public Reglement() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idTontine = getArguments().getString("idTontine");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reglement, container, false);
        checkUser(view);
        return view;
    }

    private void checkUser(View view) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if (mUser ==null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }else {
            idUser = mUser.getUid();
            initViews(view);
            allArticles();
        }
    }

    private void initViews(View view) {
        Toolbar articleToolbar = view.findViewById(R.id.articleToolbar);
        articleToolbar.setTitle("Règlement");
        ((AppCompatActivity)getActivity()).setSupportActionBar(articleToolbar);
        database = FirebaseDatabase.getInstance().getReference();
        articleList = new ArrayList<>();
        tvNoArticles = view.findViewById(R.id.tvNoArticle);
        rvTontineArticles = view.findViewById(R.id.rvArticles);
        rvTontineArticles.setHasFixedSize(true);
        rvTontineArticles.setLayoutManager(new LinearLayoutManager(getActivity()));
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
                        adapterReglement = new AdapterReglement(getActivity(), articleList);
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
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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
                        adapterReglement = new AdapterReglement(getActivity(), articleList);
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
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}