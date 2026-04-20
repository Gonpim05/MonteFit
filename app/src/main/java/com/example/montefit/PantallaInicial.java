package com.example.montefit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PantallaInicial extends AppCompatActivity {

    private RecyclerView rvEntrenamientos;
    private InterfazListaEntrenamientos adaptador;
    private List<Entrenamiento> listaEntrenamientos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_inicial);

        rvEntrenamientos = findViewById(R.id.rvEntrenamientos);
        rvEntrenamientos.setLayoutManager(new LinearLayoutManager(this));

        adaptador = new InterfazListaEntrenamientos(this, listaEntrenamientos, this::confirmarEliminar);
        rvEntrenamientos.setAdapter(adaptador);

        // Botones de navegación (como View para evitar ClassCastException, ya que btnPerfil ahora es un TextView)
        android.view.View btnEntrenar = findViewById(R.id.btnEntrenar);
        android.view.View btnComida = findViewById(R.id.btnComida);
        android.view.View btnLogros = findViewById(R.id.btnLogros);
        android.view.View btnSocial = findViewById(R.id.btnSocial);
        android.view.View btnPerfil = findViewById(R.id.btnPerfil);

        btnEntrenar.setOnClickListener(v -> startActivity(new Intent(this, PantallaEntrenar.class)));
        btnComida.setOnClickListener(v -> startActivity(new Intent(this, PantallaComida.class)));
        btnLogros.setOnClickListener(v -> startActivity(new Intent(this, PantallaLogros.class)));
        btnSocial.setOnClickListener(v -> startActivity(new Intent(this, PantallaSocial.class)));
        btnPerfil.setOnClickListener(v -> startActivity(new Intent(this, PantallaPerfil.class)));

        // Saludo personalizado con el nombre de usuario
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        String correo = GestorUsuarios.getInstance().getCorreoActual();
        if (correo != null) {
            new Thread(() -> {
                String nombre = ClienteApi.obtenerInstancia().obtenerNombre(correo);
                runOnUiThread(() -> {
                    if (!nombre.isEmpty()) {
                        tvGreeting.setText("¡Hola, " + nombre + "!");
                    } else {
                        tvGreeting.setText("¡Hola!");
                    }
                });
            }).start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarEntrenamientos();
    }

    private void cargarEntrenamientos() {
        String correo = GestorUsuarios.getInstance().getCorreoActual();
        if (correo == null) return;

        new Thread(() -> {
            JSONArray datos = ClienteApi.obtenerInstancia().obtenerMisRutinas(correo);
            List<Entrenamiento> lista = new ArrayList<>();

            for (int i = 0; i < datos.length(); i++) {
                try {
                    JSONObject obj = datos.getJSONObject(i);
                    long id = obj.optLong("id", 0);
                    String fecha = obj.optString("date", "");
                    boolean publico = obj.optInt("es_publico", 1) == 1;

                    // Cargar detalles del entrenamiento
                    JSONArray detalles = ClienteApi.obtenerInstancia().obtenerDetallesRutina(id);
                    List<Entrenamiento.EjercicioDetalle> listaDetalles = new ArrayList<>();
                    for (int j = 0; j < detalles.length(); j++) {
                        JSONObject det = detalles.getJSONObject(j);
                        listaDetalles.add(new Entrenamiento.EjercicioDetalle(
                                det.optString("nombre_ejercicio", ""),
                                det.optInt("series", 0),
                                det.optDouble("peso", 0),
                                det.optInt("repeticiones", 0)
                        ));
                    }
                    lista.add(new Entrenamiento(id, fecha, listaDetalles, publico));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                listaEntrenamientos.clear();
                listaEntrenamientos.addAll(lista);
                adaptador.notifyDataSetChanged();
            });
        }).start();
    }

    private void confirmarEliminar(int posicion) {
        Entrenamiento ent = listaEntrenamientos.get(posicion);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar entrenamiento")
                .setMessage("¿Seguro que quieres eliminar este entrenamiento?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    new Thread(() -> {
                        ClienteApi.obtenerInstancia().eliminarRutina(ent.getId());
                        runOnUiThread(this::cargarEntrenamientos);
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
