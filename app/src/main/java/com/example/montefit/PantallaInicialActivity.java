package com.example.montefit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantallaInicialActivity extends AppCompatActivity {

        private Button btnEntrenar, btnComida, btnPerfil, btnLogros, btnSocial;
        private RecyclerView rvEntrenamientos;
        private EntrenamientoAdapter adapter;
        private List<Entrenamiento> listaEntrenamientos;
        private DatabaseHelper dbHelper;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                EdgeToEdge.enable(this);
                setContentView(R.layout.pantalla_inicial);

                dbHelper = UserManager.getInstance().getDbHelper();
                if (dbHelper == null) {
                        UserManager.getInstance().init(this);
                        dbHelper = UserManager.getInstance().getDbHelper();
                }
                btnEntrenar = findViewById(R.id.btnEntrenar);
                btnComida = findViewById(R.id.btnComida);
                btnPerfil = findViewById(R.id.btnPerfil);
                btnLogros = findViewById(R.id.btnLogros);
                btnSocial = findViewById(R.id.btnSocial);
                rvEntrenamientos = findViewById(R.id.rvEntrenamientos);
                rvEntrenamientos.setLayoutManager(new LinearLayoutManager(this));
                listaEntrenamientos = new ArrayList<>();
                adapter = new EntrenamientoAdapter(this, listaEntrenamientos, position -> {
                        showDeleteWorkoutDialog(position);
                });
                rvEntrenamientos.setAdapter(adapter);

                // Set Listeners
                btnEntrenar.setOnClickListener(
                                v -> startActivity(new Intent(PantallaInicialActivity.this, EntrenarActivity.class)));

                btnComida
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicialActivity.this, ComidaActivity.class)));

                btnPerfil
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicialActivity.this, PerfilActivity.class)));

                btnLogros
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicialActivity.this, LogrosActivity.class)));

                btnSocial
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicialActivity.this, SocialActivity.class)));
        }

        @Override
        protected void onResume() {
                super.onResume();
                loadWorkouts();
        }

        private void loadWorkouts() {
                String email = UserManager.getInstance().getCurrentUserEmail();
                if (email == null)
                        return;

                listaEntrenamientos.clear();
                Cursor cursor = dbHelper.getUserTrainings(email);
                if (cursor != null && cursor.moveToFirst()) {
                        do {
                                long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
                                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                                List<Entrenamiento.EjercicioDetalle> ejerciciosDetalle = new ArrayList<>();
                                Cursor detailCursor = dbHelper.getTrainingDetails(id);
                                if (detailCursor != null && detailCursor.moveToFirst()) {
                                        do {
                                                String name = detailCursor.getString(
                                                                detailCursor.getColumnIndexOrThrow("exercise_name"));
                                                int series = detailCursor
                                                                .getInt(detailCursor.getColumnIndexOrThrow("series"));
                                                double weight = detailCursor.getDouble(
                                                                detailCursor.getColumnIndexOrThrow("weight"));
                                                boolean found = false;
                                                for (Entrenamiento.EjercicioDetalle ed : ejerciciosDetalle) {
                                                        if (ed.nombre.equals(name) && ed.peso == weight) {
                                                                ed.series += series;
                                                                found = true;
                                                                break;
                                                        }
                                                }
                                                if (!found) {
                                                        ejerciciosDetalle.add(new Entrenamiento.EjercicioDetalle(name,
                                                                        series, weight));
                                                }
                                        } while (detailCursor.moveToNext());
                                        detailCursor.close();
                                }

                                listaEntrenamientos.add(new Entrenamiento(id, date, ejerciciosDetalle));
                        } while (cursor.moveToNext());
                        cursor.close();
                }
                adapter.notifyDataSetChanged();
        }

        private void showDeleteWorkoutDialog(int position) {
                Entrenamiento e = listaEntrenamientos.get(position);
                new AlertDialog.Builder(this)
                                .setTitle("Eliminar entrenamiento")
                                .setMessage("¿Estás seguro de que quieres eliminar este entrenamiento?")
                                .setPositiveButton("Eliminar", (dialog, which) -> {
                                        if (dbHelper.deleteTraining(e.getId())) {
                                                Toast.makeText(this, "Entrenamiento eliminado", Toast.LENGTH_SHORT)
                                                                .show();
                                                loadWorkouts();
                                        } else {
                                                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                                        }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
        }
}
