package frgp.utn.com.gestionsalvador.mercaderia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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

        // 1. Botón de la flechita en el Header (Verde Oliva)
        ImageView btnVolverHeader = findViewById(R.id.btn_volver_mercaderia);
        btnVolverHeader.setOnClickListener(v -> finish());

        // 2. Botón de abajo "Volver al Menú"
        Button btnVolverAbajo = findViewById(R.id.btnVolver);
        btnVolverAbajo.setOnClickListener(v -> finish());
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
}

