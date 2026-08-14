package frgp.utn.com.gestionsalvador.pedidos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import android.widget.ImageButton;

import Adaptadores.PedidoAdapterLista;
import Entidad.Pedido;
import frgp.utn.com.gestionsalvador.R;

public class PedidoActivity extends AppCompatActivity {

    // Declaramos las herramientas para la lista y la base de datos
    private RecyclerView rvPedidos;
    private PedidoAdapterLista adaptador;
    private List<Pedido> listaPedidos;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        // Inicializamos Firebase y la lista vacía
        db = FirebaseFirestore.getInstance();
        listaPedidos = new ArrayList<>();

        // Enlazamos el RecyclerView (¡Revisá que este ID coincida con tu XML!)
        rvPedidos = findViewById(R.id.rv_pedidos);

        // Verificamos que el RecyclerView exista en el diseño para no tirar error
        if (rvPedidos != null) {
            rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        }

        // Buscamos el botón de volver
        FloatingActionButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Buscamos el botón de agregar
        FloatingActionButton btnAgregarPedido = findViewById(R.id.fabNuevoPedido);
        btnAgregarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PedidoActivity.this, CrearPedidoActivity.class);
                startActivity(intent);
            }
        });

        ImageButton btnHistorial = findViewById(R.id.btnHistorial);
        btnHistorial.setOnClickListener(v -> {
            Intent intent = new Intent(PedidoActivity.this, HistorialPedidoActivity.class);
            startActivity(intent);
        });

        // Llamamos a la función que descarga todo de Firebase
        cargarPedidosDesdeFirestore();
    }

    private void cargarPedidosDesdeFirestore() {
        // 1. Calculamos el límite de hace 24 horas respecto al momento actual
        long hace24Horas = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());

        db.collection("pedidos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaPedidos.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pedido pedido = document.toObject(Pedido.class);
                            pedido.setId(document.getId()); // Aseguramos que guarde bien el ID

                            // 2. Filtramos por fecha: solo agregamos si tiene menos de 24 horas
                            if (pedido.getFecha() != null) {
                                try {
                                    java.util.Date fechaPedido = sdf.parse(pedido.getFecha());
                                    if (fechaPedido != null && fechaPedido.getTime() >= hace24Horas) {
                                        listaPedidos.add(pedido);
                                    }
                                } catch (java.text.ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        // Si el adaptador ya existe, solo le avisamos que los datos cambiaron.
                        // Si no existe, lo creamos y se lo asignamos al RecyclerView por única vez.
                        if (adaptador == null) {
                            adaptador = new PedidoAdapterLista(listaPedidos);
                            if (rvPedidos != null) {
                                rvPedidos.setAdapter(adaptador);
                            }
                        } else {
                            adaptador.notifyDataSetChanged();
                        }

                        // Control de la vista vacía
                        View vistaVacia = findViewById(R.id.ll_estado_vacio);
                        if (vistaVacia != null && rvPedidos != null) {
                            if (listaPedidos.isEmpty()) {
                                vistaVacia.setVisibility(View.VISIBLE);
                                rvPedidos.setVisibility(View.GONE);
                            } else {
                                vistaVacia.setVisibility(View.GONE);
                                rvPedidos.setVisibility(View.VISIBLE);
                            }
                        }

                    } else {
                        Log.e("FirestoreError", "Error al descargar los pedidos", task.getException());
                        Toast.makeText(PedidoActivity.this, "Error al cargar la lista de pedidos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarPedidosDesdeFirestore();
    }
}
