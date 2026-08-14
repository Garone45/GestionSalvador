package frgp.utn.com.gestionsalvador.pedidos;

import android.os.Bundle;
import android.view.View; // <-- Importante para usar View.VISIBLE / View.GONE
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import frgp.utn.com.gestionsalvador.R;

public class DetallePedidoActivity extends AppCompatActivity {

    private TextView tvCliente, tvDireccion, tvTelefono, tvProductos, tvObservaciones, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        // Enlazamos vistas
        tvCliente = findViewById(R.id.tvDetalleCliente);
        tvDireccion = findViewById(R.id.tvDetalleDireccion);
        tvTelefono = findViewById(R.id.tvDetalleTelefono);
        tvProductos = findViewById(R.id.tvDetalleProductos);
        tvObservaciones = findViewById(R.id.tvDetalleObservaciones);
        tvTotal = findViewById(R.id.tvDetalleTotal);

        ImageView btnVolver = findViewById(R.id.btn_volver_detalle);
        btnVolver.setOnClickListener(v -> finish());

        // Recibimos los datos enviados desde el adaptador
        String pedidoId = getIntent().getStringExtra("pedido_id");
        String cliente = getIntent().getStringExtra("nombre_cliente");
        String direccion = getIntent().getStringExtra("direccion_cliente");
        String telefono = getIntent().getStringExtra("telefono_cliente");
        String productos = getIntent().getStringExtra("productos_detalle");
        String observaciones = getIntent().getStringExtra("observaciones_cliente");
        double total = getIntent().getDoubleExtra("total_pedido", 0.0);

        // RECIBIMOS EL ESTADO DEL PEDIDO
        String estado = getIntent().getStringExtra("estado_pedido");

        // Seteamos los textos en las tarjetas
        tvCliente.setText("Cliente: " + (cliente != null ? cliente : "Sin nombre"));
        tvDireccion.setText("Dirección: " + (direccion != null ? direccion : "Sin dirección"));
        tvTelefono.setText("Teléfono: " + (telefono != null ? telefono : "Sin teléfono"));
        tvProductos.setText(productos != null ? productos : "Sin productos detallados");
        tvObservaciones.setText("Observaciones: " + (observaciones != null && !observaciones.isEmpty() ? observaciones : "Ninguna"));
        tvTotal.setText("Total: $ " + total);

        // Configuración del botón para marcar como Entregado
        Button btnMarcarEntregado = findViewById(R.id.btnMarcarEntregado);

        // VALIDACIÓN: Si ya está entregado, ocultamos el botón por completo
        if (estado != null && estado.equalsIgnoreCase("Entregado")) {
            btnMarcarEntregado.setVisibility(View.GONE);
        } else {
            btnMarcarEntregado.setVisibility(View.VISIBLE);

            // Si está pendiente, dejamos activa la funcionalidad del botón
            btnMarcarEntregado.setOnClickListener(v -> {
                if (pedidoId != null && !pedidoId.isEmpty()) {
                    FirebaseFirestore.getInstance()
                            .collection("pedidos")
                            .document(pedidoId)
                            .update("estado", "Entregado")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "¡Pedido marcado como Entregado!", Toast.LENGTH_SHORT).show();
                                finish(); // Cierra el detalle y vuelve a la lista
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "ID de pedido no encontrado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}