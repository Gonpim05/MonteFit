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

public class LogrosAdapter extends RecyclerView.Adapter<LogrosAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public LogrosAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_logro, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position))
            return;

        @SuppressLint("Range")
        int idxTitulo = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOGRO_TITULO);
        @SuppressLint("Range")
        int idxDesc = cursor.getColumnIndex(DatabaseHelper.COLUMN_LOGRO_DESCRIPCION);
        // The simple alias "obtenido" from the subquery
        @SuppressLint("Range")
        int idxObtenido = cursor.getColumnIndex("obtenido");

        String titulo = cursor.getString(idxTitulo);
        String descripcion = cursor.getString(idxDesc);
        boolean obtenido = cursor.getInt(idxObtenido) > 0;

        holder.txtTitulo.setText(titulo);
        holder.txtDesc.setText(descripcion);
        holder.checkBox.setChecked(obtenido);
        holder.itemView.setAlpha(obtenido ? 1.0f : 0.5f);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
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
