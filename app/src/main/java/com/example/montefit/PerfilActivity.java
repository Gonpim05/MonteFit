package com.example.montefit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {
    private android.widget.TextView tvNombre, tvEmail, tvDetails;
    private com.google.android.material.button.MaterialButton btnCambiarPass, btnEditarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        tvNombre = findViewById(R.id.tvNombrePerfil);
        tvEmail = findViewById(R.id.tvEmailPerfil);
        tvDetails = findViewById(R.id.tvDetails);
        btnCambiarPass = findViewById(R.id.btnCambiarContrasena);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);

        loadUserData();

        btnCambiarPass.setOnClickListener(v -> mostrarDialogoCambioPass());
        btnEditarPerfil.setOnClickListener(v -> mostrarDialogoEditarPerfil());
    }

    private void loadUserData() {
        String email = UserManager.getInstance().getCurrentUserEmail();
        if (email != null) {
            tvEmail.setText(email);
            DatabaseHelper db = UserManager.getInstance().getDbHelper();
            if (db != null) {
                android.database.Cursor cursor = db.getUserProfile(email);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    int age = cursor.isNull(cursor.getColumnIndexOrThrow("age")) ? 0
                            : cursor.getInt(cursor.getColumnIndexOrThrow("age"));
                    double weight = cursor.isNull(cursor.getColumnIndexOrThrow("weight")) ? 0.0
                            : cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
                    String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));

                    tvNombre.setText(name != null ? name : "Sin Nombre");
                    tvDetails.setText(
                            String.format("Edad: %d | Peso: %.1fkg | Sexo: %s", age, weight, sex != null ? sex : "--"));
                    cursor.close();
                }
            }
        }
    }

    private void mostrarDialogoEditarPerfil() {
        String email = UserManager.getInstance().getCurrentUserEmail();
        if (email == null)
            return;
        DatabaseHelper db = UserManager.getInstance().getDbHelper();
        String currentName = "";
        int currentAge = 0;
        double currentWeight = 0;
        String currentSex = "";

        android.database.Cursor cursor = db.getUserProfile(email);
        if (cursor != null && cursor.moveToFirst()) {
            currentName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow("age")))
                currentAge = cursor.getInt(cursor.getColumnIndexOrThrow("age"));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow("weight")))
                currentWeight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
            currentSex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
            cursor.close();
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Editar Perfil");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final android.widget.EditText inputName = new android.widget.EditText(this);
        inputName.setHint("Nombre");
        inputName.setText(currentName);
        layout.addView(inputName);

        final android.widget.EditText inputAge = new android.widget.EditText(this);
        inputAge.setHint("Edad");
        inputAge.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (currentAge > 0)
            inputAge.setText(String.valueOf(currentAge));
        layout.addView(inputAge);

        final android.widget.EditText inputWeight = new android.widget.EditText(this);
        inputWeight.setHint("Peso (kg)");
        inputWeight.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (currentWeight > 0)
            inputWeight.setText(String.valueOf(currentWeight));
        layout.addView(inputWeight);

        final android.widget.EditText inputSex = new android.widget.EditText(this);
        inputSex.setHint("Sexo (Hombre/Mujer)");
        if (currentSex != null)
            inputSex.setText(currentSex);
        layout.addView(inputSex);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String newName = inputName.getText().toString();
            String ageStr = inputAge.getText().toString();
            String weightStr = inputWeight.getText().toString();
            String newSex = inputSex.getText().toString();

            int newAge = ageStr.isEmpty() ? 0 : Integer.parseInt(ageStr);
            double newWeight = weightStr.isEmpty() ? 0 : Double.parseDouble(weightStr);

            if (db.updateUserProfile(email, newName, newAge, newWeight, newSex)) {
                android.widget.Toast.makeText(this, "Perfil actualizado", android.widget.Toast.LENGTH_SHORT).show();
                loadUserData();
            } else {
                android.widget.Toast.makeText(this, "Error al actualizar", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoCambioPass() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Cambiar Contraseña");

        // Layout del diálogo
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final android.widget.EditText inputCurrent = new android.widget.EditText(this);
        inputCurrent.setHint("Contraseña Actual");
        inputCurrent.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputCurrent);

        final android.widget.EditText inputNew = new android.widget.EditText(this);
        inputNew.setHint("Nueva Contraseña");
        inputNew.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputNew);

        builder.setView(layout);

        builder.setPositiveButton("Cambiar", (dialog, which) -> {
            String currentPass = inputCurrent.getText().toString();
            String newPass = inputNew.getText().toString();
            cambiarContrasena(currentPass, newPass);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void cambiarContrasena(String currentPass, String newPass) {
        String email = UserManager.getInstance().getCurrentUserEmail();
        if (email == null) {
            android.widget.Toast.makeText(this, "No hay usuario logueado", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = UserManager.getInstance().getDbHelper();
        if (db.checkUserPassword(email, currentPass)) {
            if (db.updatePassword(email, newPass)) {
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
}
