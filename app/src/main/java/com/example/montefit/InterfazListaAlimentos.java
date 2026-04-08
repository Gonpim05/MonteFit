package com.example.montefit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class InterfazListaAlimentos extends RecyclerView.Adapter<InterfazListaAlimentos.ViewHolder> {

    private List<Alimento> listaAlimentos;

    public interface OnFoodClickListener {
        void onDeleteClick(Alimento Alimento);
    }

    private OnFoodClickListener listener;

    public InterfazListaAlimentos(List<Alimento> listaAlimentos, OnFoodClickListener listener) {
        this.listaAlimentos = listaAlimentos;
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
        Alimento Alimento = listaAlimentos.get(posicion);
        filaVisor.text1.setText(Alimento.getName());
        filaVisor.text2.setText(String.format("%d calorias | P: %.1fg | C: %.1fg | G: %.1fg",
                Alimento.getKcal(), Alimento.getProtein(), Alimento.getCarbs(), Alimento.getFats()));

        filaVisor.itemView.setOnClickListener(v -> {
            listener.onDeleteClick(Alimento);
        });

        filaVisor.itemView.setOnLongClickListener(v -> {
            listener.onDeleteClick(Alimento);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaAlimentos.size();
    }

    public void updateList(List<Alimento> newList) {
        this.listaAlimentos = newList;
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



















