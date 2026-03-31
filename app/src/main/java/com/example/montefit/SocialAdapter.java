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

public class SocialAdapter extends RecyclerView.Adapter<SocialAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public SocialAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position))
            return;
        @SuppressLint("Range")
        int idxNombre = cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE);
        @SuppressLint("Range")
        int idxPeso = cursor.getColumnIndex(DatabaseHelper.COLUMN_PESO_MAXIMO);

        String nombre = cursor.getString(idxNombre);
        float peso = cursor.getFloat(idxPeso);

        holder.txtPosicion.setText("#" + (position + 1));
        holder.txtNombre.setText(nombre);
        holder.txtPeso.setText(String.format("%.1f kg", peso));
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
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
