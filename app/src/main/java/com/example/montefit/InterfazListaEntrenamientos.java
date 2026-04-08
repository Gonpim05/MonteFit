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

public class InterfazListaEntrenamientos extends RecyclerView.Adapter<InterfazListaEntrenamientos.ViewHolder> {

    private List<Entrenamiento> entrenamientos;
    private Context contexto;

    public interface OnItemLongClickListener {
        void onItemLongClick(int posicion);
    }

    private OnItemLongClickListener longClickListener;

    public InterfazListaEntrenamientos(Context contexto, List<Entrenamiento> entrenamientos,
            OnItemLongClickListener longClickListener) {
        this.contexto = contexto;
        this.entrenamientos = entrenamientos;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_entrenamiento, padre, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder filaVisor, int posicion) {
        Entrenamiento entrenamiento = entrenamientos.get(posicion);
        filaVisor.tvNombre.setText("Entrenamiento del " + entrenamiento.getFecha());

        // Mostrar icono de visibilidad
        if (filaVisor.tvIconoVisibilidad != null) {
            filaVisor.tvIconoVisibilidad.setText(entrenamiento.isPublico() ? "🌐" : "🔒");
        }

        filaVisor.itemView.setOnClickListener(v -> {
            Intent cambioPantalla = new Intent(contexto, PantallaDetalleEntrenamiento.class);
            cambioPantalla.putExtra("entrenamiento", entrenamiento);
            contexto.startActivity(cambioPantalla);
        });

        filaVisor.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(posicion);
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
        TextView tvIconoVisibilidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreEntrenamiento);
            tvIconoVisibilidad = itemView.findViewById(R.id.tvIconoVisibilidad);
        }
    }
}
