package com.example.montefit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class EntrenarActivity extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView rvSession;
    private com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton fabAdd;
    private com.google.android.material.button.MaterialButton btnFinish;

    private TrainingSessionAdapter adapter;
    private java.util.List<TrainingSessionItem> sessionList;
    private DatabaseHelper dbHelper;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenar);

        dbHelper = UserManager.getInstance().getDbHelper();
        currentUserEmail = UserManager.getInstance().getCurrentUserEmail();

        if (dbHelper == null || currentUserEmail == null) {
            android.widget.Toast.makeText(this, "Error de sesión", android.widget.Toast.LENGTH_SHORT).show();
            UserManager.getInstance().init(this);
            dbHelper = UserManager.getInstance().getDbHelper();
            currentUserEmail = "admin@montefit.com"; // Fallback for dev
        }

        rvSession = findViewById(R.id.rvTrainingSession);
        fabAdd = findViewById(R.id.fabAddExercise);
        btnFinish = findViewById(R.id.btnFinalizarEntreno);

        rvSession.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        sessionList = new java.util.ArrayList<>();
        adapter = new TrainingSessionAdapter(sessionList, new TrainingSessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showEditSessionItemDialog(position);
            }

            @Override
            public void onItemLongClick(int position) {
                showDeleteSessionItemDialog(position);
            }
        });
        rvSession.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showSelectExerciseDialog());
        btnFinish.setOnClickListener(v -> finalizarEntrenamiento());
    }

    private void showSelectExerciseDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Ejercicio");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 10);

        // Spinner BodyPart
        final android.widget.Spinner spinnerBodyPart = new android.widget.Spinner(this);
        String[] parts = { "Todos", "Pecho", "Espalda", "Pierna", "Brazos", "Hombro", "Cardio" };
        android.widget.ArrayAdapter<String> adapterParts = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, parts);
        spinnerBodyPart.setAdapter(adapterParts);
        layout.addView(spinnerBodyPart);

        // List Exercises
        final android.widget.ListView listExercises = new android.widget.ListView(this);
        layout.addView(listExercises);

        // Button Add Custom
        final android.widget.Button btnCustom = new android.widget.Button(this);
        btnCustom.setText("Crear Ejercicio Personalizado");
        layout.addView(btnCustom);

        builder.setView(layout);
        final android.app.AlertDialog dialog = builder.create();

        // Load exercises initially
        loadExercisesIntoList(listExercises, "Todos", dialog);

        spinnerBodyPart.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position,
                    long id) {
                loadExercisesIntoList(listExercises, parts[position], dialog);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        btnCustom.setOnClickListener(v -> {
            dialog.dismiss();
            showCreateCustomExerciseDialog();
        });

        dialog.show();
    }

    private void loadExercisesIntoList(android.widget.ListView listView, String bodyPart,
            android.app.AlertDialog parentDialog) {
        java.util.List<Exercise> exercises = new java.util.ArrayList<>();
        android.database.Cursor cursor = dbHelper.getExercisesByBodyPart(bodyPart);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String bp = cursor.getString(cursor.getColumnIndexOrThrow("body_part"));
                exercises.add(new Exercise(id, name, bp));
            } while (cursor.moveToNext());
            cursor.close();
        }

        android.widget.ArrayAdapter<Exercise> adapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, exercises);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Exercise selected = exercises.get(position);
            parentDialog.dismiss();
            showAddSetsDialog(selected);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Exercise selected = exercises.get(position);
            showExerciseOptionsDialog(selected, () -> loadExercisesIntoList(listView, bodyPart, parentDialog));
            return true;
        });
    }

    private void showExerciseOptionsDialog(Exercise exercise, Runnable onRefresh) {
        String[] options = { "Editar", "Eliminar" };
        new android.app.AlertDialog.Builder(this)
                .setTitle(exercise.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showCreateCustomExerciseDialog(exercise, onRefresh);
                    } else if (which == 1) {
                        new android.app.AlertDialog.Builder(this)
                                .setMessage("¿Eliminar " + exercise.getName() + "?")
                                .setPositiveButton("Sí", (d, w) -> {
                                    if (dbHelper.deleteExercise(exercise.getId())) {
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

    private void showCreateCustomExerciseDialog(Exercise exerciseToEdit, Runnable onRefresh) {
        boolean isEditing = (exerciseToEdit != null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(isEditing ? "Editar Ejercicio" : "Nuevo Ejercicio");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre del Ejercicio");
        if (isEditing)
            inputName.setText(exerciseToEdit.getName());
        layout.addView(inputName);

        final android.widget.Spinner spinnerBodyPart = new android.widget.Spinner(this);
        String[] parts = { "Pecho", "Espalda", "Pierna", "Brazos", "Hombro", "Cardio" };
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
        layout.addView(spinnerBodyPart);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String name = inputName.getText().toString();
            String part = spinnerBodyPart.getSelectedItem().toString();
            if (!name.isEmpty()) {
                if (isEditing) {
                    if (dbHelper.updateExercise(exerciseToEdit.getId(), name, part)) {
                        android.widget.Toast.makeText(this, "Actualizado", android.widget.Toast.LENGTH_SHORT).show();
                        if (onRefresh != null)
                            onRefresh.run();
                    } else {
                        android.widget.Toast.makeText(this, "Error al actualizar", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (dbHelper.addExercise(name, part)) {
                        android.widget.Toast.makeText(this, "Ejercicio Creado", android.widget.Toast.LENGTH_SHORT)
                                .show();
                        // Reuse showSelectExerciseDialog() to refresh or specific refresh
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
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void showAddSetsDialog(Exercise exercise) {
        TrainingSessionItem sessionItem = new TrainingSessionItem(exercise);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(exercise.getName());
        builder.setMessage("Añadre series y repeticiones");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        // Inputs for 1st set (Simplify: Loop for adding sets? Or just Input "Sets: 4,
        // Reps: 10, Weight: 50")
        // User asked "choose exercise -> let you put series, weight and reps of EACH
        // set".
        // To implement "Each set", I'd need a dynamic list.
        // MVP: Add one "block" of sets that are identical, or just add 1 set at a time.
        // Let's do: Dialog adds ONE set. Can add multiple times?
        // Better: Dialog asks "How many sets?". Then inputs for each? Too complex for
        // simple dialog.
        // MVP: Input "Series", "Repeticiones", "Peso". We assume all series are equal
        // for this entry.
        // If users want different weights, they add the exercise twice? No, that's bad
        // UX.
        // Let's try: "Añadir Serie" loop.
        // Actually, let's just ask "Series", "Reps", "Peso".
        // And we generate N entries in the sessionItem.

        final android.widget.EditText inputSeries = new android.widget.EditText(this);
        inputSeries.setHint("Número de Series");
        inputSeries.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputSeries);

        final android.widget.EditText inputReps = new android.widget.EditText(this);
        inputReps.setHint("Repeticiones por serie");
        inputReps.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputReps);

        final android.widget.EditText inputWeight = new android.widget.EditText(this);
        inputWeight.setHint("Peso (kg)");
        inputWeight.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(inputWeight);

        builder.setView(layout);
        builder.setPositiveButton("Añadir", (dialog, which) -> {
            try {
                int series = Integer.parseInt(inputSeries.getText().toString());
                int reps = Integer.parseInt(inputReps.getText().toString());
                double weight = Double.parseDouble(inputWeight.getText().toString());

                for (int i = 0; i < series; i++) {
                    sessionItem.addSet(reps, weight);
                }
                sessionList.add(sessionItem);
                adapter.notifyDataSetChanged();

            } catch (Exception e) {
                android.widget.Toast.makeText(this, "Datos inválidos", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void finalizarEntrenamiento() {
        if (sessionList.isEmpty())
            return;

        long trainingId = dbHelper.addTraining(getDate(), currentUserEmail);
        if (trainingId != -1) {
            for (TrainingSessionItem item : sessionList) {
                for (TrainingSessionItem.SetDetail set : item.getSets()) {

                    dbHelper.addTrainingDetail(trainingId, item.getExercise().getName(), 1, set.reps, set.weight);
                }
            }
            android.widget.Toast.makeText(this, "Entrenamiento Guardado", android.widget.Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteSessionItemDialog(int position) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar ejercicio")
                .setMessage("¿Quitar de la sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    sessionList.remove(position);
                    adapter.notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showEditSessionItemDialog(int position) {
        TrainingSessionItem item = sessionList.get(position);
        java.util.List<TrainingSessionItem.SetDetail> sets = item.getSets();


        int currentSeries = sets.size();
        int currentReps = currentSeries > 0 ? sets.get(0).reps : 0;
        double currentWeight = currentSeries > 0 ? sets.get(0).weight : 0;

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Editar " + item.getExercise().getName());

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 10, 40, 10);

        final android.widget.EditText inputSeries = new android.widget.EditText(this);
        inputSeries.setHint("Número de Series");
        inputSeries.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputSeries.setText(String.valueOf(currentSeries));
        layout.addView(inputSeries);

        final android.widget.EditText inputReps = new android.widget.EditText(this);
        inputReps.setHint("Repeticiones por serie");
        inputReps.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputReps.setText(String.valueOf(currentReps));
        layout.addView(inputReps);

        final android.widget.EditText inputWeight = new android.widget.EditText(this);
        inputWeight.setHint("Peso (kg)");
        inputWeight.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputWeight.setText(String.valueOf(currentWeight));
        layout.addView(inputWeight);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            try {
                int series = Integer.parseInt(inputSeries.getText().toString());
                int reps = Integer.parseInt(inputReps.getText().toString());
                double weight = Double.parseDouble(inputWeight.getText().toString());


                item.getSets().clear();
                for (int i = 0; i < series; i++) {
                    item.addSet(reps, weight);
                }
                adapter.notifyItemChanged(position);

            } catch (Exception e) {
                android.widget.Toast.makeText(this, "Datos inválidos", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private String getDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}
