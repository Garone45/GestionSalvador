package frgp.utn.com.gestionsalvador.reportes;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import frgp.utn.com.gestionsalvador.R;

import Entidad.Pedido;

public class  ReportesActivity extends AppCompatActivity {

    private RadioGroup rgFiltros;
    private TextView tvTotalReporte;
    private ImageView btnVolver;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        db = FirebaseFirestore.getInstance();

        rgFiltros = findViewById(R.id.rgFiltros);
        tvTotalReporte = findViewById(R.id.tvTotalReporte);
        btnVolver = findViewById(R.id.btnVolver);

        // BOTÓN VOLVER: Cierra esta activity y regresa al MainActivity
        btnVolver.setOnClickListener(v -> finish());

        // Cargamos el reporte por defecto al abrir ("Hoy")
        calcularReporte("Hoy");

        // Escuchamos cuando cambie el filtro de los RadioButtons
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
                        java.util.Calendar hoy = java.util.Calendar.getInstance();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pedido pedido = document.toObject(Pedido.class);

                            // Validamos que esté entregado
                            if (pedido.getEstado() != null && pedido.getEstado().equalsIgnoreCase("Entregado")) {

                                java.util.Date fechaPedidoDate = pedido.getFechaDate();

                                if (fechaPedidoDate != null) {
                                    java.util.Calendar fechaPedido = java.util.Calendar.getInstance();
                                    fechaPedido.setTime(fechaPedidoDate);

                                    boolean incluir = false;

                                    if (filtro.equals("Hoy")) {
                                        // Mismo año y mismo día del año
                                        incluir = (hoy.get(java.util.Calendar.YEAR) == fechaPedido.get(java.util.Calendar.YEAR) &&
                                                hoy.get(java.util.Calendar.DAY_OF_YEAR) == fechaPedido.get(java.util.Calendar.DAY_OF_YEAR));
                                    } else if (filtro.equals("Semana")) {
                                        // Mismo año y misma semana
                                        incluir = (hoy.get(java.util.Calendar.YEAR) == fechaPedido.get(java.util.Calendar.YEAR) &&
                                                hoy.get(java.util.Calendar.WEEK_OF_YEAR) == fechaPedido.get(java.util.Calendar.WEEK_OF_YEAR));
                                    } else if (filtro.equals("Mes")) {
                                        // Mismo año y mismo mes
                                        incluir = (hoy.get(java.util.Calendar.YEAR) == fechaPedido.get(java.util.Calendar.YEAR) &&
                                                hoy.get(java.util.Calendar.MONTH) == fechaPedido.get(java.util.Calendar.MONTH));
                                    }

                                    // Si cumple con el filtro de fecha, lo sumamos al total
                                    if (incluir) {
                                        totalRecaudado += pedido.getTotal();
                                    }
                                }
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