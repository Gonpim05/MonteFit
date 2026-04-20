package com.example.montefit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Adaptador de logros - usa List en vez de Cursor (ya no depende de SQLite).
 */
public class InterfazListaLogros extends RecyclerView.Adapter<InterfazListaLogros.ViewHolder> {

    private Context contexto;
    private List<Logro> listaLogros;

    /** Modelo simple para un logro */
    public static class Logro {
        public String titulo;
        public String descripcion;
        public boolean obtenido;

        public Logro(String titulo, String descripcion, boolean obtenido) {
            this.titulo = titulo;
            this.descripcion = descripcion;
            this.obtenido = obtenido;
        }
    }

    public InterfazListaLogros(Context contexto, List<Logro> listaLogros) {
        this.contexto = contexto;
        this.listaLogros = listaLogros;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int viewType) {
        View vista = LayoutInflater.from(contexto).inflate(R.layout.item_logro, padre, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder filaVisor, int posicion) {
        Logro logro = listaLogros.get(posicion);

        filaVisor.txtTitulo.setText(logro.titulo);
        filaVisor.txtDesc.setText(logro.descripcion);
        filaVisor.checkBox.setChecked(logro.obtenido);
        filaVisor.itemView.setAlpha(logro.obtenido ? 1.0f : 0.5f);

        if (filaVisor.txtEmoji != null) {
            filaVisor.txtEmoji.setText(logro.obtenido ? "🏆" : "🔒");
        }
    }

    @Override
    public int getItemCount() {
        return listaLogros == null ? 0 : listaLogros.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDesc, txtEmoji;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTituloLogro);
            txtDesc = itemView.findViewById(R.id.txtDescLogro);
            checkBox = itemView.findViewById(R.id.checkLogro);
            txtEmoji = itemView.findViewById(R.id.txtEmojiLogro);
        }
    }
}
