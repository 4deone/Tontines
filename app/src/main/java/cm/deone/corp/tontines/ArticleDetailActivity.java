package cm.deone.corp.tontines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ArticleDetailActivity extends AppCompatActivity {

    private String idArticle;
    private String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Intent intent = getIntent();
        idArticle = intent.getStringExtra("idArticle");

    }
}