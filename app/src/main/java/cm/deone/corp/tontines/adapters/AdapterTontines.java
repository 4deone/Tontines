package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cm.deone.corp.tontines.R;
import cm.deone.corp.tontines.models.Tontine;

public class AdapterTontines extends RecyclerView.Adapter<AdapterTontines.MyHolder>{


    private Context context;
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

        holder.mTitreTontine.setText(name);
        holder.mDeviseTontine.setText(devise);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+name+"("+devise+")", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return tontineList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        TextView mTitreTontine;
        TextView mDeviseTontine;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mTitreTontine = itemView.findViewById(R.id.titreTontine);
            mDeviseTontine = itemView.findViewById(R.id.deviseTontine);
        }
    }
}
