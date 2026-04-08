package com.example.montefit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaEntrenar extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView visorSesion;
    private com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton botonAnadir;
    private com.google.android.material.button.MaterialButton botonFinalizar;
    private androidx.appcompat.widget.SwitchCompat switchPublico;

    private InterfazListaEjerciciosSesion miAdaptador;
    private java.util.List<EjercicioSesion> listaSesion;
    private GestorBaseDatos gestorBD;
    private String correoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_entrenar);

        gestorBD = GestorUsuarios.getInstance().getDbHelper();
        correoActual = GestorUsuarios.getInstance().getCurrentUserEmail();

        if (gestorBD == null || correoActual == null) {
            android.widget.Toast.makeText(this, "Error de sesión", android.widget.Toast.LENGTH_SHORT).show();
            GestorUsuarios.getInstance().init(this);
            gestorBD = GestorUsuarios.getInstance().getDbHelper();
            correoActual = "admin@montefit.com";
        }

        visorSesion = findViewById(R.id.rvTrainingSession);
        botonAnadir = findViewById(R.id.fabAddExercise);
        botonFinalizar = findViewById(R.id.btnFinalizarEntreno);
        switchPublico = findViewById(R.id.switchPublico);

        visorSesion.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        listaSesion = new java.util.ArrayList<>();
        miAdaptador = new InterfazListaEjerciciosSesion(listaSesion, new InterfazListaEjerciciosSesion.OnItemClickListener() {
            @Override
            public void onItemClick(int posicion) {
                showEditSessionItemDialog(posicion);
            }

            @Override
            public void onItemLongClick(int posicion) {
                showDeleteSessionItemDialog(posicion);
            }
        });
        visorSesion.setAdapter(miAdaptador);

        botonAnadir.setOnClickListener(v -> showSelectExerciseDialog());
        botonFinalizar.setOnClickListener(v -> finalizarEntrenamiento());
    }

    // Helper para obtener la imagen del grupo muscular
    private int getImagenMusculo(String grupoMuscular) {
        if (grupoMuscular == null) return 0;
        switch (grupoMuscular) {
            case "Pecho": return R.drawable.musculo_pecho;
            case "Espalda": return R.drawable.musculo_espalda;
            case "Hombro": return R.drawable.musculo_hombro;
            case "Biceps": return R.drawable.musculo_biceps;
            case "Triceps": return R.drawable.musculo_triceps;
            case "Pierna": return R.drawable.musculo_pierna;
            case "Abdomen": return R.drawable.musculo_abdomen;
            case "Gluteos": return R.drawable.musculo_gluteos;
            case "Gemelos": return R.drawable.musculo_gemelos;
            case "Trapecio": return R.drawable.musculo_trapecio;
            case "Antebrazo": return R.drawable.musculo_antebrazo;
            default: return 0;
        }
    }

    private void showSelectExerciseDialog() {
        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this, 
                com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog);
        constructorDialogo.setTitle("💪 Seleccionar Ejercicio");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(30, 20, 30, 10);

        // Imagen del grupo muscular
        final android.widget.ImageView imgMusculo = new android.widget.ImageView(this);
        imgMusculo.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 300));
        imgMusculo.setScaleType(android.widget.ImageView.ScaleType.FIT_CENTER);
        imgMusculo.setImageResource(R.drawable.musculo_pecho); // Default
        contenedor.addView(imgMusculo);

        // Spinner parteCuerpo
        final android.widget.Spinner spinnerBodyPart = new android.widget.Spinner(this);
        String[] parts = { "Todos", "Pecho", "Espalda", "Hombro", "Biceps", "Triceps", "Pierna", 
                          "Abdomen", "Gluteos", "Gemelos", "Trapecio", "Antebrazo" };
        android.widget.ArrayAdapter<String> adapterParts = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, parts);
        spinnerBodyPart.setAdapter(adapterParts);
        android.widget.LinearLayout.LayoutParams spinnerParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
        spinnerParams.topMargin = 16;
        spinnerBodyPart.setLayoutParams(spinnerParams);
        contenedor.addView(spinnerBodyPart);

        // List Exercises
        final android.widget.ListView listExercises = new android.widget.ListView(this);
        android.widget.LinearLayout.LayoutParams listParams = new android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 400);
        listParams.topMargin = 8;
        listExercises.setLayoutParams(listParams);
        contenedor.addView(listExercises);

        // Button Add Custom
        final android.widget.Button btnCustom = new android.widget.Button(this);
        btnCustom.setText("+ Crear Ejercicio Personalizado");
        btnCustom.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        btnCustom.setTextColor(getResources().getColor(R.color.accent_power));
        contenedor.addView(btnCustom);

        constructorDialogo.setView(contenedor);
        final android.app.AlertDialog miDialogo = constructorDialogo.create();

        // Load exercises initially
        loadExercisesIntoList(listExercises, "Todos", miDialogo);

        spinnerBodyPart.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> padre, android.view.View vista, int posicion,
                    long id) {
                String selectedPart = parts[posicion];
                loadExercisesIntoList(listExercises, selectedPart, miDialogo);
                
                // Actualizar imagen del grupo muscular
                if (!"Todos".equals(selectedPart)) {
                    int imgRes = getImagenMusculo(selectedPart);
                    if (imgRes != 0) {
                        imgMusculo.setImageResource(imgRes);
                        imgMusculo.setVisibility(android.view.View.VISIBLE);
                    } else {
                        imgMusculo.setVisibility(android.view.View.GONE);
                    }
                } else {
                    imgMusculo.setVisibility(android.view.View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> padre) {
            }
        });

        btnCustom.setOnClickListener(v -> {
            miDialogo.dismiss();
            showCreateCustomExerciseDialog();
        });

        miDialogo.show();
    }

    private void loadExercisesIntoList(android.widget.ListView listView, String parteCuerpo,
            android.app.AlertDialog parentDialog) {
        java.util.List<Ejercicio> exercises = new java.util.ArrayList<>();
        android.database.Cursor datosBD = gestorBD.getExercisesByBodyPart(parteCuerpo);
        if (datosBD != null && datosBD.moveToFirst()) {
            do {
                int id = datosBD.getInt(datosBD.getColumnIndexOrThrow("id"));
                String nombre = datosBD.getString(datosBD.getColumnIndexOrThrow("nombre"));
                String bp = datosBD.getString(datosBD.getColumnIndexOrThrow("parte_cuerpo"));
                exercises.add(new Ejercicio(id, nombre, bp));
            } while (datosBD.moveToNext());
            datosBD.close();
        }

        android.widget.ArrayAdapter<Ejercicio> adaptadorEjercicios = new android.widget.ArrayAdapter<>(this,
                R.layout.item_lista_simple, exercises);
        listView.setAdapter(adaptadorEjercicios);

        listView.setOnItemClickListener((padre, vista, posicion, id) -> {
            Ejercicio selected = exercises.get(posicion);
            parentDialog.dismiss();
            showAddSetsDialog(selected);
        });

        listView.setOnItemLongClickListener((padre, vista, posicion, id) -> {
            Ejercicio selected = exercises.get(posicion);
            showExerciseOptionsDialog(selected, () -> loadExercisesIntoList(listView, parteCuerpo, parentDialog));
            return true;
        });
    }

    private void showExerciseOptionsDialog(Ejercicio Ejercicio, Runnable onRefresh) {
        String[] options = { "Editar", "Eliminar" };
        new android.app.AlertDialog.Builder(this)
                .setTitle(Ejercicio.getName())
                .setItems(options, (miDialogo, which) -> {
                    if (which == 0) {
                        showCreateCustomExerciseDialog(Ejercicio, onRefresh);
                    } else if (which == 1) {
                        new android.app.AlertDialog.Builder(this)
                                .setMessage("¿Eliminar " + Ejercicio.getName() + "?")
                                .setPositiveButton("Sí", (d, w) -> {
                                    if (gestorBD.deleteExercise(Ejercicio.getId())) {
                                        android.widget.Toast.makeText(this, "Ejercicio eliminado",
                                                android.widget.Toast.LENGTH_SHORT).show();
                                        onRefresh.run();
                                    } else {
                                        android.widget.Toast
                                                .makeText(this, "Error al eliminar", android.widget.Toast.LENGTH_SHORT)
                                                .show();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                })
                .show();
    }

    private void showCreateCustomExerciseDialog() {
        showCreateCustomExerciseDialog(null, null);
    }

    private void showCreateCustomExerciseDialog(Ejercicio exerciseToEdit, Runnable onRefresh) {
        boolean isEditing = (exerciseToEdit != null);
        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle(isEditing ? "Editar Ejercicio" : "Nuevo Ejercicio");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(40, 20, 40, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre del Ejercicio");
        if (isEditing)
            inputName.setText(exerciseToEdit.getName());
        contenedor.addView(inputName);

        final android.widget.Spinner spinnerBodyPart = new android.widget.Spinner(this);
        String[] parts = { "Pecho", "Espalda", "Hombro", "Biceps", "Triceps", "Pierna", 
                          "Abdomen", "Gluteos", "Gemelos", "Trapecio", "Antebrazo" };
        android.widget.ArrayAdapter<String> adapterParts = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, parts);
        spinnerBodyPart.setAdapter(adapterParts);
        if (isEditing) {
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals(exerciseToEdit.getBodyPart())) {
                    spinnerBodyPart.setSelection(i);
                    break;
                }
            }
        }
        contenedor.addView(spinnerBodyPart);

        constructorDialogo.setView(contenedor);
        constructorDialogo.setPositiveButton("Guardar", (miDialogo, which) -> {
            String nombre = inputName.getText().toString();
            String part = spinnerBodyPart.getSelectedItem().toString();
            if (!nombre.isEmpty()) {
                if (isEditing) {
                    if (gestorBD.updateExercise(exerciseToEdit.getId(), nombre, part)) {
                        android.widget.Toast.makeText(this, "Actualizado", android.widget.Toast.LENGTH_SHORT).show();
                        if (onRefresh != null)
                            onRefresh.run();
                    } else {
                        android.widget.Toast.makeText(this, "Error al actualizar", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (gestorBD.addExercise(nombre, part)) {
                        android.widget.Toast.makeText(this, "Ejercicio Creado", android.widget.Toast.LENGTH_SHORT)
                                .show();
                        if (onRefresh != null) {
                            onRefresh.run();
                        } else {
                            showSelectExerciseDialog();
                        }
                    } else {
                        android.widget.Toast.makeText(this, "Error al crear", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                android.widget.Toast.makeText(this, "El nombre no puede estar vacío", android.widget.Toast.LENGTH_SHORT)
                        .show();
            }
        });
        constructorDialogo.setNegativeButton("Cancelar", null);
        constructorDialogo.show();
    }

    private void showAddSetsDialog(Ejercicio Ejercicio) {
        EjercicioSesion sessionItem = new EjercicioSesion(Ejercicio);

        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle(Ejercicio.getName());
        constructorDialogo.setMessage("Añadir series y repeticiones");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(40, 10, 40, 10);

        final android.widget.EditText inputSeries = new android.widget.EditText(this);
        inputSeries.setHint("Número de Series");
        inputSeries.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputSeries.setTextColor(android.graphics.Color.WHITE);
        inputSeries.setHintTextColor(android.graphics.Color.LTGRAY);
        contenedor.addView(inputSeries);

        final android.widget.EditText inputReps = new android.widget.EditText(this);
        inputReps.setHint("Repeticiones por serie");
        inputReps.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputReps.setTextColor(android.graphics.Color.WHITE);
        inputReps.setHintTextColor(android.graphics.Color.LTGRAY);
        contenedor.addView(inputReps);

        final android.widget.EditText inputWeight = new android.widget.EditText(this);
        inputWeight.setHint("Peso (kg)");
        inputWeight.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputWeight.setTextColor(android.graphics.Color.WHITE);
        inputWeight.setHintTextColor(android.graphics.Color.LTGRAY);
        contenedor.addView(inputWeight);

        constructorDialogo.setView(contenedor);
        constructorDialogo.setPositiveButton("Añadir", (miDialogo, which) -> {
            try {
                int series = Integer.parseInt(inputSeries.getText().toString());
                int repeticiones = Integer.parseInt(inputReps.getText().toString());
                double peso = Double.parseDouble(inputWeight.getText().toString());

                for (int i = 0; i < series; i++) {
                    sessionItem.addSerie(repeticiones, peso);
                }
                listaSesion.add(sessionItem);
                miAdaptador.notifyDataSetChanged();

            } catch (Exception e) {
                android.widget.Toast.makeText(this, "Datos inválidos", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        constructorDialogo.setNegativeButton("Cancelar", null);
        constructorDialogo.show();
    }

    private void finalizarEntrenamiento() {
        if (listaSesion.isEmpty())
            return;

        boolean esPublico = switchPublico.isChecked();
        long trainingId = gestorBD.addTraining(getDate(), correoActual, esPublico);
        if (trainingId != -1) {
            for (EjercicioSesion elemento : listaSesion) {
                for (EjercicioSesion.DetalleSerie set : elemento.getSeries()) {
                    gestorBD.addTrainingDetail(trainingId, elemento.getEjercicio().getName(), 1, set.repeticiones, set.peso);
                }
            }
            android.widget.Toast.makeText(this, "Entrenamiento Guardado", android.widget.Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteSessionItemDialog(int posicion) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Quitar de la sesión?")
                .setPositiveButton("Sí", (miDialogo, which) -> {
                    listaSesion.remove(posicion);
                    miAdaptador.notifyItemRemoved(posicion);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditSessionItemDialog(int posicion) {
        EjercicioSesion elemento = listaSesion.get(posicion);
        java.util.List<EjercicioSesion.DetalleSerie> sets = elemento.getSeries();

        int currentSeries = sets.size();
        int currentReps = currentSeries > 0 ? sets.get(0).repeticiones : 0;
        double currentWeight = currentSeries > 0 ? sets.get(0).peso : 0;

        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle("Editar " + elemento.getEjercicio().getName());

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(40, 10, 40, 10);

        final android.widget.EditText inputSeries = new android.widget.EditText(this);
        inputSeries.setHint("Número de Series");
        inputSeries.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputSeries.setText(String.valueOf(currentSeries));
        contenedor.addView(inputSeries);

        final android.widget.EditText inputReps = new android.widget.EditText(this);
        inputReps.setHint("Repeticiones por serie");
        inputReps.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputReps.setText(String.valueOf(currentReps));
        contenedor.addView(inputReps);

        final android.widget.EditText inputWeight = new android.widget.EditText(this);
        inputWeight.setHint("Peso (kg)");
        inputWeight.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputWeight.setText(String.valueOf(currentWeight));
        contenedor.addView(inputWeight);

        constructorDialogo.setView(contenedor);
        constructorDialogo.setPositiveButton("Guardar", (miDialogo, which) -> {
            try {
                int series = Integer.parseInt(inputSeries.getText().toString());
                int repeticiones = Integer.parseInt(inputReps.getText().toString());
                double peso = Double.parseDouble(inputWeight.getText().toString());

                elemento.getSeries().clear();
                for (int i = 0; i < series; i++) {
                    elemento.addSerie(repeticiones, peso);
                }
                miAdaptador.notifyItemChanged(posicion);

            } catch (Exception e) {
                android.widget.Toast.makeText(this, "Datos inválidos", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        constructorDialogo.setNegativeButton("Cancelar", null);
        constructorDialogo.show();
    }

    private String getDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}
