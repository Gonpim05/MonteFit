package com.example.montefit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import com.google.android.material.textfield.TextInputEditText;

public class PantallaRegistro extends AppCompatActivity {

    private TextInputEditText campoNombre, campoCorreo, campoContrasena;
    private Button botonRegistro, botonYaTengoCuenta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_registro);

        campoNombre = findViewById(R.id.etNombre);
        campoCorreo = findViewById(R.id.correo);
        campoContrasena = findViewById(R.id.contrasena);
        botonRegistro = findViewById(R.id.botonRegistrate);
        botonYaTengoCuenta = findViewById(R.id.btnYaTengoCuenta);

        botonRegistro.setOnClickListener(v -> intentarRegistro());

        botonYaTengoCuenta.setOnClickListener(v -> {
            startActivity(new Intent(this, PantallaPrincipal.class));
            finish();
        });
    }

    private void intentarRegistro() {
        String nombre = campoNombre.getText().toString().trim();
        String correo = campoCorreo.getText().toString().trim();
        String contrasena = campoContrasena.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        botonRegistro.setEnabled(false);
        botonRegistro.setText("Registrando...");

        new Thread(() -> {
            JSONObject resp = ClienteApi.obtenerInstancia().registrarUsuario(nombre, correo, contrasena);
            boolean registroOk = resp.optBoolean("ok", false);
            String error = resp.optString("error", "Error al registrar usuario");

            runOnUiThread(() -> {
                botonRegistro.setEnabled(true);
                botonRegistro.setText("Registrarse");

                if (registroOk) {
                    Toast.makeText(this, "¡Cuenta creada! Inicia sesión", Toast.LENGTH_SHORT).show();
                    finish(); // Volver al login
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}
