package com.example.montefit;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.MatrixCursor;
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

public class SocialActivity extends AppCompatActivity {

    private Spinner spinnerEjercicios;
    private RecyclerView recyclerView;
    private TextView tvSemanaActual;
    private Button btnBuscarUsuario;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        dbHelper = UserManager.getInstance().getDbHelper();
        if (dbHelper == null) {
            UserManager.getInstance().init(this);
            dbHelper = UserManager.getInstance().getDbHelper();
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
        Cursor allExercises = dbHelper.getAllEjercicios();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ejerciciosPrincipales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEjercicios.setAdapter(adapter);

        spinnerEjercicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < ejercicioIds.length) {
                    loadRankingsForExercise(ejercicioIds[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadRankingsForExercise(int ejercicioId) {
        if (ejercicioId == 0) {
            Toast.makeText(this, "Ejercicio no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor rankingsCursor = dbHelper.getEstadisticasRanking(ejercicioId);
        if (rankingsCursor != null && rankingsCursor.getCount() > 0) {
            SocialAdapter adapter = new SocialAdapter(this, rankingsCursor);
            recyclerView.setAdapter(adapter);
        } else {
            // Empty adapter or clear list
            recyclerView.setAdapter(null);
            Toast.makeText(this, "Aún no hay rankings para este ejercicio esta semana", Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarDialogoBusquedaUsuario() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Buscar Usuario");

        final android.widget.EditText inputNombre = new android.widget.EditText(this);
        inputNombre.setHint("Nombre del usuario");
        inputNombre.setPadding(50, 40, 50, 40);
        builder.setView(inputNombre);

        builder.setPositiveButton("Buscar", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            if (!nombre.isEmpty()) {
                buscarYMostrarUsuario(nombre);
            } else {
                Toast.makeText(this, "Ingresa un nombre", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    @SuppressLint("Range")
    private void buscarYMostrarUsuario(String nombre) {
        Cursor cursor = dbHelper.getUserByName(nombre);

        if (cursor != null && cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex("id"));
            String nombreUsuario = cursor.getString(cursor.getColumnIndex("nombre"));
            cursor.close();

            mostrarEntrenamientosUsuario(userId, nombreUsuario);
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            if (cursor != null)
                cursor.close();
        }
    }

    private void mostrarEntrenamientosUsuario(int userId, String nombreUsuario) {
        Cursor entrenamientos = dbHelper.getUserTrainingsByUserId(userId);

        if (entrenamientos == null || entrenamientos.getCount() == 0) {
            Toast.makeText(this, nombreUsuario + " no tiene entrenamientos registrados", Toast.LENGTH_SHORT).show();
            if (entrenamientos != null)
                entrenamientos.close();
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Entrenamientos de ").append(nombreUsuario).append(":\n\n");

        int count = 0;
        while (entrenamientos.moveToNext() && count < 10) {
            @SuppressLint("Range")
            String fecha = entrenamientos.getString(entrenamientos.getColumnIndex("date"));
            @SuppressLint("Range")
            long rutinaId = entrenamientos.getLong(entrenamientos.getColumnIndex("id"));

            sb.append("📅 ").append(fecha).append("\n");


            Cursor detalles = dbHelper.getTrainingDetails(rutinaId);
            if (detalles != null && detalles.moveToFirst()) {
                do {
                    @SuppressLint("Range")
                    String ejercicio = detalles.getString(detalles.getColumnIndex("exercise_name"));
                    @SuppressLint("Range")
                    int series = detalles.getInt(detalles.getColumnIndex("series"));
                    @SuppressLint("Range")
                    int reps = detalles.getInt(detalles.getColumnIndex("reps"));
                    @SuppressLint("Range")
                    double weight = detalles.getDouble(detalles.getColumnIndex("weight"));

                    sb.append("  • ").append(ejercicio).append(": ")
                            .append(series).append("x").append(reps)
                            .append(" - ").append(weight).append("kg\n");
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
