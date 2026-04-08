package com.example.montefit;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaPerfil extends AppCompatActivity {
    private android.widget.TextView tvNombre, tvEmail, tvDetails;
    private com.google.android.material.button.MaterialButton btnCambiarPass, btnEditarPerfil;
    private android.widget.ImageButton btnThemeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_perfil);

        tvNombre = findViewById(R.id.tvNombrePerfil);
        tvEmail = findViewById(R.id.tvEmailPerfil);
        tvDetails = findViewById(R.id.tvDetails);
        btnCambiarPass = findViewById(R.id.btnCambiarContrasena);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnThemeToggle = findViewById(R.id.btnThemeToggle);

        updateThemeIcon();
        loadUserData();

        btnThemeToggle.setOnClickListener(v -> {
            int currentMode = PreferenciasApp.getThemeMode(this);
            int newMode = (currentMode == 0) ? 1 : 0; // Toggle 0 (Dark) <-> 1 (Light)
            PreferenciasApp.saveThemeMode(this, newMode);
            recreate(); // Recargar actividad para aplicar tema
        });

        btnCambiarPass.setOnClickListener(v -> mostrarDialogoCambioPass());
        btnEditarPerfil.setOnClickListener(v -> mostrarDialogoEditarPerfil());
    }

    private void loadUserData() {
        String correo = GestorUsuarios.getInstance().getCurrentUserEmail();
        if (correo != null) {
            tvEmail.setText(correo);
            GestorBaseDatos db = GestorUsuarios.getInstance().getDbHelper();
            if (db != null) {
                android.database.Cursor datosBD = db.getUserProfile(correo);
                if (datosBD != null && datosBD.moveToFirst()) {
                    String nombre = datosBD.getString(datosBD.getColumnIndexOrThrow("nombre"));
                    int age = datosBD.isNull(datosBD.getColumnIndexOrThrow("age")) ? 0
                            : datosBD.getInt(datosBD.getColumnIndexOrThrow("age"));
                    double peso = datosBD.isNull(datosBD.getColumnIndexOrThrow("peso")) ? 0.0
                            : datosBD.getDouble(datosBD.getColumnIndexOrThrow("peso"));
                    String sex = datosBD.getString(datosBD.getColumnIndexOrThrow("sex"));

                    tvNombre.setText(nombre != null ? nombre : "Sin Nombre");
                    String ageStr = age > 0 ? String.valueOf(age) : "--";
                    String pesoStr = peso > 0 ? String.format("%.1fkg", peso) : "--";
                    String sexStr = (sex != null && !sex.isEmpty()) ? sex : "--";
                    tvDetails.setText(String.format("Edad: %s | Peso: %s | Sexo: %s", ageStr, pesoStr, sexStr));
                    datosBD.close();
                }
            }
        }
    }

    private void mostrarDialogoEditarPerfil() {
        String correo = GestorUsuarios.getInstance().getCurrentUserEmail();
        if (correo == null)
            return;
        GestorBaseDatos db = GestorUsuarios.getInstance().getDbHelper();
        String currentName = "";
        int currentAge = 0;
        double currentWeight = 0;
        String currentSex = "";

        android.database.Cursor datosBD = db.getUserProfile(correo);
        if (datosBD != null && datosBD.moveToFirst()) {
            currentName = datosBD.getString(datosBD.getColumnIndexOrThrow("nombre"));
            if (!datosBD.isNull(datosBD.getColumnIndexOrThrow("age")))
                currentAge = datosBD.getInt(datosBD.getColumnIndexOrThrow("age"));
            if (!datosBD.isNull(datosBD.getColumnIndexOrThrow("peso")))
                currentWeight = datosBD.getDouble(datosBD.getColumnIndexOrThrow("peso"));
            currentSex = datosBD.getString(datosBD.getColumnIndexOrThrow("sex"));
            datosBD.close();
        }

        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle("Editar Perfil");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(50, 40, 50, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre");
        inputName.setText(currentName);
        inputName.setTextColor(android.graphics.Color.WHITE);
        inputName.setHintTextColor(android.graphics.Color.LTGRAY);
        contenedor.addView(inputName);

        final android.widget.EditText inputAge = new android.widget.EditText(this);
        inputAge.setHint("Edad");
        inputAge.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        inputAge.setTextColor(android.graphics.Color.WHITE);
        inputAge.setHintTextColor(android.graphics.Color.LTGRAY);
        if (currentAge > 0)
            inputAge.setText(String.valueOf(currentAge));
        contenedor.addView(inputAge);

        final android.widget.EditText inputWeight = new android.widget.EditText(this);
        inputWeight.setHint("Peso (kg)");
        inputWeight.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        inputWeight.setTextColor(android.graphics.Color.WHITE);
        inputWeight.setHintTextColor(android.graphics.Color.LTGRAY);
        if (currentWeight > 0)
            inputWeight.setText(String.valueOf(currentWeight));
        contenedor.addView(inputWeight);

        final android.widget.Spinner spinnerSex = new android.widget.Spinner(this);
        String[] sexOptions = { "Hombre", "Mujer", "Otro" };
        android.widget.ArrayAdapter<String> adapterSex = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, sexOptions);
        spinnerSex.setAdapter(adapterSex);
        if (currentSex != null) {
            for (int i = 0; i < sexOptions.length; i++) {
                if (sexOptions[i].equals(currentSex)) {
                    spinnerSex.setSelection(i);
                    break;
                }
            }
        }
        contenedor.addView(spinnerSex);

        constructorDialogo.setView(contenedor);

        constructorDialogo.setPositiveButton("Guardar", (miDialogo, which) -> {
            String newName = inputName.getText().toString();
            String ageStr = inputAge.getText().toString();
            String weightStr = inputWeight.getText().toString();
            String newSex = spinnerSex.getSelectedItem().toString();

            int newAge = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
            double newWeight = weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr);

            if (db.updateUserProfile(correo, newName, newAge, newWeight, newSex)) {
                android.widget.Toast.makeText(this, "Perfil actualizado", android.widget.Toast.LENGTH_SHORT).show();
                loadUserData();
            } else {
                android.widget.Toast.makeText(this, "Error al actualizar", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        constructorDialogo.setNegativeButton("Cancelar", null);
        constructorDialogo.show();
    }

    private void mostrarDialogoCambioPass() {
        android.app.AlertDialog.Builder constructorDialogo = new android.app.AlertDialog.Builder(this);
        constructorDialogo.setTitle("Cambiar Contraseña");

        android.widget.LinearLayout contenedor = new android.widget.LinearLayout(this);
        contenedor.setOrientation(android.widget.LinearLayout.VERTICAL);
        contenedor.setPadding(50, 40, 50, 10);

        final android.widget.EditText inputCurrent = new android.widget.EditText(this);
        inputCurrent.setHint("Contraseña Actual");
        inputCurrent.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        contenedor.addView(inputCurrent);

        final android.widget.EditText inputNew = new android.widget.EditText(this);
        inputNew.setHint("Nueva Contraseña");
        inputNew.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        contenedor.addView(inputNew);

        constructorDialogo.setView(contenedor);

        constructorDialogo.setPositiveButton("Cambiar", (miDialogo, which) -> {
            String currentPass = inputCurrent.getText().toString();
            String newPass = inputNew.getText().toString();
            cambiarContrasena(currentPass, newPass);
        });
        constructorDialogo.setNegativeButton("Cancelar", (miDialogo, which) -> miDialogo.cancel());

        constructorDialogo.show();
    }

    private void cambiarContrasena(String currentPass, String newPass) {
        String correo = GestorUsuarios.getInstance().getCurrentUserEmail();
        if (correo == null) {
            android.widget.Toast.makeText(this, "No hay usuario logueado", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        GestorBaseDatos db = GestorUsuarios.getInstance().getDbHelper();
        if (db.checkUserPassword(correo, currentPass)) {
            if (db.updatePassword(correo, newPass)) {
                android.widget.Toast
                        .makeText(this, "Contraseña actualizada correctamente", android.widget.Toast.LENGTH_SHORT)
                        .show();
            } else {
                android.widget.Toast
                        .makeText(this, "Error al actualizar la contraseña", android.widget.Toast.LENGTH_SHORT).show();
            }
        } else {
            android.widget.Toast.makeText(this, "La contraseña actual es incorrecta", android.widget.Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void updateThemeIcon() {
        int mode = PreferenciasApp.getThemeMode(this);
        if (mode == 1) {
            btnThemeToggle.setImageResource(R.drawable.ic_moon); // Si está en luz, mostrar luna
        } else {
            btnThemeToggle.setImageResource(R.drawable.ic_sun); // Si está en oscuro, mostrar sol
        }
    }
}
