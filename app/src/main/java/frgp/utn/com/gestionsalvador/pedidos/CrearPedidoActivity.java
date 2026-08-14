package frgp.utn.com.gestionsalvador.pedidos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Adaptadores.PedidoAdapter;
import Entidad.Producto;
import frgp.utn.com.gestionsalvador.R;

public class CrearPedidoActivity extends AppCompatActivity {

    private RecyclerView rvCatalogo;
    private PedidoAdapter adaptador;
    private FirebaseFirestore db;

    // Listas ordenadas para el manejo de filtros y catálogo
    private List<Producto> listaProductosCompleta = new ArrayList<>();
    private List<Producto> listaProductosMostrados = new ArrayList<>();

    private TextView tvResumenCantidad;
    private TextView tvResumenTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_pedido);

        db = FirebaseFirestore.getInstance();

        rvCatalogo = findViewById(R.id.rv_catalogo_productos);
        rvCatalogo.setLayoutManager(new LinearLayoutManager(this));

        tvResumenCantidad = findViewById(R.id.tv_cant_productos);
        tvResumenTotal = findViewById(R.id.tv_precio_total);

        // Cargamos los productos desde Firestore al iniciar
        cargarCatalogoDesdeFirestore();

        ImageView btnVolver = findViewById(R.id.btn_volver);
        btnVolver.bringToFront();

        Button btnTodas = findViewById(R.id.btnTodas);
        Button btnVerduras = findViewById(R.id.btnVerduras);
        Button btnFrutas = findViewById(R.id.btnFrutas);

        btnTodas.setOnClickListener(v -> filtrarCategoria("Todas"));
        btnVerduras.setOnClickListener(v -> filtrarCategoria("Verduras"));
        btnFrutas.setOnClickListener(v -> filtrarCategoria("Frutas"));

        btnVolver.setOnClickListener(v -> finish());

        // BOTÓN CONTINUAR (¡ARREGLADO!)
        Button btnContinuar = findViewById(R.id.btn_continuar);
        btnContinuar.setOnClickListener(v -> {

            ArrayList<Producto> carrito = new ArrayList<>();
            double totalCalculado = 0;

            // 🔍 ¡CORRECCIÓN CLAVE! Recorremos la lista completa que tiene los datos reales y las cantidades cargadas
            for (Producto p : listaProductosCompleta) {
                if (p.getCantidad() > 0) {
                    carrito.add(p);
                    totalCalculado += (p.getPrecio_venta() * p.getCantidad());
                }
            }

            // Validamos que el carrito no esté vacío
            if (carrito.isEmpty()) {
                Toast.makeText(CrearPedidoActivity.this, "¡El carrito está vacío! Seleccioná mercadería", Toast.LENGTH_SHORT).show();
                return;
            }

            // Viajamos al Paso 2 enviando el carrito y el total calculado
            Intent intent = new Intent(CrearPedidoActivity.this, DatosEntregaActivity.class);
            intent.putExtra("carrito_productos", carrito);
            intent.putExtra("total_pedido", totalCalculado);
            startActivity(intent);
        });
    }

    private void cargarCatalogoDesdeFirestore() {
        db.collection("productos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaProductosCompleta.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Producto producto = document.toObject(Producto.class);
                            listaProductosCompleta.add(producto);
                        }

                        // Por defecto, al arrancar mostramos todos los productos
                        listaProductosMostrados.clear();
                        listaProductosMostrados.addAll(listaProductosCompleta);

                        // Inicializamos el adaptador si es nulo, o le avisamos si ya existía
                        if (adaptador == null) {
                            adaptador = new PedidoAdapter(listaProductosMostrados, new PedidoAdapter.OnCarritoChangeListener() {
                                @Override
                                public void onCarritoCambiado(double cantidadTotal, double precioTotal) {
                                    String cantTexto;
                                    if (cantidadTotal == (long) cantidadTotal) {
                                        cantTexto = String.valueOf((long) cantidadTotal);
                                    } else {
                                        cantTexto = String.valueOf(cantidadTotal);
                                    }

                                    tvResumenCantidad.setText(cantTexto + " productos");
                                    tvResumenTotal.setText("$ " + precioTotal);
                                }
                            });
                            rvCatalogo.setAdapter(adaptador);
                        } else {
                            adaptador.notifyDataSetChanged();
                        }

                    } else {
                        Log.w("CatalogoError", "Error al descargar la mercadería", task.getException());
                    }
                });
    }

    private void filtrarCategoria(String categoria) {
        listaProductosMostrados.clear();

        if (categoria.equals("Todas")) {
            listaProductosMostrados.addAll(listaProductosCompleta);
        } else {
            for (Producto p : listaProductosCompleta) {
                if (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase(categoria)) {
                    listaProductosMostrados.add(p);
                }
            }
        }

        if (adaptador != null) {
            adaptador.notifyDataSetChanged();
        }
    }
}