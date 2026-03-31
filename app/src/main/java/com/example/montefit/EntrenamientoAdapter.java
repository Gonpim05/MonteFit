package com.example.montefit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EntrenamientoAdapter extends RecyclerView.Adapter<EntrenamientoAdapter.ViewHolder> {

    private List<Entrenamiento> entrenamientos;
    private Context context;

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    private OnItemLongClickListener longClickListener;

    public EntrenamientoAdapter(Context context, List<Entrenamiento> entrenamientos,
            OnItemLongClickListener longClickListener) {
        this.context = context;
        this.entrenamientos = entrenamientos;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_entrenamiento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entrenamiento entrenamiento = entrenamientos.get(position);
        holder.tvNombre.setText("Entrenamiento del " + entrenamiento.getFecha());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleEntrenamientoActivity.class);
            intent.putExtra("entrenamiento", entrenamiento);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return entrenamientos.size();
    }

    public void updateList(List<Entrenamiento> newList) {
        this.entrenamientos = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEntrenamiento);
        }
    }
}
