package com.example.montefit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PantallaPrincipal extends AppCompatActivity {
    private EditText etCorreo, etContrasena;
    private Button btnLoguear, btnIrARegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Aplicar tema antes de onCreate
        PreferenciasApp.applyTheme(PreferenciasApp.getThemeMode(this));
        super.onCreate(savedInstanceState);
        GestorUsuarios.getInstance().init(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_login);
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
        etCorreo = findViewById(R.id.correo);
        etContrasena = findViewById(R.id.contrasena);
        btnLoguear = findViewById(R.id.botonLoguear);
        btnIrARegistro = findViewById(R.id.botonRegistro);
        btnLoguear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = etCorreo.getText().toString().trim();
                String contrasena = etContrasena.getText().toString().trim();

                if (correo.isEmpty() || contrasena.isEmpty()) {
                    Toast.makeText(PantallaPrincipal.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (GestorUsuarios.getInstance().login(correo, contrasena)) {
                        Toast.makeText(PantallaPrincipal.this, "Bienvenido " + correo, Toast.LENGTH_SHORT).show();
                        Intent cambioPantalla = new Intent(PantallaPrincipal.this, PantallaInicial.class);
                        startActivity(cambioPantalla);
                        finish();
                    } else {
                        Toast.makeText(PantallaPrincipal.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        btnIrARegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cambioPantalla = new Intent(PantallaPrincipal.this, PantallaRegistro.class);
                startActivity(cambioPantalla);
            }
        });
    }
}


















