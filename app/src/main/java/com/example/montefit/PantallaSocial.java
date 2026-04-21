package com.example.montefit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PantallaSocial extends AppCompatActivity {

    private RecyclerView recyclerRanking;
    private Spinner spinnerEjercicios;
    private List<JSONObject> listaEjerciciosTodos = new ArrayList<>();
    private InterfazListaSocial adaptadorRanking;
    private List<InterfazListaSocial.ItemRanking> listaRanking = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_social);

        recyclerRanking = findViewById(R.id.recyclerRanking);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this));
        adaptadorRanking = new InterfazListaSocial(this, listaRanking);
        recyclerRanking.setAdapter(adaptadorRanking);

        spinnerEjercicios = findViewById(R.id.spinnerEjerciciosRanking);
        TextView tvSemana = findViewById(R.id.tvSemanaActual);

        java.util.Calendar cal = java.util.Calendar.getInstance();
        tvSemana.setText("Semana " + cal.get(java.util.Calendar.WEEK_OF_YEAR) + " - " + cal.get(java.util.Calendar.YEAR));


        new Thread(() -> {
            JSONArray ejercicios = ClienteApi.obtenerInstancia().obtenerTodosEjercicios();
            List<String> nombresEj = new ArrayList<>();
            listaEjerciciosTodos.clear();

            for (int i = 0; i < ejercicios.length(); i++) {
                try {
                    JSONObject obj = ejercicios.getJSONObject(i);
                    listaEjerciciosTodos.add(obj);
                    nombresEj.add(obj.optString("nombre", ""));
                } catch (Exception e) { e.printStackTrace(); }
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, nombresEj);
                spinnerEjercicios.setAdapter(spinnerAdapter);

                spinnerEjercicios.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int pos, long id) {
                        cargarRanking(pos);
                    }
                    @Override
                    public void onNothingSelected(android.widget.AdapterView<?> parent) {}
                });
            });
        }).start();


        findViewById(R.id.btnBuscarUsuario).setOnClickListener(v -> mostrarBuscarUsuario());
    }

    private void cargarRanking(int posSpinner) {
        if (posSpinner < 0 || posSpinner >= listaEjerciciosTodos.size()) return;

        int ejercicioId = listaEjerciciosTodos.get(posSpinner).optInt("_id", 0);

        new Thread(() -> {
            JSONArray ranking = ClienteApi.obtenerInstancia().obtenerRankings(ejercicioId);
            List<InterfazListaSocial.ItemRanking> lista = new ArrayList<>();

            for (int i = 0; i < ranking.length(); i++) {
                try {
                    JSONObject obj = ranking.getJSONObject(i);
                    lista.add(new InterfazListaSocial.ItemRanking(
                            obj.optString("nombre", ""),
                            (float) obj.optDouble("peso_maximo", 0)
                    ));
                } catch (Exception e) { e.printStackTrace(); }
            }

            runOnUiThread(() -> {
                listaRanking.clear();
                listaRanking.addAll(lista);
                adaptadorRanking.notifyDataSetChanged();
            });
        }).start();
    }

    private void mostrarBuscarUsuario() {
        EditText input = new EditText(this);
        input.setHint("Nombre del usuario");
        input.setPadding(50, 30, 50, 10);

        new AlertDialog.Builder(this)
                .setTitle("Buscar Usuario")
                .setView(input)
                .setPositiveButton("Buscar", (d, w) -> {
                    String nombre = input.getText().toString().trim();
                    if (nombre.isEmpty()) return;

                    new Thread(() -> {
                        JSONArray resultados = ClienteApi.obtenerInstancia().buscarUsuario(nombre);
                        runOnUiThread(() -> {
                            if (resultados.length() == 0) {
                                Toast.makeText(this, "No se encontró ningún usuario", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String[] nombres = new String[resultados.length()];
                            for (int i = 0; i < resultados.length(); i++) {
                                nombres[i] = resultados.optJSONObject(i).optString("nombre", "") +
                                        " (" + resultados.optJSONObject(i).optString("correo", "") + ")";
                            }
                            new AlertDialog.Builder(this)
                                    .setTitle("Usuarios encontrados")
                                    .setItems(nombres, (d2, idx) -> {
                                        int userId = resultados.optJSONObject(idx).optInt("id", 0);
                                        mostrarEntrenamientosPublicos(userId, nombres[idx]);
                                    })
                                    .show();
                        });
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarEntrenamientosPublicos(int usuarioId, String nombreUsuario) {
        new Thread(() -> {
            JSONArray rutinas = ClienteApi.obtenerInstancia().obtenerRutinasPublicas(usuarioId);
            runOnUiThread(() -> {
                if (rutinas.length() == 0) {
                    Toast.makeText(this, "No tiene entrenamientos públicos", Toast.LENGTH_SHORT).show();
                    return;
                }
                String[] items = new String[rutinas.length()];
                for (int i = 0; i < rutinas.length(); i++) {
                    items[i] = "Entrenamiento del " + rutinas.optJSONObject(i).optString("date", "");
                }
                new AlertDialog.Builder(this)
                        .setTitle("Entrenos de " + nombreUsuario)
                        .setItems(items, (d, idx) -> {
                            long rutinaId = rutinas.optJSONObject(idx).optLong("id", 0);
                            String fecha = rutinas.optJSONObject(idx).optString("date", "");
                            new Thread(() -> {
                                JSONArray detallesJson = ClienteApi.obtenerInstancia().obtenerDetallesRutina(rutinaId);
                                java.util.List<Entrenamiento.EjercicioDetalle> listaDetalles = new java.util.ArrayList<>();
                                for (int j = 0; j < detallesJson.length(); j++) {
                                    JSONObject det = detallesJson.optJSONObject(j);
                                    if (det != null) {
                                        listaDetalles.add(new Entrenamiento.EjercicioDetalle(
                                                det.optString("nombre_ejercicio", ""),
                                                det.optInt("series", 0),
                                                det.optDouble("peso", 0),
                                                det.optInt("repeticiones", 0)
                                        ));
                                    }
                                }
                                Entrenamiento ent = new Entrenamiento(rutinaId, fecha, listaDetalles, true);
                                runOnUiThread(() -> {
                                    android.content.Intent intent = new android.content.Intent(PantallaSocial.this, PantallaDetalleEntrenamiento.class);
                                    intent.putExtra("entrenamiento", ent);
                                    startActivity(intent);
                                });
                            }).start();
                        })
                        .setPositiveButton("Cerrar", null)
                        .show();
            });
        }).start();
    }
}
