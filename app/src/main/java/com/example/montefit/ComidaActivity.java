package com.example.montefit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ComidaActivity extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView rvComida;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd;
    private FoodAdapter adapter;
    private java.util.List<Food> foodList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida);

        rvComida = findViewById(R.id.rvComida);
        fabAdd = findViewById(R.id.fabAddComida);
        rvComida.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        dbHelper = UserManager.getInstance().getDbHelper();
        if (dbHelper == null) {
            UserManager.getInstance().init(this);
            dbHelper = UserManager.getInstance().getDbHelper();
        }

        loadFoods();

        fabAdd.setOnClickListener(v -> showAddFoodDialog());
    }

    private void loadFoods() {
        foodList = new java.util.ArrayList<>();
        android.database.Cursor cursor = dbHelper.getAllFoods();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int kcal = cursor.getInt(cursor.getColumnIndexOrThrow("kcal"));
                double protein = cursor.getDouble(cursor.getColumnIndexOrThrow("protein"));
                double carbs = cursor.getDouble(cursor.getColumnIndexOrThrow("carbs"));
                double fats = cursor.getDouble(cursor.getColumnIndexOrThrow("fats"));
                foodList.add(new Food(id, name, kcal, protein, carbs, fats));
            } while (cursor.moveToNext());
            cursor.close();
        }

        if (adapter == null) {
            adapter = new FoodAdapter(foodList, this::showOptionsDialog);
            rvComida.setAdapter(adapter);
        } else {
            adapter.updateList(foodList);
        }
    }

    private void showOptionsDialog(Food food) {
        String[] options = { "Editar", "Eliminar" };
        new android.app.AlertDialog.Builder(this)
                .setTitle(food.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showAddFoodDialog(food); // Reuse dialog for editing
                    } else if (which == 1) {
                        confirmDelete(food);
                    }
                })
                .show();
    }

    private void confirmDelete(Food food) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar " + food.getName())
                .setMessage("¿Estás seguro?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    if (dbHelper.deleteFood(food.getId())) {
                        android.widget.Toast.makeText(this, "Eliminado", android.widget.Toast.LENGTH_SHORT).show();
                        loadFoods();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showAddFoodDialog() {
        showAddFoodDialog(null);
    }

    private void showAddFoodDialog(Food foodToEdit) {
        boolean isEditing = (foodToEdit != null);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(isEditing ? "Editar Comida" : "Añadir Comida");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre");
        if (isEditing)
            inputName.setText(foodToEdit.getName());
        layout.addView(inputName);

        final android.widget.EditText inputKcal = new android.widget.EditText(this);
        inputKcal.setHint("Kcal");
        inputKcal.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (isEditing)
            inputKcal.setText(String.valueOf(foodToEdit.getKcal()));
        layout.addView(inputKcal);

        final android.widget.EditText inputProtein = new android.widget.EditText(this);
        inputProtein.setHint("Proteínas (g)");
        inputProtein.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (isEditing)
            inputProtein.setText(String.valueOf(foodToEdit.getProtein()));
        layout.addView(inputProtein);

        final android.widget.EditText inputCarbs = new android.widget.EditText(this);
        inputCarbs.setHint("Carbohidratos (g)");
        inputCarbs.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (isEditing)
            inputCarbs.setText(String.valueOf(foodToEdit.getCarbs()));
        layout.addView(inputCarbs);

        final android.widget.EditText inputFats = new android.widget.EditText(this);
        inputFats.setHint("Grasas (g)");
        inputFats.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (isEditing)
            inputFats.setText(String.valueOf(foodToEdit.getFats()));
        layout.addView(inputFats);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            try {
                String name = inputName.getText().toString();
                if (name.isEmpty()) {
                    android.widget.Toast.makeText(this, "El nombre es obligatorio", android.widget.Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                int kcal = Integer.parseInt(inputKcal.getText().toString());
                double protein = Double.parseDouble(inputProtein.getText().toString());
                double carbs = Double.parseDouble(inputCarbs.getText().toString());
                double fats = Double.parseDouble(inputFats.getText().toString());

                if (isEditing) {
                    if (dbHelper.updateFood(foodToEdit.getId(), name, kcal, protein, carbs, fats)) {
                        android.widget.Toast.makeText(this, "Actualizado", android.widget.Toast.LENGTH_SHORT).show();
                        loadFoods();
                    } else {
                        android.widget.Toast.makeText(this, "Error al actualizar", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (dbHelper.addFood(name, kcal, protein, carbs, fats)) {
                        android.widget.Toast.makeText(this, "Guardado", android.widget.Toast.LENGTH_SHORT).show();
                        loadFoods();
                    } else {
                        android.widget.Toast.makeText(this, "Error al guardar", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    }
                }
            } catch (NumberFormatException e) {
                android.widget.Toast.makeText(this, "Datos numéricos inválidos", android.widget.Toast.LENGTH_SHORT)
                        .show();
            }
        });
        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }
}
