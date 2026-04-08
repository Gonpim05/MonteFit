package com.example.montefit;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PantallaComida extends AppCompatActivity implements InterfazListaAlimentos.OnFoodClickListener {

    private RecyclerView recyclerView;
    private InterfazListaAlimentos miAdaptador;
    private List<Alimento> listaAlimentos;
    private GestorBaseDatos gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_comida);

        gestorBD = GestorUsuarios.getInstance().getDbHelper();
        if (gestorBD == null) {
            GestorUsuarios.getInstance().init(this);
            gestorBD = GestorUsuarios.getInstance().getDbHelper();
        }

        recyclerView = findViewById(R.id.rvComida);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaAlimentos = new ArrayList<>();
        miAdaptador = new InterfazListaAlimentos(listaAlimentos, this);
        recyclerView.setAdapter(miAdaptador);

        findViewById(R.id.fabAddComida).setOnClickListener(v -> showAddFoodDialog(null));

        loadFoods();
    }

    private void loadFoods() {
        listaAlimentos.clear();
        Cursor datosBD = gestorBD.getAllFoods();
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
        miAdaptador.notifyDataSetChanged();
    }

    @Override
    public void onDeleteClick(Alimento Alimento) {
        showFoodOptionsDialog(Alimento);
    }

    private void showFoodOptionsDialog(Alimento Alimento) {
        String[] opciones = { "Editar", "Eliminar" };
        new android.app.AlertDialog.Builder(this)
                .setTitle(Alimento.getName())
                .setItems(opciones, (miDialogo, which) -> {
                    if (which == 0) {
                        showAddFoodDialog(Alimento);
                    } else {
                        new android.app.AlertDialog.Builder(this)
                                .setTitle("Eliminar " + Alimento.getName() + "?")
                                .setPositiveButton("Sí", (d, w) -> {
                                    if (gestorBD.deleteFood(Alimento.getId())) {
                                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show();
                                        loadFoods();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                    }
                })
                .show();
    }

    private void showAddFoodDialog(Alimento existente) {
        boolean editing = existente != null;
        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle(editing ? "Editar Comida" : "Añadir Comida");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(50, 30, 50, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre");
        inputName.setTextColor(android.graphics.Color.WHITE);
        inputName.setHintTextColor(android.graphics.Color.LTGRAY);
        if (editing)
            inputName.setText(existente.getName());
        contenedor.addView(inputName);

        final android.widget.EditText inputKcal = new android.widget.EditText(this);
        inputKcal.setHint("Calorías");
        inputKcal.setTextColor(android.graphics.Color.WHITE);
        inputKcal.setHintTextColor(android.graphics.Color.LTGRAY);
        inputKcal.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (editing)
            inputKcal.setText(String.valueOf(existente.getKcal()));
        contenedor.addView(inputKcal);

        final android.widget.EditText inputProt = new android.widget.EditText(this);
        inputProt.setHint("Proteínas (g)");
        inputProt.setTextColor(android.graphics.Color.WHITE);
        inputProt.setHintTextColor(android.graphics.Color.LTGRAY);
        inputProt.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (editing)
            inputProt.setText(String.valueOf(existente.getProtein()));
        contenedor.addView(inputProt);

        final android.widget.EditText inputCarb = new android.widget.EditText(this);
        inputCarb.setHint("Carbohidratos (g)");
        inputCarb.setTextColor(android.graphics.Color.WHITE);
        inputCarb.setHintTextColor(android.graphics.Color.LTGRAY);
        inputCarb.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (editing)
            inputCarb.setText(String.valueOf(existente.getCarbs()));
        contenedor.addView(inputCarb);

        final android.widget.EditText inputFat = new android.widget.EditText(this);
        inputFat.setHint("Grasas (g)");
        inputFat.setTextColor(android.graphics.Color.WHITE);
        inputFat.setHintTextColor(android.graphics.Color.LTGRAY);
        inputFat.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (editing)
            inputFat.setText(String.valueOf(existente.getFats()));
        contenedor.addView(inputFat);

        constructorDialogo.setView(contenedor);
        constructorDialogo.setPositiveButton("Guardar", (miDialogo, which) -> {
            try {
                String nombre = inputName.getText().toString();
                int kcal = Integer.parseInt(inputKcal.getText().toString());
                double prot = Double.parseDouble(inputProt.getText().toString());
                double carb = Double.parseDouble(inputCarb.getText().toString());
                double fat = Double.parseDouble(inputFat.getText().toString());

                boolean ok;
                if (editing) {
                    ok = gestorBD.updateFood(existente.getId(), nombre, kcal, prot, carb, fat);
                } else {
                    ok = gestorBD.addFood(nombre, kcal, prot, carb, fat);
                }

                if (ok) {
                    Toast.makeText(this, editing ? "Actualizado" : "Comida añadida", Toast.LENGTH_SHORT).show();
                    loadFoods();
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Datos inválidos", Toast.LENGTH_SHORT).show();
            }
        });
        constructorDialogo.setNegativeButton("Cancelar", null);
        constructorDialogo.show();
    }
}
