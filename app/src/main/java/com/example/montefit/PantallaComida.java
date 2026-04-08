package com.example.montefit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaComida extends AppCompatActivity {
    private androidx.recyclerview.widget.RecyclerView rvComida;
    private com.google.android.material.floatingactionbutton.FloatingActionButton botonAnadir;
    private InterfazListaAlimentos miAdaptador;
    private java.util.List<Alimento> listaAlimentos;
    private GestorBaseDatos gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_comida);

        rvComida = findViewById(R.id.rvComida);
        botonAnadir = findViewById(R.id.fabAddComida);
        rvComida.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        gestorBD = GestorUsuarios.getInstance().getDbHelper();
        if (gestorBD == null) {
            GestorUsuarios.getInstance().init(this);
            gestorBD = GestorUsuarios.getInstance().getDbHelper();
        }

        loadFoods();

        botonAnadir.setOnClickListener(v -> showAddFoodDialog());
    }

    private void loadFoods() {
        listaAlimentos = new java.util.ArrayList<>();
        android.database.Cursor datosBD = gestorBD.getAllFoods();
        if (datosBD != null && datosBD.moveToFirst()) {
            do {
                int id = datosBD.getInt(datosBD.getColumnIndexOrThrow("id"));
                String nombre = datosBD.getString(datosBD.getColumnIndexOrThrow("nombre"));
                int calorias = datosBD.getInt(datosBD.getColumnIndexOrThrow("calorias"));
                double proteinas = datosBD.getDouble(datosBD.getColumnIndexOrThrow("proteinas"));
                double carbohidratos = datosBD.getDouble(datosBD.getColumnIndexOrThrow("carbohidratos"));
                double grasas = datosBD.getDouble(datosBD.getColumnIndexOrThrow("grasas"));
                listaAlimentos.add(new Alimento(id, nombre, calorias, proteinas, carbohidratos, grasas));
            } while (datosBD.moveToNext());
            datosBD.close();
        }

        if (miAdaptador == null) {
            miAdaptador = new InterfazListaAlimentos(listaAlimentos, this::showOptionsDialog);
            rvComida.setAdapter(miAdaptador);
        } else {
            miAdaptador.updateList(listaAlimentos);
        }
    }

    private void showOptionsDialog(Alimento Alimento) {
        String[] options = { "Editar", "Eliminar" };
        new android.app.AlertDialog.Builder(this)
                .setTitle(Alimento.getName())
                .setItems(options, (miDialogo, which) -> {
                    if (which == 0) {
                        showAddFoodDialog(Alimento); // Reuse miDialogo for editing
                    } else if (which == 1) {
                        confirmDelete(Alimento);
                    }
                })
                .show();
    }

    private void confirmDelete(Alimento Alimento) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar " + Alimento.getName())
                .setMessage("¿Estás seguro?")
                .setPositiveButton("Sí", (miDialogo, which) -> {
                    if (gestorBD.deleteFood(Alimento.getId())) {
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

    private void showAddFoodDialog(Alimento foodToEdit) {
        boolean isEditing = (foodToEdit != null);
        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle(isEditing ? "Editar Comida" : "Añadir Comida");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(50, 20, 50, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre");
        if (isEditing)
            inputName.setText(foodToEdit.getName());
        contenedor.addView(inputName);

        final android.widget.EditText inputKcal = new android.widget.EditText(this);
        inputKcal.setHint("calorias");
        inputKcal.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (isEditing)
            inputKcal.setText(String.valueOf(foodToEdit.getKcal()));
        contenedor.addView(inputKcal);

        final android.widget.EditText inputProtein = new android.widget.EditText(this);
        inputProtein.setHint("Proteínas (g)");
        inputProtein.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (isEditing)
            inputProtein.setText(String.valueOf(foodToEdit.getProtein()));
        contenedor.addView(inputProtein);

        final android.widget.EditText inputCarbs = new android.widget.EditText(this);
        inputCarbs.setHint("Carbohidratos (g)");
        inputCarbs.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (isEditing)
            inputCarbs.setText(String.valueOf(foodToEdit.getCarbs()));
        contenedor.addView(inputCarbs);

        final android.widget.EditText inputFats = new android.widget.EditText(this);
        inputFats.setHint("Grasas (g)");
        inputFats.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (isEditing)
            inputFats.setText(String.valueOf(foodToEdit.getFats()));
        contenedor.addView(inputFats);

        constructorDialogo.setView(contenedor);

        constructorDialogo.setPositiveButton("Guardar", (miDialogo, which) -> {
            try {
                String nombre = inputName.getText().toString();
                if (nombre.isEmpty()) {
                    android.widget.Toast.makeText(this, "El nombre es obligatorio", android.widget.Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                int calorias = Integer.parseInt(inputKcal.getText().toString());
                double proteinas = Double.parseDouble(inputProtein.getText().toString());
                double carbohidratos = Double.parseDouble(inputCarbs.getText().toString());
                double grasas = Double.parseDouble(inputFats.getText().toString());

                if (isEditing) {
                    if (gestorBD.updateFood(foodToEdit.getId(), nombre, calorias, proteinas, carbohidratos, grasas)) {
                        android.widget.Toast.makeText(this, "Actualizado", android.widget.Toast.LENGTH_SHORT).show();
                        loadFoods();
                    } else {
                        android.widget.Toast.makeText(this, "Error al actualizar", android.widget.Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    if (gestorBD.addFood(nombre, calorias, proteinas, carbohidratos, grasas)) {
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
        constructorDialogo.setNegativeButton("Cancelar", null);

        constructorDialogo.show();
    }
}

























