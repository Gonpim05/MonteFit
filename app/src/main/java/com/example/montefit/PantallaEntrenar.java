package com.example.montefit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PantallaEntrenar extends AppCompatActivity {

    private RecyclerView rvSesion;
    private InterfazListaEjerciciosSesion adaptadorSesion;
    private List<EjercicioSesion> listaSesion = new ArrayList<>();
    private boolean sesionPublica = true;

    private static final String[] GRUPOS_MUSCULARES = {
            "Pecho", "Espalda", "Hombro", "Bíceps", "Tríceps",
            "Antebrazo", "Abdomen", "Pierna", "Glúteo", "Gemelo", "Trapecio",
            "➕ Crear Ejercicio Personalizado"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_entrenar);

        rvSesion = findViewById(R.id.rvTrainingSession);
        rvSesion.setLayoutManager(new LinearLayoutManager(this));

        adaptadorSesion = new InterfazListaEjerciciosSesion(listaSesion,
                new InterfazListaEjerciciosSesion.OnItemClickListener() {
                    @Override
                    public void onItemClick(int posicion) {
                        mostrarOpcionesSerie(posicion);
                    }

                    @Override
                    public void onItemLongClick(int posicion) {
                        confirmarEliminarEjercicio(posicion);
                    }
                });
        rvSesion.setAdapter(adaptadorSesion);

        androidx.appcompat.widget.SwitchCompat switchPublico = findViewById(R.id.switchPublico);
        switchPublico.setChecked(true);
        switchPublico.setOnCheckedChangeListener((btn, checked) -> sesionPublica = checked);

        ExtendedFloatingActionButton fabAdd = findViewById(R.id.fabAddExercise);
        fabAdd.setOnClickListener(v -> mostrarDialogoEjercicio());

        MaterialButton btnFinalizar = findViewById(R.id.btnFinalizarEntreno);
        btnFinalizar.setOnClickListener(v -> finalizarEntrenamiento());
    }

    private void mostrarDialogoEjercicio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar área o acción");

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this,
                R.layout.item_grupo_muscular, R.id.tvNombreMusculo, GRUPOS_MUSCULARES) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                return view;
            }
        };

        builder.setAdapter(adapter, (d, grupoIdx) -> {
            String grupoSeleccionado = GRUPOS_MUSCULARES[grupoIdx];

            if (grupoSeleccionado.contains("Personalizado")) {
                mostrarDialogoEjercicioPersonalizado();
                return;
            }

            new Thread(() -> {
                JSONArray ejercicios = ClienteApi.obtenerInstancia().obtenerEjerciciosPorGrupo(grupoSeleccionado);
                List<Ejercicio> listaEj = new ArrayList<>();
                for (int i = 0; i < ejercicios.length(); i++) {
                    try {
                        JSONObject obj = ejercicios.getJSONObject(i);
                        listaEj.add(new Ejercicio(
                                obj.optInt("id", 0),
                                obj.optString("nombre", ""),
                                obj.optString("parte_cuerpo", "")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> {
                    if (listaEj.isEmpty()) {
                        Toast.makeText(this, "No hay ejercicios en " + grupoSeleccionado, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] nombres = new String[listaEj.size()];
                    for (int i = 0; i < listaEj.size(); i++)
                        nombres[i] = listaEj.get(i).getName();

                    android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_ejercicios_maniqui,
                            null);
                    android.widget.ListView lvEjercicios = dialogView.findViewById(R.id.lvEjerciciosDialog);
                    android.widget.TextView tvTitulo = dialogView.findViewById(R.id.tvTituloDialog);

                    tvTitulo.setText("Ejercicios de " + grupoSeleccionado);

                    android.widget.ArrayAdapter<String> adapterEjercicios = new android.widget.ArrayAdapter<>(this,
                            android.R.layout.simple_list_item_1, nombres);
                    lvEjercicios.setAdapter(adapterEjercicios);

                    androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setView(dialogView)
                            .show();

                    lvEjercicios.setOnItemClickListener((parent, viewx, position, id) -> {
                        Ejercicio seleccionado = listaEj.get(position);
                        EjercicioSesion nuevo = new EjercicioSesion(seleccionado);
                        listaSesion.add(nuevo);
                        adaptadorSesion.notifyDataSetChanged();
                        agregarSerieDialog(listaSesion.size() - 1);
                        dialog.dismiss();
                    });
                });
            }).start();
        });
        builder.show();
    }

    private void mostrarDialogoEjercicioPersonalizado() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ejercicio Personalizado");

        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Ej: Sentadilla Búlgara...");
        builder.setView(inputNombre);

        builder.setPositiveButton("Añadir", (d, w) -> {
            String nombre = inputNombre.getText().toString().trim();
            if (!nombre.isEmpty()) {
                Ejercicio customEj = new Ejercicio(-1, nombre, "Personalizado");
                EjercicioSesion nuevo = new EjercicioSesion(customEj);
                listaSesion.add(nuevo);
                adaptadorSesion.notifyDataSetChanged();
                agregarSerieDialog(listaSesion.size() - 1);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarOpcionesSerie(int posicion) {
        String[] opciones = { "Añadir Serie", "Deshacer / Quitar última serie", "Eliminar Ejercicio Completo" };
        new AlertDialog.Builder(this)
                .setTitle("Opciones del Ejercicio")
                .setItems(opciones, (d, idx) -> {
                    if (idx == 0) {
                        agregarSerieDialog(posicion);
                    } else if (idx == 1) {
                        EjercicioSesion ej = listaSesion.get(posicion);
                        if (ej.getSeries().size() > 0) {
                            ej.getSeries().remove(ej.getSeries().size() - 1);
                            adaptadorSesion.notifyDataSetChanged();
                            Toast.makeText(this, "Serie eliminada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No hay series que quitar", Toast.LENGTH_SHORT).show();
                        }
                    } else if (idx == 2) {
                        confirmarEliminarEjercicio(posicion);
                    }
                })
                .show();
    }

    private void agregarSerieDialog(int posicion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir serie");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        EditText inputReps = new EditText(this);
        inputReps.setHint("Repeticiones");
        inputReps.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputReps);

        EditText inputPeso = new EditText(this);
        inputPeso.setHint("Peso (kg)");
        inputPeso.setInputType(
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPeso);

        builder.setView(layout);
        builder.setPositiveButton("Añadir", (d, w) -> {
            try {
                int reps = Integer.parseInt(inputReps.getText().toString().trim());
                double peso = Double.parseDouble(inputPeso.getText().toString().trim());
                listaSesion.get(posicion).addSerie(reps, peso);
                adaptadorSesion.notifyDataSetChanged();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmarEliminarEjercicio(int posicion) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Quitar " + listaSesion.get(posicion).getEjercicio().getName() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    listaSesion.remove(posicion);
                    adaptadorSesion.notifyDataSetChanged();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void finalizarEntrenamiento() {
        if (listaSesion.isEmpty()) {
            Toast.makeText(this, "Añade al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        String correo = GestorUsuarios.getInstance().getCorreoActual();
        if (correo == null) {
            Toast.makeText(this, "Error de sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        new Thread(() -> {
            // 1. Crear la rutina
            long rutinaId = ClienteApi.obtenerInstancia().crearRutina(fecha, correo, sesionPublica);

            if (rutinaId <= 0) {
                runOnUiThread(() -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show());
                return;
            }

            // Guardar individualmente cada Set para desglose futuro
            for (EjercicioSesion ej : listaSesion) {
                int numSerie = 1;
                for (EjercicioSesion.DetalleSerie serie : ej.getSeries()) {
                    ClienteApi.obtenerInstancia().agregarDetalleRutina(
                            rutinaId,
                            ej.getEjercicio().getName(),
                            numSerie,
                            serie.repeticiones,
                            serie.peso);
                    numSerie++;
                }
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "¡Entrenamiento guardado! 💪", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}
