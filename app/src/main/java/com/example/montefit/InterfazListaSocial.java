package com.example.montefit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adaptador de ranking social - usa List en vez de Cursor (ya no depende de SQLite).
 */
public class InterfazListaSocial extends RecyclerView.Adapter<InterfazListaSocial.ViewHolder> {

    private Context contexto;
    private List<ItemRanking> listaRanking;

    /** Modelo simple para un item del ranking */
    public static class ItemRanking {
        public String nombre;
        public float pesoMaximo;

        public ItemRanking(String nombre, float pesoMaximo) {
            this.nombre = nombre;
            this.pesoMaximo = pesoMaximo;
        }
    }

    public InterfazListaSocial(Context contexto, List<ItemRanking> listaRanking) {
        this.contexto = contexto;
        this.listaRanking = listaRanking;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_ranking, padre, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder filaVisor, int posicion) {
        ItemRanking item = listaRanking.get(posicion);

        filaVisor.txtPosicion.setText("#" + (posicion + 1));
        filaVisor.txtNombre.setText(item.nombre);
        filaVisor.txtPeso.setText(String.format("%.1f kg", item.pesoMaximo));
    }

    @Override
    public int getItemCount() {
        return listaRanking == null ? 0 : listaRanking.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPosicion, txtNombre, txtPeso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPosicion = itemView.findViewById(R.id.txtPosicion);
            txtNombre = itemView.findViewById(R.id.txtNombreUsuario);
            txtPeso = itemView.findViewById(R.id.txtPesoMax);
        }
    }
}
