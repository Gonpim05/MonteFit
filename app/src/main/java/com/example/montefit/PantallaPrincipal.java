package com.example.montefit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class PantallaPrincipal extends AppCompatActivity {

    private EditText campoCorreo, campoContrasena;
    private Button botonLogin, botonRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenciasApp.applyTheme(PreferenciasApp.getThemeMode(this));
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_login);

        campoCorreo = findViewById(R.id.correo);
        campoContrasena = findViewById(R.id.contrasena);
        botonLogin = findViewById(R.id.botonLoguear);
        botonRegistro = findViewById(R.id.botonRegistro);

        botonLogin.setOnClickListener(v -> intentarLogin());

        botonRegistro.setOnClickListener(v -> {
            startActivity(new Intent(this, PantallaRegistro.class));
        });
    }

    private void intentarLogin() {
        String correo = campoCorreo.getText().toString().trim();
        String contrasena = campoContrasena.getText().toString().trim();

        if (correo.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        botonLogin.setEnabled(false);
        botonLogin.setText("Conectando...");

        new Thread(() -> {
            JSONObject resp = ClienteApi.obtenerInstancia().iniciarSesion(correo, contrasena);
            boolean loginOk = resp.optBoolean("ok", false);
            String error = resp.optString("error", "Correo o contraseña incorrectos");

            runOnUiThread(() -> {
                botonLogin.setEnabled(true);
                botonLogin.setText("Iniciar sesión");

                if (loginOk) {
                    GestorUsuarios.getInstance().setCorreoActual(correo);
                    // Obtener el usuario_id en segundo plano
                    new Thread(() -> {
                        int userId = ClienteApi.obtenerInstancia().obtenerUsuarioId(correo);
                        GestorUsuarios.getInstance().setUsuarioId(userId);
                    }).start();

                    startActivity(new Intent(PantallaPrincipal.this, PantallaInicial.class));
                    finish();
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}
