package com.example.montefit;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

public class PantallaPerfil extends AppCompatActivity {

    private ImageButton btnThemeToggle;
    private TextView tvNombre, tvEmail, tvDetails;
    private MaterialButton btnEditar, btnCambiarContrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenciasApp.applyTheme(PreferenciasApp.getThemeMode(this));
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_perfil);

        tvNombre = findViewById(R.id.tvNombrePerfil);
        tvEmail = findViewById(R.id.tvEmailPerfil);
        tvDetails = findViewById(R.id.tvDetails);
        btnEditar = findViewById(R.id.btnEditarPerfil);
        btnCambiarContrasena = findViewById(R.id.btnCambiarContrasena);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);

        // Toggle tema claro/oscuro
        updateThemeIcon();
        btnThemeToggle.setOnClickListener(v -> {
            int modoActual = PreferenciasApp.getThemeMode(this);
            int nuevoModo = (modoActual == 1) ? 2 : 1;
            PreferenciasApp.saveThemeMode(this, nuevoModo);
            PreferenciasApp.applyTheme(nuevoModo);
            recreate();
        });

        btnEditar.setOnClickListener(v -> mostrarDialogoEditar());
        btnCambiarContrasena.setOnClickListener(v -> mostrarDialogoCambiarContrasena());

        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        String correo = GestorUsuarios.getInstance().getCorreoActual();
        if (correo == null) {
            tvNombre.setText("Sin sesión");
            tvEmail.setText("");
            tvDetails.setText("No hay datos");
            return;
        }

        tvEmail.setText(correo);

        new Thread(() -> {
            JSONObject perfil = ClienteApi.obtenerInstancia().obtenerPerfil(correo);

            runOnUiThread(() -> {
                if (perfil != null && perfil.length() > 0) {
                    String nombre = perfil.optString("nombre", "Usuario");
                    int edad = perfil.optInt("age", 0);
                    double peso = perfil.optDouble("peso", 0);
                    String sexo = perfil.optString("sex", "");

                    tvNombre.setText(nombre);
                    tvDetails.setText(
                            "Edad: " + (edad > 0 ? edad : "--") +
                            " | Peso: " + (peso > 0 ? peso + " kg" : "--") +
                            " | Sexo: " + (sexo.isEmpty() ? "--" : sexo)
                    );
                } else {
                    tvNombre.setText("Usuario");
                    tvDetails.setText("No se pudo cargar el perfil");
                }
            });
        }).start();
    }

    private void mostrarDialogoEditar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Perfil");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre");
        layout.addView(inputNombre);

        EditText inputEdad = new EditText(this);
        inputEdad.setHint("Edad");
        inputEdad.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputEdad);

        EditText inputPeso = new EditText(this);
        inputPeso.setHint("Peso (kg)");
        inputPeso.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPeso);

        EditText inputSexo = new EditText(this);
        inputSexo.setHint("Sexo (M/F)");
        layout.addView(inputSexo);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (d, w) -> {
            String correo = GestorUsuarios.getInstance().getCorreoActual();
            String nombre = inputNombre.getText().toString().trim();
            int edad = 0;
            double peso = 0;
            try { edad = Integer.parseInt(inputEdad.getText().toString().trim()); } catch (Exception ignored) {}
            try { peso = Double.parseDouble(inputPeso.getText().toString().trim()); } catch (Exception ignored) {}
            String sexo = inputSexo.getText().toString().trim();

            int finalEdad = edad;
            double finalPeso = peso;
            new Thread(() -> {
                boolean ok = ClienteApi.obtenerInstancia().actualizarPerfil(correo, nombre, finalEdad, finalPeso, sexo);
                runOnUiThread(() -> {
                    if (ok) {
                        Toast.makeText(this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        cargarDatosUsuario();
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoCambiarContrasena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        EditText inputNueva = new EditText(this);
        inputNueva.setHint("Nueva contraseña");
        inputNueva.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputNueva.setPadding(50, 30, 50, 10);
        builder.setView(inputNueva);

        builder.setPositiveButton("Cambiar", (d, w) -> {
            String correo = GestorUsuarios.getInstance().getCorreoActual();
            String nueva = inputNueva.getText().toString().trim();
            if (nueva.isEmpty()) {
                Toast.makeText(this, "Escribe una contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                boolean ok = ClienteApi.obtenerInstancia().cambiarContrasena(correo, nueva);
                runOnUiThread(() -> {
                    Toast.makeText(this, ok ? "Contraseña cambiada" : "Error", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void updateThemeIcon() {
        int mode = PreferenciasApp.getThemeMode(this);
        if (btnThemeToggle != null) {
            btnThemeToggle.setImageResource(mode == 1 ? R.drawable.ic_moon : R.drawable.ic_sun);
        }
    }
}
