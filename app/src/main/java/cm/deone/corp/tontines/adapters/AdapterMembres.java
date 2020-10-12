package cm.deone.corp.tontines.adapters;

import android.content.Context;
import android.graphics.Typeface;
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
import cm.deone.corp.tontines.models.Membre;

import static cm.deone.corp.tontines.outils.MesOutils.findContact;

public class AdapterMembres extends RecyclerView.Adapter<AdapterMembres.MyHolder> {

    private Context context;
    private IntRvClickListner listener;
    private List<Membre> membreList;

    public AdapterMembres(Context context, List<Membre> membreList) {
        this.context = context;
        this.membreList = membreList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMembres.MyHolder holder, final int position) {
        final String nom = membreList.get(position).getName();
        final String bureau = membreList.get(position).getBureau();
        final String phone = membreList.get(position).getPhone();
        boolean myContact = findContact(context, phone);

        holder.mContactTontine.setText(bureau);

        if (myContact) {
            holder.mContactName.setText(nom);
        }else{
            holder.mContactName.setText("~ "+nom +" ~");
            holder.mContactName.setTypeface(holder.mContactName.getTypeface(), Typeface.ITALIC);
        }

    }

    @Override
    public int getItemCount() {
        return membreList.size();
    }

    public void setOnItemClickListener(IntRvClickListner listener){
        this.listener = listener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView mAvatar;
        TextView mContactName;
        TextView mContactTontine;

        MyHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.imContact);
            mContactName = itemView.findViewById(R.id.tvContactName);
            mContactTontine = itemView.findViewById(R.id.tvContactNumberTontine);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onItemClick(v, position);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.onLongItemClick(v, position);
            }
            return true;
        }
    }
}
