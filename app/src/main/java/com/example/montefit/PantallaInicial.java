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

public class PantallaInicial extends AppCompatActivity {

        private Button btnEntrenar, btnComida, btnPerfil, btnLogros, btnSocial;
        private RecyclerView rvEntrenamientos;
        private InterfazListaEntrenamientos miAdaptador;
        private List<Entrenamiento> listaEntrenamientos;
        private GestorBaseDatos gestorBD;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                EdgeToEdge.enable(this);
                setContentView(R.layout.pantalla_inicial);

                gestorBD = GestorUsuarios.getInstance().getDbHelper();
                if (gestorBD == null) {
                        GestorUsuarios.getInstance().init(this);
                        gestorBD = GestorUsuarios.getInstance().getDbHelper();
                }
                btnEntrenar = findViewById(R.id.btnEntrenar);
                btnComida = findViewById(R.id.btnComida);
                btnPerfil = findViewById(R.id.btnPerfil);
                btnLogros = findViewById(R.id.btnLogros);
                btnSocial = findViewById(R.id.btnSocial);
                rvEntrenamientos = findViewById(R.id.rvEntrenamientos);
                rvEntrenamientos.setLayoutManager(new LinearLayoutManager(this));
                listaEntrenamientos = new ArrayList<>();
                miAdaptador = new InterfazListaEntrenamientos(this, listaEntrenamientos, posicion -> {
                        showWorkoutOptionsDialog(posicion);
                });
                rvEntrenamientos.setAdapter(miAdaptador);

                // Set Listeners
                btnEntrenar.setOnClickListener(
                                v -> startActivity(new Intent(PantallaInicial.this, PantallaEntrenar.class)));

                btnComida
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicial.this, PantallaComida.class)));

                btnPerfil
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicial.this, PantallaPerfil.class)));

                btnLogros
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicial.this, PantallaLogros.class)));

                btnSocial
                                .setOnClickListener(v -> startActivity(
                                                new Intent(PantallaInicial.this, PantallaSocial.class)));
        }

        @Override
        protected void onResume() {
                super.onResume();
                loadWorkouts();
        }

        private void loadWorkouts() {
                String correo = GestorUsuarios.getInstance().getCurrentUserEmail();
                if (correo == null)
                        return;

                listaEntrenamientos.clear();
                Cursor datosBD = gestorBD.getUserTrainings(correo);
                if (datosBD != null && datosBD.moveToFirst()) {
                        do {
                                long id = datosBD.getLong(datosBD.getColumnIndexOrThrow("id"));
                                String date = datosBD.getString(datosBD.getColumnIndexOrThrow("date"));
                                boolean esPublico = datosBD.getInt(datosBD.getColumnIndexOrThrow("es_publico")) == 1;
                                List<Entrenamiento.EjercicioDetalle> ejerciciosDetalle = new ArrayList<>();
                                Cursor detailCursor = gestorBD.getTrainingDetails(id);
                                if (detailCursor != null && detailCursor.moveToFirst()) {
                                        do {
                                                String nombre = detailCursor.getString(
                                                                detailCursor.getColumnIndexOrThrow("nombre_ejercicio"));
                                                int series = detailCursor
                                                                .getInt(detailCursor.getColumnIndexOrThrow("series"));
                                                double peso = detailCursor.getDouble(
                                                                detailCursor.getColumnIndexOrThrow("peso"));
                                                boolean found = false;
                                                for (Entrenamiento.EjercicioDetalle ed : ejerciciosDetalle) {
                                                        if (ed.nombre.equals(nombre) && ed.peso == peso) {
                                                                ed.series += series;
                                                                found = true;
                                                                break;
                                                        }
                                                }
                                                if (!found) {
                                                        ejerciciosDetalle.add(new Entrenamiento.EjercicioDetalle(nombre,
                                                                        series, peso));
                                                }
                                        } while (detailCursor.moveToNext());
                                        detailCursor.close();
                                }

                                listaEntrenamientos.add(new Entrenamiento(id, date, ejerciciosDetalle, esPublico));
                        } while (datosBD.moveToNext());
                        datosBD.close();
                }
                miAdaptador.notifyDataSetChanged();
        }

        private void showWorkoutOptionsDialog(int posicion) {
                Entrenamiento e = listaEntrenamientos.get(posicion);
                String visibilidadTexto = e.isPublico() ? "Hacer Privado 🔒" : "Hacer Público 🌐";
                String[] options = { visibilidadTexto, "Eliminar" };

                new AlertDialog.Builder(this)
                                .setTitle("Opciones del Entrenamiento")
                                .setItems(options, (miDialogo, which) -> {
                                        if (which == 0) {
                                                // Toggle visibilidad
                                                if (gestorBD.toggleEntrenamientoPublico(e.getId())) {
                                                        e.setPublico(!e.isPublico());
                                                        miAdaptador.notifyItemChanged(posicion);
                                                        Toast.makeText(this,
                                                                        e.isPublico() ? "Entrenamiento público" : "Entrenamiento privado",
                                                                        Toast.LENGTH_SHORT).show();
                                                }
                                        } else if (which == 1) {
                                                showDeleteWorkoutDialog(posicion);
                                        }
                                })
                                .show();
        }

        private void showDeleteWorkoutDialog(int posicion) {
                Entrenamiento e = listaEntrenamientos.get(posicion);
                new AlertDialog.Builder(this)
                                .setTitle("Eliminar entrenamiento")
                                .setMessage("¿Estás seguro de que quieres eliminar este entrenamiento?")
                                .setPositiveButton("Eliminar", (miDialogo, which) -> {
                                        if (gestorBD.deleteTraining(e.getId())) {
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
