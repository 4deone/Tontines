package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Article;
import cm.deone.corp.tontines.models.User;

public class AdapterReglement extends RecyclerView.Adapter<AdapterReglement.MyHolder> {

    private Context context;
    private IntRvClickListner listener;
    private List<Article> articleList;

    public AdapterReglement(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_article, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterReglement.MyHolder holder, final int position) {
        final String type = articleList.get(position).getTypeArticle();
        final String titre = articleList.get(position).getTitreArticle();
        final String contenu = articleList.get(position).getContenuArticle();
        final String timestamp = articleList.get(position).getDateArticle();
        holder.mType.setText(type);
        holder.mTitre.setText(titre);
        holder.mContenu.setText(contenu);
        holder.mDate.setText(convertTimestamp(timestamp));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    private String convertTimestamp(String timestamp) {
        SimpleDateFormat formater = new SimpleDateFormat(context.getResources().getString(R.string.date_pattern));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        return formater.format(calendar.getTime());
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView mType;
        TextView mTitre;
        TextView mContenu;
        TextView mDate;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mType = itemView.findViewById(R.id.tvTypeArticle);
            mTitre = itemView.findViewById(R.id.tvTitreArticle);
            mContenu = itemView.findViewById(R.id.tvContenuArticle);
            mDate = itemView.findViewById(R.id.tvDateArticle);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onLongItemClick(position);
            }
            return true;
        }
    }
}
