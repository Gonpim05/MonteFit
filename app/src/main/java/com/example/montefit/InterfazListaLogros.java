package com.example.montefit;

import android.content.Context;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class InterfazListaLogros extends RecyclerView.Adapter<InterfazListaLogros.ViewHolder> {

    private Context contexto;
    private Cursor datosBD;

    public InterfazListaLogros(Context contexto, Cursor datosBD) {
        this.contexto = contexto;
        this.datosBD = datosBD;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_logro, padre, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder filaVisor, int posicion) {
        if (!datosBD.moveToPosition(posicion))
            return;

        @SuppressLint("Range")
        int idxTitulo = datosBD.getColumnIndex(GestorBaseDatos.COLUMN_LOGRO_TITULO);
        @SuppressLint("Range")
        int idxDesc = datosBD.getColumnIndex(GestorBaseDatos.COLUMN_LOGRO_DESCRIPCION);
        // The simple alias "obtenido" from the subquery
        @SuppressLint("Range")
        int idxObtenido = datosBD.getColumnIndex("obtenido");

        String titulo = datosBD.getString(idxTitulo);
        String descripcion = datosBD.getString(idxDesc);
        boolean obtenido = datosBD.getInt(idxObtenido) > 0;

        filaVisor.txtTitulo.setText(titulo);
        filaVisor.txtDesc.setText(descripcion);
        filaVisor.checkBox.setChecked(obtenido);
        filaVisor.itemView.setAlpha(obtenido ? 1.0f : 0.5f);
    }

    @Override
    public int getItemCount() {
        return datosBD == null ? 0 : datosBD.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDesc;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTituloLogro);
            txtDesc = itemView.findViewById(R.id.txtDescLogro);
            checkBox = itemView.findViewById(R.id.checkLogro);
        }
    }
}























