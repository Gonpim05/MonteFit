package com.example.montefit;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PantallaLogros extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GestorBaseDatos gestorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pantalla_logros);

        gestorBD = GestorUsuarios.getInstance().getDbHelper();
        if (gestorBD == null) {
            GestorUsuarios.getInstance().init(this);
            gestorBD = GestorUsuarios.getInstance().getDbHelper();
        }

        recyclerView = findViewById(R.id.recyclerLogros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get ID of current user
        String correoUsuario = GestorUsuarios.getInstance().getCurrentUserEmail();
        if (correoUsuario == null || correoUsuario.isEmpty()) {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int userId = gestorBD.getUserId(correoUsuario);
        if (userId != -1) {
            loadAchievements(userId);
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAchievements(int userId) {
        Cursor datosBD = gestorBD.getLogros(userId);
        if (datosBD != null && datosBD.getCount() > 0) {
            InterfazListaLogros miAdaptador = new InterfazListaLogros(this, datosBD);
            recyclerView.setAdapter(miAdaptador);
        } else {
            Toast.makeText(this, "No hay logros disponibles", Toast.LENGTH_SHORT).show();
        }
    }
}















