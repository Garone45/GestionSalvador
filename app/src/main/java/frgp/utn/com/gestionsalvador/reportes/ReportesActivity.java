package frgp.utn.com.gestionsalvador.reportes;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import frgp.utn.com.gestionsalvador.R;

import Entidad.Pedido;

public class ReportesActivity extends AppCompatActivity {

    private RadioGroup rgFiltros;
    private TextView tvTotalReporte;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        db = FirebaseFirestore.getInstance();

        rgFiltros = findViewById(R.id.rgFiltros);
        tvTotalReporte = findViewById(R.id.tvTotalReporte);

        // Cargamos el reporte por defecto al abrir (por ejemplo, "Hoy")
        calcularReporte("Hoy");

        // Escuchamos cuando tu hermana cambie el filtro de los RadioButtons
        rgFiltros.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbHoy) {
                calcularReporte("Hoy");
            } else if (checkedId == R.id.rbSemana) {
                calcularReporte("Semana");
            } else if (checkedId == R.id.rbMes) {
                calcularReporte("Mes");
            }
        });
    }

    private void calcularReporte(String filtro) {
        db.collection("pedidos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double totalRecaudado = 0.0;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pedido pedido = document.toObject(Pedido.class);

                            // Sumamos los montos de los pedidos que ya fueron entregados
                            if (pedido.getEstado() != null && pedido.getEstado().equalsIgnoreCase("Entregado")) {
                                totalRecaudado += pedido.getTotal();
                            }
                        }

                        // Mostramos el total formateado en pantalla
                        tvTotalReporte.setText("$ " + String.format("%.2f", totalRecaudado));

                    } else {
                        Toast.makeText(ReportesActivity.this, "Error al calcular los reportes", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}