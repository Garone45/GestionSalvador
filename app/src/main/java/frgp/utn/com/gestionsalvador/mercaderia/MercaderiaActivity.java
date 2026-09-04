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

                        // Limpia y convierte el precio de forma segura
                        guardarNuevoComboEnFirestore(nombre, precioStr);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
        // 3. Botón de abajo "Eliminar Producto" (Abre un listado para elegir cuál borrar)
        Button btnEliminarProducto = findViewById(R.id.btn_eliminar_producto);
        btnEliminarProducto.setOnClickListener(v -> {
            if (listaProductos.isEmpty()) {
                Toast.makeText(this, "No hay productos para eliminar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Extraemos los nombres de todos los productos cargados en la lista
            String[] nombresProductos = new String[listaProductos.size()];
            for (int i = 0; i < listaProductos.size(); i++) {
                nombresProductos[i] = listaProductos.get(i).getNombre();
            }

            // Mostramos un diálogo flotante con la lista para seleccionar
            new AlertDialog.Builder(this)
                    .setTitle("Seleccioná el producto a eliminar")
                    .setItems(nombresProductos, (dialog, which) -> {
                        // Obtenemos exactamente el producto que tocó el usuario
                        Producto productoSeleccionado = listaProductos.get(which);


                        confirmarYBorrarProducto(productoSeleccionado);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void cargarListaDesdeFirestore() {
        // Usamos addSnapshotListener en lugar de .get() para velocidad instantánea con caché local
        db.collection("productos")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(MercaderiaActivity.this, "Error al traer mercadería", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        listaProductos.clear();

                        for (QueryDocumentSnapshot document : value) {
                            Producto p = document.toObject(Producto.class);

                            if (p.getId() == null) {
                                p.setId(document.getId());
                            }

                            listaProductos.add(p);
                        }

                        // Refrescamos el adaptador al instante
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void guardarNuevoComboEnFirestore(String nombre, String precioStr) {
        precioStr = precioStr.replace(" ", "").replace(".", "").replace(",", ".");

        double precioFinal = 0.0;
        try {
            precioFinal = Double.parseDouble(precioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "El precio ingresado no es válido", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("precio_venta", precioFinal);
        producto.put("categoria", "Combos");
        producto.put("tipo_venta", "Unidad");

        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "¡Combo agregado con éxito!", Toast.LENGTH_SHORT).show();
                    cargarListaDesdeFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar el combo", Toast.LENGTH_SHORT).show();
                });
    }
    private void confirmarYBorrarProducto(Producto producto) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que querés eliminar '" + producto.getNombre() + "'?")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> {
                    if (producto.getId() != null && !producto.getId().isEmpty()) {
                        db.collection("productos").document(producto.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "¡Producto eliminado con éxito!", Toast.LENGTH_SHORT).show();
                                    // Como estamos usando addSnapshotListener,
                                    // la lista se va a actualizar sola en la pantalla al instante.
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al eliminar el producto", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void clickVolver(View view) {
        Toast.makeText(this, "CLICK POR XML", Toast.LENGTH_SHORT).show();
        finish();
    }
}