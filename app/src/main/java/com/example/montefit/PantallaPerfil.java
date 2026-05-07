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
    private MaterialButton btnEditar, btnCambiarContrasena, btnBorrarCuenta;
    private int intentosFallidos = 0;

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
        btnBorrarCuenta = findViewById(R.id.btnBorrarCuenta);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);

        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

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
        btnBorrarCuenta.setOnClickListener(v -> mostrarDialogoBorrarCuenta());

        androidx.appcompat.widget.SwitchCompat switchLibras = findViewById(R.id.switchLibras);
        switchLibras.setChecked(PreferenciasApp.usaLibras(this));
        switchLibras.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PreferenciasApp.saveUnidadPeso(this, isChecked);
            cargarDatosUsuario();
        });

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
                    boolean esPrivado = perfil.optInt("es_privado", 0) == 1;

                    tvNombre.setText(nombre);
                    
                    String pesoStr = "--";
                    if (peso > 0) {
                        pesoStr = PreferenciasApp.formatPeso(peso, PantallaPerfil.this);
                    }
                    
                    tvDetails.setText(
                            "Edad: " + (edad > 0 ? edad : "--") +
                            " | Peso: " + pesoStr +
                            " | Sexo: " + (sexo.isEmpty() ? "--" : sexo)
                    );

                    androidx.appcompat.widget.SwitchCompat switchPrivado = findViewById(R.id.switchPrivado);
                    switchPrivado.setOnCheckedChangeListener(null);
                    switchPrivado.setChecked(esPrivado);
                    switchPrivado.setOnCheckedChangeListener((v, isChecked) -> {
                        new Thread(() -> {
                            ClienteApi.obtenerInstancia().actualizarPerfil(correo, nombre, edad, peso, sexo, isChecked);
                        }).start();
                    });
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
        inputPeso.setHint("Peso (" + (PreferenciasApp.usaLibras(this) ? "lbs" : "kg") + ")");
        inputPeso.setInputType(android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL | android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputPeso);

        android.widget.Spinner spinnerSexo = new android.widget.Spinner(this);
        String[] opcionesSexo = {"Hombre", "Mujer", "Otro", "Prefiero no decir"};
        android.widget.ArrayAdapter<String> adapterSexo = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesSexo);
        spinnerSexo.setAdapter(adapterSexo);
        layout.addView(spinnerSexo);

        builder.setView(layout);
        builder.setPositiveButton("Guardar", (d, w) -> {
            String correo = GestorUsuarios.getInstance().getCorreoActual();
            String nombre = inputNombre.getText().toString().trim();
            int edad = 0;
            double peso = 0;
            try { edad = Integer.parseInt(inputEdad.getText().toString().trim()); } catch (Exception ignored) {}
            try { peso = Double.parseDouble(inputPeso.getText().toString().trim()); } catch (Exception ignored) {}
            String sexo = spinnerSexo.getSelectedItem().toString();

            if (PreferenciasApp.usaLibras(PantallaPerfil.this) && peso > 0) {
                peso = PreferenciasApp.convertirAkgDesdeUnidadActual(peso, true);
            }

            int finalEdad = edad;
            double finalPeso = peso;
            androidx.appcompat.widget.SwitchCompat switchPrivado = findViewById(R.id.switchPrivado);
            boolean esPrivado = switchPrivado.isChecked();

            new Thread(() -> {
                boolean ok = ClienteApi.obtenerInstancia().actualizarPerfil(correo, nombre, finalEdad, finalPeso, sexo, esPrivado);
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

            String hashNueva = HashUtils.sha256(nueva);

            new Thread(() -> {
                boolean ok = ClienteApi.obtenerInstancia().cambiarContrasena(correo, hashNueva);
                runOnUiThread(() -> {
                    Toast.makeText(this, ok ? "Contraseña cambiada" : "Error", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoBorrarCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Borrar Cuenta");
        builder.setMessage("Para confirmar el borrado, introduce tu contraseña actual. Si fallas 2 veces, se cerrará la sesión.");

        EditText inputPass = new EditText(this);
        inputPass.setHint("Tu contraseña");
        inputPass.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        inputPass.setPadding(50, 30, 50, 10);
        builder.setView(inputPass);

        builder.setPositiveButton("Borrar Definitivamente", (d, w) -> {
            String correo = GestorUsuarios.getInstance().getCorreoActual();
            String pass = inputPass.getText().toString().trim();
            String passHash = HashUtils.sha256(pass);

            new Thread(() -> {
                boolean ok = ClienteApi.obtenerInstancia().eliminarCuenta(correo, passHash);
                runOnUiThread(() -> {
                    if (ok) {
                        Toast.makeText(this, "Cuenta eliminada correctamente", Toast.LENGTH_LONG).show();
                        cerrarSesionYSalir();
                    } else {
                        intentosFallidos++;
                        if (intentosFallidos >= 2) {
                            Toast.makeText(this, "Demasiados intentos. Cerrando sesión.", Toast.LENGTH_LONG).show();
                            cerrarSesionYSalir();
                        } else {
                            Toast.makeText(this, "Contraseña incorrecta. Intento 1/2", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }).start();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void cerrarSesionYSalir() {
        GestorUsuarios.getInstance().cerrarSesion();
        android.content.Intent intent = new android.content.Intent(this, PantallaPrincipal.class);
        intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void updateThemeIcon() {
        int mode = PreferenciasApp.getThemeMode(this);
        if (btnThemeToggle != null) {
            btnThemeToggle.setImageResource(mode == 1 ? R.drawable.ic_moon : R.drawable.ic_sun);
        }
    }
}
