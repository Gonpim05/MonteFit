package com.example.montefit;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PantallaComida extends AppCompatActivity {

    private RecyclerView rvComida;
    private InterfazListaAlimentos adaptador;
    private List<Alimento> listaAlimentos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_comida);

        rvComida = findViewById(R.id.rvComida);
        rvComida.setLayoutManager(new LinearLayoutManager(this));

        adaptador = new InterfazListaAlimentos(listaAlimentos, alimento -> mostrarOpcionesAlimento(alimento));
        rvComida.setAdapter(adaptador);

        FloatingActionButton fabAdd = findViewById(R.id.fabAddComida);
        fabAdd.setOnClickListener(v -> mostrarDialogoAnadir());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarComidas();
    }

    private void cargarComidas() {
        new Thread(() -> {
            JSONArray datos = ClienteApi.obtenerInstancia().obtenerComidas();
            List<Alimento> lista = new ArrayList<>();

            for (int i = 0; i < datos.length(); i++) {
                try {
                    JSONObject obj = datos.getJSONObject(i);
                    lista.add(new Alimento(
                            obj.optInt("id", 0),
                            obj.optString("nombre", ""),
                            obj.optInt("calorias", 0),
                            obj.optDouble("proteinas", 0),
                            obj.optDouble("carbohidratos", 0),
                            obj.optDouble("grasas", 0)
                    ));
                } catch (Exception e) { e.printStackTrace(); }
            }

            runOnUiThread(() -> {
                listaAlimentos.clear();
                listaAlimentos.addAll(lista);
                adaptador.notifyDataSetChanged();
            });
        }).start();
    }

    private void mostrarDialogoAnadir() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir Comida");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del alimento");
        layout.addView(inputNombre);

        EditText inputCal = new EditText(this);
        inputCal.setHint("Calorías");
        inputCal.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCal);

        EditText inputProt = new EditText(this);
        inputProt.setHint("Proteínas (g)");
        inputProt.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputProt);

        EditText inputCarb = new EditText(this);
        inputCarb.setHint("Carbohidratos (g)");
        inputCarb.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCarb);

        EditText inputGras = new EditText(this);
        inputGras.setHint("Grasas (g)");
        inputGras.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputGras);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (d, w) -> {
            String correo = GestorUsuarios.getInstance().getCorreoActual();
            String nombre = inputNombre.getText().toString().trim();
            int cal = 0;
            double prot = 0, carb = 0, gras = 0;
            try { cal = Integer.parseInt(inputCal.getText().toString().trim()); } catch (Exception ignored) {}
            try { prot = Double.parseDouble(inputProt.getText().toString().trim()); } catch (Exception ignored) {}
            try { carb = Double.parseDouble(inputCarb.getText().toString().trim()); } catch (Exception ignored) {}
            try { gras = Double.parseDouble(inputGras.getText().toString().trim()); } catch (Exception ignored) {}

            int finalCal = cal;
            double finalProt = prot, finalCarb = carb, finalGras = gras;
            new Thread(() -> {
                ClienteApi.obtenerInstancia().guardarComida(nombre, correo, finalCal, finalProt, finalCarb, finalGras);
                runOnUiThread(this::cargarComidas);
            }).start();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarOpcionesAlimento(Alimento alimento) {
        new AlertDialog.Builder(this)
                .setTitle(alimento.getName())
                .setItems(new String[]{"Editar", "Eliminar"}, (d, w) -> {
                    if (w == 0) mostrarDialogoEditar(alimento);
                    else eliminarAlimento(alimento);
                })
                .show();
    }

    private void mostrarDialogoEditar(Alimento alimento) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Comida");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        EditText inputNombre = new EditText(this);
        inputNombre.setText(alimento.getName());
        layout.addView(inputNombre);

        EditText inputCal = new EditText(this);
        inputCal.setText(String.valueOf(alimento.getKcal()));
        inputCal.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCal);

        EditText inputProt = new EditText(this);
        inputProt.setText(String.valueOf(alimento.getProtein()));
        inputProt.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputProt);

        EditText inputCarb = new EditText(this);
        inputCarb.setText(String.valueOf(alimento.getCarbs()));
        inputCarb.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCarb);

        EditText inputGras = new EditText(this);
        inputGras.setText(String.valueOf(alimento.getFats()));
        inputGras.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputGras);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (d, w) -> {
            String nombre = inputNombre.getText().toString().trim();
            int cal = 0;
            double prot = 0, carb = 0, gras = 0;
            try { cal = Integer.parseInt(inputCal.getText().toString().trim()); } catch (Exception ignored) {}
            try { prot = Double.parseDouble(inputProt.getText().toString().trim()); } catch (Exception ignored) {}
            try { carb = Double.parseDouble(inputCarb.getText().toString().trim()); } catch (Exception ignored) {}
            try { gras = Double.parseDouble(inputGras.getText().toString().trim()); } catch (Exception ignored) {}

            int fCal = cal; double fProt = prot, fCarb = carb, fGras = gras;
            new Thread(() -> {
                ClienteApi.obtenerInstancia().editarComida(alimento.getId(), nombre, fCal, fProt, fCarb, fGras);
                runOnUiThread(this::cargarComidas);
            }).start();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void eliminarAlimento(Alimento alimento) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Eliminar " + alimento.getName() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    new Thread(() -> {
                        ClienteApi.obtenerInstancia().eliminarComida(alimento.getId());
                        runOnUiThread(this::cargarComidas);
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
