package frgp.utn.com.gestionsalvador.pedidos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Adaptadores.ResumenAdapter;
import Entidad.Pedido;
import Entidad.Producto;
import frgp.utn.com.gestionsalvador.R;

public class ResumenPedidoActivity extends AppCompatActivity {

    private FirebaseFirestore db; // Preparando la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_pedido);

        // Inicializamos Firebase
        db = FirebaseFirestore.getInstance();

        // 1. Enlazamos las etiquetas del cliente
        TextView tvNombre = findViewById(R.id.tv_resumen_nombre);
        TextView tvDireccion = findViewById(R.id.tv_resumen_direccion);
        TextView tvTelefono = findViewById(R.id.tv_resumen_telefono);
        TextView tvObservaciones = findViewById(R.id.tv_resumen_observaciones);

        // Y las etiquetas del ticket (Lista y Total)
        RecyclerView rvProductos = findViewById(R.id.rv_resumen_productos);
        TextView tvTotal = findViewById(R.id.tv_resumen_total);

        ImageView btnVolver = findViewById(R.id.btn_volver_resumen);
        btnVolver.setOnClickListener(v -> finish());

        // 2. ¡ABRIMOS LA MOCHILA AL 100%!
        // Datos del cliente...
        String nombre = getIntent().getStringExtra("nombre_cliente");
        String direccion = getIntent().getStringExtra("direccion_cliente");
        String telefono = getIntent().getStringExtra("telefono_cliente");
        String observaciones = getIntent().getStringExtra("observaciones_cliente");

        // ¡Y LAS VERDURAS CON LA PLATA!
        ArrayList<Producto> carrito = (ArrayList<Producto>) getIntent().getSerializableExtra("carrito_productos");
        double totalPedido = getIntent().getDoubleExtra("total_pedido", 0.0);

        // 3. Imprimimos los datos en la pantalla
        if (nombre != null) tvNombre.setText(nombre);
        if (direccion != null) tvDireccion.setText(direccion);
        if (telefono != null) tvTelefono.setText(telefono);
        if (observaciones != null && !observaciones.isEmpty()) {
            tvObservaciones.setText(observaciones);
        }

        tvTotal.setText("$ " + totalPedido);

        // 4. Configuramos la listita de productos elegidos
        if (carrito != null) {
            rvProductos.setLayoutManager(new LinearLayoutManager(this));
            ResumenAdapter adaptador = new ResumenAdapter(carrito);
            rvProductos.setAdapter(adaptador);
        }

        // 5. ¡LA MAGIA DE GUARDAR EN FIREBASE!
        Button btnConfirmar = findViewById(R.id.btn_confirmar_pedido);
        btnConfirmar.setOnClickListener(v -> {

            // Desactivamos el botón un segundito para que no lo toquen 2 veces sin querer
            btnConfirmar.setEnabled(false);
            btnConfirmar.setText("Guardando pedido...");

            // Sacamos la fecha actual de hoy
            String fechaHoy = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            // Armamos la caja con el pedido usando tu molde de Entidad.Pedido
            Pedido nuevoPedido = new Pedido(
                    nombre,
                    direccion,
                    telefono,
                    observaciones,
                    carrito,
                    fechaHoy,
                    totalPedido,
                    "Pendiente" // Estado inicial por defecto
            );

            // Lo mandamos a la colección "pedidos" en la nube
            db.collection("pedidos")
                    .add(nuevoPedido)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "¡Pedido guardado con éxito!", Toast.LENGTH_SHORT).show();

                        // Viajamos a la pantalla de Pedidos del Día
                        Intent intentFinal = new Intent(ResumenPedidoActivity.this, PedidoActivity.class);

                        // Este truquito borra el historial de pantallas para que si tocan "Atrás", no vuelvan al carrito vacío
                        intentFinal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentFinal);

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        // Si falla, volvemos a activar el botón
                        btnConfirmar.setEnabled(true);
                        btnConfirmar.setText("Confirmar y Guardar Pedido");
                    });
        });
    }
}