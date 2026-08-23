package frgp.utn.com.gestionsalvador.mercaderia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TUS CLASES
import Entidad.Producto;
import Adaptadores.ProductoAdapter;
import frgp.utn.com.gestionsalvador.R;

public class MercaderiaActivity extends AppCompatActivity {

    private RecyclerView rvListaMercaderia;
    private FirebaseFirestore db;
    private ProductoAdapter adapter;
    private List<Producto> listaProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mercaderia);

        // Configuración del RecyclerView
        rvListaMercaderia = findViewById(R.id.rvListaMercaderia);
        rvListaMercaderia.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        // Inicializamos la lista y el adaptador vacíos
        listaProductos = new ArrayList<>();
        adapter = new ProductoAdapter(listaProductos);
        rvListaMercaderia.setAdapter(adapter);

        // Llamamos a la función que trae los datos de Firestore
        cargarListaDesdeFirestore();

        // 1. Botón de la flechita en el Header para volver
        ImageView btnVolverHeader = findViewById(R.id.btn_volver_mercaderia);
        btnVolverHeader.setOnClickListener(v -> finish());

        // 2. Botón de abajo "Agregar Producto" (Abre la ventanita flotante)
        Button btnAgregarProducto = findViewById(R.id.btn_agregar_producto);
        btnAgregarProducto.setOnClickListener(v -> {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_producto, null);

            EditText etNombre = dialogView.findViewById(R.id.et_nombre_combo);
            EditText etPrecio = dialogView.findViewById(R.id.et_precio_combo);

            new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        String nombre = etNombre.getText().toString().trim();
                        String precioStr = etPrecio.getText().toString().trim();

                        if (nombre.isEmpty() || precioStr.isEmpty()) {
                            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        double precio = Double.parseDouble(precioStr);
                        guardarNuevoComboEnFirestore(nombre, precio);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void cargarListaDesdeFirestore() {
        db.collection("productos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaProductos.clear(); // Limpiamos la lista por las dudas

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Convertimos el documento de Firebase a un objeto Producto
                            Producto p = document.toObject(Producto.class);
                            listaProductos.add(p);
                        }

                        // Le avisamos al Adapter que ya tenemos los datos listos para dibujar
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MercaderiaActivity.this, "Error al traer mercadería", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarNuevoComboEnFirestore(String nombre, double precio) {
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("precio", precio);

        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Combo agregado con éxito!", Toast.LENGTH_SHORT).show();
                    cargarListaDesdeFirestore(); // Recarga la lista en el momento
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el combo", Toast.LENGTH_SHORT).show();
                });
    }
}