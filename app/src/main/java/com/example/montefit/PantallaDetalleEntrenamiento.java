package com.example.montefit;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class PantallaDetalleEntrenamiento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pantalla_detalle_entrenamiento);

        TextView tvTitulo = findViewById(R.id.tvTituloDetalle);
        ListView lvEjercicios = findViewById(R.id.lvEjerciciosDetalle);
        TextView tvTotalKilos = findViewById(R.id.tvTotalKilos);

        Entrenamiento entrenamiento;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            entrenamiento = getIntent().getSerializableExtra("entrenamiento", Entrenamiento.class);
        } else {
            @SuppressWarnings("deprecation")
            Entrenamiento oldEntrenamiento = (Entrenamiento) getIntent().getSerializableExtra("entrenamiento");
            entrenamiento = oldEntrenamiento;
        }

        if (entrenamiento != null) {
            tvTitulo.setText("📋 Detalle del " + entrenamiento.getFecha());

            double totalPeso = 0;
            java.util.List<String> displayList = new java.util.ArrayList<>();

            for (Entrenamiento.EjercicioDetalle ed : entrenamiento.getEjerciciosDetalle()) {
                String elemento = "💪 " + ed.nombre + " (Set " + ed.series + "): " + ed.peso + " kg x " + ed.repeticiones + " reps";
                displayList.add(elemento);
                // Calculamos el volumen real: Peso x Repeteciones de cada serie
                totalPeso += (ed.peso * ed.repeticiones);
            }

            ArrayAdapter<String> miAdaptador = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, displayList);
            lvEjercicios.setAdapter(miAdaptador);

            tvTotalKilos.setText("🏋️ Total Kilos: " + String.format("%.1f", totalPeso) + " kg");
        } else {
            Toast.makeText(this, "Error al cargar entrenamiento", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
