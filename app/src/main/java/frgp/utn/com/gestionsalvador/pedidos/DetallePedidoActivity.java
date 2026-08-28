package frgp.utn.com.gestionsalvador.pedidos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import Adaptadores.PickingAdapter;
import Entidad.Producto;
import frgp.utn.com.gestionsalvador.R;

public class DetallePedidoActivity extends AppCompatActivity {

    private TextView tvCliente, tvDireccion, tvTelefono, tvProductos, tvObservaciones, tvTotal;
    private ArrayList<Producto> listaProductosPedido; // Guardamos la lista globalmente para usarla en el diálogo
    private String pedidoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_pedido);

        pedidoId = getIntent().getStringExtra("pedido_id");

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

        // RECIBIMOS LA LISTA DE OBJETOS PARA LA EDICIÓN Y PICKING
        listaProductosPedido = (ArrayList<Producto>) getIntent().getSerializableExtra("lista_productos_objeto");

        // Seteamos los textos en las tarjetas
        tvCliente.setText("Cliente: " + (cliente != null ? cliente : "Sin nombre"));
        tvDireccion.setText("Dirección: " + (direccion != null ? direccion : "Sin dirección"));
        tvTelefono.setText("Teléfono: " + (telefono != null ? telefono : "Sin teléfono"));
        tvProductos.setText(productos != null ? productos : "Sin productos detallados");
        tvObservaciones.setText("Observaciones: " + (observaciones != null && !observaciones.isEmpty() ? observaciones : "Ninguna"));
        tvTotal.setText("Total: $ " + total);

        // CONFIGURAR CLIC EN LA TARJETA DE PRODUCTOS PARA ABRIR EL DIÁLOGO DE PICKING (MODO JAVI)
        tvProductos.setOnClickListener(v -> mostrarDialogPicking());

        // Referencias a los botones de acción
        Button btnMarcarEntregado = findViewById(R.id.btnMarcarEntregado);
        Button btnEditarPedido = findViewById(R.id.btnEditarPedido);

        // VALIDACIÓN: Si ya está entregado, ocultamos ambos botones por completo
        if (estado != null && estado.equalsIgnoreCase("Entregado")) {
            btnMarcarEntregado.setVisibility(View.GONE);
            if (btnEditarPedido != null) btnEditarPedido.setVisibility(View.GONE);
        } else {
            btnMarcarEntregado.setVisibility(View.VISIBLE);
            if (btnEditarPedido != null) btnEditarPedido.setVisibility(View.VISIBLE);

            // Funcionalidad del botón para marcar como Entregado
            btnMarcarEntregado.setOnClickListener(v -> {
                if (pedidoId != null && !pedidoId.isEmpty()) {
                    FirebaseFirestore.getInstance()
                            .collection("pedidos")
                            .document(pedidoId)
                            .update("estado", "Entregado")
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "¡Pedido marcado como Entregado!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "ID de pedido no encontrado", Toast.LENGTH_SHORT).show();
                }
            });

            // Funcionalidad del botón para Editar Pedido
            if (btnEditarPedido != null) {
                btnEditarPedido.setOnClickListener(v -> {
                    if (pedidoId != null && !pedidoId.isEmpty()) {
                        Intent intent = new Intent(DetallePedidoActivity.this, CrearPedidoActivity.class);
                        intent.putExtra("pedido_id_editar", pedidoId);
                        if (listaProductosPedido != null) {
                            intent.putExtra("productos_a_editar", listaProductosPedido);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "ID de pedido no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // MÉTODO PARA ABRIR EL DIÁLOGO DE ARMADO (PICKING)
    // MÉTODO PARA ABRIR EL DIÁLOGO DE ARMADO (PICKING)
    private void mostrarDialogPicking() {
        if (listaProductosPedido == null || listaProductosPedido.isEmpty()) {
            Toast.makeText(this, "No hay productos detallados para armar", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Inflamos el diseño del diálogo
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_picking, null);

        // 2. Configuramos el RecyclerView dentro del diálogo
        RecyclerView recycler = dialogView.findViewById(R.id.recyclerDialogPicking);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 3. Convertimos la lista de objetos Producto a una lista de Strings legibles para Javi
        List<String> textosParaPicking = new ArrayList<>();
        for (Producto p : listaProductosPedido) {
            String linea = p.getCantidad() + " - " + p.getNombre();
            textosParaPicking.add(linea);
        }

        // 4. Conectamos nuestro adaptador con casillas de verificación
        PickingAdapter adapter = new PickingAdapter(textosParaPicking);
        recycler.setAdapter(adapter);

        // 5. Construimos y mostramos el AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // 6. Botón para finalizar el armado desde el diálogo
        Button btnFinalizar = dialogView.findViewById(R.id.btnFinalizarArmado);
        btnFinalizar.setOnClickListener(v -> {
            if (pedidoId != null && !pedidoId.isEmpty()) {
                FirebaseFirestore.getInstance()
                        .collection("pedidos")
                        .document(pedidoId)
                        .update("estado", "Armado")
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "¡Pedido armado con éxito!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); // Cierra el diálogo
                            finish(); // Cierra el detalle y vuelve automáticamente a la lista de pedidos
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "ID de pedido no encontrado", Toast.LENGTH_SHORT).show();
            }
        });

        // 7. ¡Mostramos la ventana en pantalla!
        dialog.show();
    }
}