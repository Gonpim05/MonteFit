package com.example.montefit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InterfazListaSocial extends RecyclerView.Adapter<InterfazListaSocial.ViewHolder> {

    private Context contexto;
    private Cursor datosBD;

    public InterfazListaSocial(Context contexto, Cursor datosBD) {
        this.contexto = contexto;
        this.datosBD = datosBD;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_ranking, padre, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder filaVisor, int posicion) {
        if (!datosBD.moveToPosition(posicion))
            return;
        @SuppressLint("Range")
        int idxNombre = datosBD.getColumnIndex(GestorBaseDatos.COLUMN_NOMBRE);
        @SuppressLint("Range")
        int idxPeso = datosBD.getColumnIndex(GestorBaseDatos.COLUMN_PESO_MAXIMO);

        String nombre = datosBD.getString(idxNombre);
        float peso = datosBD.getFloat(idxPeso);

        filaVisor.txtPosicion.setText("#" + (posicion + 1));
        filaVisor.txtNombre.setText(nombre);
        filaVisor.txtPeso.setText(String.format("%.1f kg", peso));
    }

    @Override
    public int getItemCount() {
        return datosBD == null ? 0 : datosBD.getCount();
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























