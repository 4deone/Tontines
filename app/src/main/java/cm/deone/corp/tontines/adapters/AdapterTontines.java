package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.interfaces.IntRvClickListner;
import cm.deone.corp.tontines.models.Tontine;

public class AdapterTontines extends RecyclerView.Adapter<AdapterTontines.MyHolder>{


    private Context context;
    private IntRvClickListner listener;
    private List<Tontine> tontineList;

    public AdapterTontines(Context context, List<Tontine> tontineList) {
        this.context = context;
        this.tontineList = tontineList;
    }

    @NonNull
    @Override
    public AdapterTontines.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tontine, parent, false);
        return new MyHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterTontines.MyHolder holder, final int position) {

        final String devise = tontineList.get(position).getDeviseTontine();
        final String name = tontineList.get(position).getNameTontine();
        final boolean privateTontine = tontineList.get(position).isStatusTontine();
        //final boolean name = tontineList.get(position).getNameTontine();

        holder.mPrivateTontine.setVisibility(privateTontine?View.VISIBLE:View.GONE);
        if (privateTontine) {
            holder.mPrivateTontine.setImageResource(R.drawable.ic_action_private);
        }
        holder.mTitreTontine.setText(name);
        holder.mDeviseTontine.setText(devise);
    }

    @Override
    public int getItemCount() {
        return tontineList.size();
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView mTitreTontine;
        TextView mDeviseTontine;
        ImageView mPrivateTontine;
        ImageView mFavoriteTontine;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mTitreTontine = itemView.findViewById(R.id.titreTontine);
            mDeviseTontine = itemView.findViewById(R.id.deviseTontine);
            mPrivateTontine = itemView.findViewById(R.id.privateTontine);
            mFavoriteTontine = itemView.findViewById(R.id.favoriteTontine);

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
