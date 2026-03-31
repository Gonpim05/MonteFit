package com.example.montefit;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LogrosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logros);

        dbHelper = UserManager.getInstance().getDbHelper();
        if (dbHelper == null) {
            UserManager.getInstance().init(this);
            dbHelper = UserManager.getInstance().getDbHelper();
        }

        recyclerView = findViewById(R.id.recyclerLogros);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get ID of current user
        String userEmail = UserManager.getInstance().getCurrentUserEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int userId = dbHelper.getUserId(userEmail);
        if (userId != -1) {
            loadAchievements(userId);
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadAchievements(int userId) {
        Cursor cursor = dbHelper.getLogros(userId);
        if (cursor != null && cursor.getCount() > 0) {
            LogrosAdapter adapter = new LogrosAdapter(this, cursor);
            recyclerView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "No hay logros disponibles", Toast.LENGTH_SHORT).show();
        }
    }
}
