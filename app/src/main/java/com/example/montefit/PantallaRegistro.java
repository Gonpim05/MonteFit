package com.example.montefit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class PantallaRegistro extends AppCompatActivity {

    // 1. Declaramos las variables (usando TextInputEditText para tus campos con
    // Material Design)
    private TextInputEditText etCorreo, etContrasena, etNombre;
    private MaterialButton btnRegistrar;
    private Button btnVolverLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Habilitar diseño Edge-to-Edge
        EdgeToEdge.enable(this);

        // 2. Cargamos el contenedor de registro (ASEGÚRATE de que el nombre coincida con tu
        // archivo .xml)
        // Si tu archivo se llama layout_registro.xml, cámbialo aquí:
        setContentView(R.layout.layout_registro);

        // 3. Ajuste de márgenes del sistema
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
        etNombre = findViewById(R.id.etNombre);
        btnRegistrar = findViewById(R.id.botonRegistrate);
        btnVolverLogin = findViewById(R.id.btnYaTengoCuenta);


        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String correo = etCorreo.getText().toString().trim();
                String pass = etContrasena.getText().toString().trim();
                String nombre = etNombre.getText().toString().trim();

                if (correo.isEmpty() || pass.isEmpty() || nombre.isEmpty()) {
                    Toast.makeText(PantallaRegistro.this, "Por favor, rellena todos los datos", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    if (GestorUsuarios.getInstance().register(correo, pass, nombre)) {
                        Toast.makeText(PantallaRegistro.this, "Registro exitoso. Por favor, inicia sesión.",
                                Toast.LENGTH_SHORT).show();

                        finish();
                    } else {
                        Toast.makeText(PantallaRegistro.this, "El usuario ya existe.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        btnVolverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}











