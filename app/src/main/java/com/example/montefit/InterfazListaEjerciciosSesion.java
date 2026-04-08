package com.example.montefit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InterfazListaEjerciciosSesion extends RecyclerView.Adapter<InterfazListaEjerciciosSesion.ViewHolder> {

    private List<EjercicioSesion> sessionItems;

    public interface OnItemClickListener {
        void onItemClick(int posicion);

        void onItemLongClick(int posicion);
    }

    private OnItemClickListener listener;

    public InterfazListaEjerciciosSesion(List<EjercicioSesion> sessionItems, OnItemClickListener listener) {
        this.sessionItems = sessionItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int viewType) {
        View vista = LayoutInflater.from(padre.getContext()).inflate(android.R.layout.simple_list_item_2, padre,
                false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder filaVisor, int posicion) {
        EjercicioSesion elemento = sessionItems.get(posicion);
        filaVisor.text1.setText(elemento.getEjercicio().getName() + " (" + elemento.getEjercicio().getBodyPart() + ")");

        StringBuilder setsInfo = new StringBuilder();
        List<EjercicioSesion.DetalleSerie> sets = elemento.getSeries();
        for (int i = 0; i < sets.size(); i++) {
            EjercicioSesion.DetalleSerie set = sets.get(i);
            setsInfo.append("Set ").append(i + 1).append(": ")
                    .append(set.repeticiones).append("x").append(set.peso).append("kg");
            if (i < sets.size() - 1)
                setsInfo.append(" | ");
        }

        filaVisor.text2.setText(setsInfo.toString());

        filaVisor.itemView.setOnClickListener(v -> {
            listener.onItemClick(posicion);
        });

        filaVisor.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(posicion);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return sessionItems.size();
    }

    public void updateList(List<EjercicioSesion> newList) {
        this.sessionItems = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}























