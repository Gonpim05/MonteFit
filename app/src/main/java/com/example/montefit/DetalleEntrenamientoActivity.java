package com.example.montefit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DetalleEntrenamientoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_entrenamiento);

        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        ListView lvEjercicios = findViewById(R.id.lvEjerciciosDetalle);
        TextView tvTotalKilos = findViewById(R.id.tvTotalKilos);

        Entrenamiento entrenamiento = (Entrenamiento) getIntent().getSerializableExtra("entrenamiento");

        if (entrenamiento != null) {
            tvTitulo.setText("Detalle del " + entrenamiento.getFecha());

            double totalPeso = 0;
            java.util.List<String> displayList = new java.util.ArrayList<>();

            for (Entrenamiento.EjercicioDetalle ed : entrenamiento.getEjerciciosDetalle()) {
                String item = ed.nombre + ": " + ed.series + " series x " + ed.peso + " kg";
                displayList.add(item);
                totalPeso += (ed.series * ed.peso);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
            lvEjercicios.setAdapter(adapter);

            tvTotalKilos.setText("Total Kilos Levantados: " + String.format("%.1f", totalPeso) + " kg");
        } else {
            Toast.makeText(this, "Error al cargar entrenamiento", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
