package com.example.montefit;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PantallaLogros extends AppCompatActivity {

    private RecyclerView recyclerLogros;
    private InterfazListaLogros adaptador;
    private List<InterfazListaLogros.Logro> listaLogros = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_logros);

        recyclerLogros = findViewById(R.id.recyclerLogros);
        recyclerLogros.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new InterfazListaLogros(this, listaLogros);
        recyclerLogros.setAdapter(adaptador);

        cargarLogros();
    }

    private void cargarLogros() {
        int userId = GestorUsuarios.getInstance().getUsuarioId();
        if (userId <= 0) {
            // Intenta obtenerlo
            String correo = GestorUsuarios.getInstance().getCorreoActual();
            if (correo == null) return;

            new Thread(() -> {
                int id = ClienteApi.obtenerInstancia().obtenerUsuarioId(correo);
                GestorUsuarios.getInstance().setUsuarioId(id);
                cargarLogrosDesdeApi(id);
            }).start();
        } else {
            new Thread(() -> cargarLogrosDesdeApi(userId)).start();
        }
    }

    private void cargarLogrosDesdeApi(int userId) {
        JSONArray datos = ClienteApi.obtenerInstancia().obtenerLogros(userId);
        List<InterfazListaLogros.Logro> lista = new ArrayList<>();

        for (int i = 0; i < datos.length(); i++) {
            try {
                JSONObject obj = datos.getJSONObject(i);
                lista.add(new InterfazListaLogros.Logro(
                        obj.optString("titulo", ""),
                        obj.optString("descripcion", ""),
                        obj.optInt("obtenido", 0) > 0
                ));
            } catch (Exception e) { e.printStackTrace(); }
        }

        runOnUiThread(() -> {
            listaLogros.clear();
            listaLogros.addAll(lista);
            adaptador.notifyDataSetChanged();
        });
    }
}
