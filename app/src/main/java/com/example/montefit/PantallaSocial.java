package com.example.montefit;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Calendar;

public class PantallaSocial extends AppCompatActivity {

    private Spinner spinnerEjercicios;
    private RecyclerView recyclerView;
    private TextView tvSemanaActual;
    private Button btnBuscarUsuario;
    private GestorBaseDatos gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_social);

        gestorBD = GestorUsuarios.getInstance().getDbHelper();
        if (gestorBD == null) {
            GestorUsuarios.getInstance().init(this);
            gestorBD = GestorUsuarios.getInstance().getDbHelper();
        }

        spinnerEjercicios = findViewById(R.id.spinnerEjerciciosRanking);
        recyclerView = findViewById(R.id.recyclerRanking);
        tvSemanaActual = findViewById(R.id.tvSemanaActual);
        btnBuscarUsuario = findViewById(R.id.btnBuscarUsuario);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Mostrar semana actual
        Calendar cal = Calendar.getInstance();
        int semana = cal.get(Calendar.WEEK_OF_YEAR);
        int anio = cal.get(Calendar.YEAR);
        tvSemanaActual.setText("Ranking Semanal: Semana " + semana + " - " + anio);

        btnBuscarUsuario.setOnClickListener(v -> mostrarDialogoBusquedaUsuario());

        setupSpinner();
    }

    private void setupSpinner() {
        String[] ejerciciosPrincipales = { "Press Banca", "Peso Muerto", "Sentadilla" };
        final int[] ejercicioIds = new int[3];
        Cursor allExercises = gestorBD.getAllEjercicios();
        if (allExercises != null && allExercises.moveToFirst()) {
            do {
                @SuppressLint("Range")
                String nombre = allExercises.getString(allExercises.getColumnIndex("nombre"));
                @SuppressLint("Range")
                int id = allExercises.getInt(allExercises.getColumnIndex("_id"));

                for (int i = 0; i < ejerciciosPrincipales.length; i++) {
                    if (nombre.equalsIgnoreCase(ejerciciosPrincipales[i])) {
                        ejercicioIds[i] = id;
                        break;
                    }
                }
            } while (allExercises.moveToNext());
            allExercises.close();
        }
        boolean todosEncontrados = true;
        for (int i = 0; i < ejercicioIds.length; i++) {
            if (ejercicioIds[i] == 0) {
                Toast.makeText(this, "Ejercicio '" + ejerciciosPrincipales[i] + "' no encontrado. Reinstala la app.",
                        Toast.LENGTH_LONG).show();
                todosEncontrados = false;
            }
        }

        if (!todosEncontrados) {
            finish();
            return;
        }
        ArrayAdapter<String> miAdaptador = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                ejerciciosPrincipales);
        miAdaptador.setDropDownViewResource(R.layout.item_spinner);
        spinnerEjercicios.setAdapter(miAdaptador);

        spinnerEjercicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> padre, View vista, int posicion, long id) {
                if (posicion >= 0 && posicion < ejercicioIds.length) {
                    loadRankingsForExercise(ejercicioIds[posicion]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> padre) {
            }
        });
    }

    private void loadRankingsForExercise(int ejercicioId) {
        if (ejercicioId == 0) {
            Toast.makeText(this, "Ejercicio no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor rankingsCursor = gestorBD.getEstadisticasRanking(ejercicioId);
        if (rankingsCursor != null && rankingsCursor.getCount() > 0) {
            InterfazListaSocial miAdaptador = new InterfazListaSocial(this, rankingsCursor);
            recyclerView.setAdapter(miAdaptador);
        } else {
            recyclerView.setAdapter(null);
            Toast.makeText(this, "Aún no hay rankings para este ejercicio esta semana", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoBusquedaUsuario() {
        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle("Buscar Usuario");

        final android.widget.EditText inputNombre = new android.widget.EditText(this);
        inputNombre.setHint("Nombre del usuario");
        inputNombre.setPadding(50, 40, 50, 40);
        inputNombre.setTextColor(android.graphics.Color.WHITE);
        inputNombre.setHintTextColor(android.graphics.Color.LTGRAY);
        constructorDialogo.setView(inputNombre);

        constructorDialogo.setPositiveButton("Buscar", (miDialogo, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            if (!nombre.isEmpty()) {
                buscarYMostrarUsuario(nombre);
            } else {
                Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show();
            }
        });
        constructorDialogo.setNegativeButton("Cancelar", null);
        constructorDialogo.show();
    }

    @SuppressLint("Range")
    private void buscarYMostrarUsuario(String nombre) {
        Cursor datosBD = gestorBD.getUserByName(nombre);

        if (datosBD != null && datosBD.moveToFirst()) {
            int userId = datosBD.getInt(datosBD.getColumnIndex("id"));
            String nombreUsuario = datosBD.getString(datosBD.getColumnIndex("nombre"));
            datosBD.close();

            mostrarEntrenamientosUsuario(userId, nombreUsuario);
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            if (datosBD != null)
                datosBD.close();
        }
    }

    private void mostrarEntrenamientosUsuario(int userId, String nombreUsuario) {
        // Solo mostramos entrenamientos PUBLICOS de otros usuarios
        Cursor entrenamientos = gestorBD.getUserPublicTrainingsByUserId(userId);

        if (entrenamientos == null || entrenamientos.getCount() == 0) {
            Toast.makeText(this, nombreUsuario + " no tiene entrenamientos públicos", Toast.LENGTH_SHORT).show();
            if (entrenamientos != null)
                entrenamientos.close();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Entrenamientos públicos de ").append(nombreUsuario).append(":\n\n");

        int count = 0;
        while (entrenamientos.moveToNext() && count < 10) {
            @SuppressLint("Range")
            String fecha = entrenamientos.getString(entrenamientos.getColumnIndex("date"));
            @SuppressLint("Range")
            long rutinaId = entrenamientos.getLong(entrenamientos.getColumnIndex("id"));

            sb.append("📅 ").append(fecha).append("\n");

            Cursor detalles = gestorBD.getTrainingDetails(rutinaId);
            if (detalles != null && detalles.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    String ejercicio = detalles.getString(detalles.getColumnIndex("nombre_ejercicio"));
                    @SuppressLint("Range")
                    int series = detalles.getInt(detalles.getColumnIndex("series"));
                    @SuppressLint("Range")
                    int repeticiones = detalles.getInt(detalles.getColumnIndex("repeticiones"));
                    @SuppressLint("Range")
                    double peso = detalles.getDouble(detalles.getColumnIndex("peso"));

                    sb.append("  • ").append(ejercicio).append(": ")
                            .append(series).append("x").append(repeticiones)
                            .append(" - ").append(peso).append("kg\n");
                } while (detalles.moveToNext());
                detalles.close();
            }
            sb.append("\n");
            count++;
        }
        entrenamientos.close();

        if (count >= 10) {
            sb.append("(Mostrando solo los primeros 10 entrenamientos)");
        }

        // Mostrar en un diálogo
        new android.app.AlertDialog.Builder(this)
                .setTitle("Perfil de " + nombreUsuario)
                .setMessage(sb.toString())
                .setPositiveButton("Cerrar", null)
                .show();
    }
}
