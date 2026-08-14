package frgp.utn.com.gestionsalvador.clientes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button; // <-- Importante: Agregado el import del Button
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import Adaptadores.ClienteAdapter;
import Entidad.Cliente;
import frgp.utn.com.gestionsalvador.R;

public class ClienteActivity extends AppCompatActivity {

    // Variables de la interfaz
    private RecyclerView rvListaClientes;
    private FloatingActionButton fabAgregarCliente;
    private Button btnVolver;

    // Variables de Firebase y el Adapter
    private FirebaseFirestore db;
    private ClienteAdapter adapter;
    private List<Cliente> listaClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);

        // 1. Conectamos las variables con los IDs del XML
        rvListaClientes = findViewById(R.id.rvListaClientes);
        fabAgregarCliente = findViewById(R.id.fabAgregarCliente);
        btnVolver = findViewById(R.id.btnVolver); // <-- Dejé solo uno, el otro estaba duplicado

        // 2. Inicializamos la base de datos de Firestore
        db = FirebaseFirestore.getInstance();

        // 3. Configuramos la lista y el Adapter vacíos por ahora
        rvListaClientes.setLayoutManager(new LinearLayoutManager(this));
        listaClientes = new ArrayList<>();
        adapter = new ClienteAdapter(listaClientes);
        rvListaClientes.setAdapter(adapter);

        // 4. Configuramos los botones
        fabAgregarCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClienteActivity.this, AgregarClienteActivity.class);
                startActivity(intent);
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // onResume se ejecuta cada vez que la pantalla vuelve a estar visible.
    // Ideal para recargar la lista si venimos de agregar un cliente nuevo.
    @Override
    protected void onResume() {
        super.onResume();
        cargarClientesDesdeFirestore();
    }

    private void cargarClientesDesdeFirestore() {
        // Asegurate de que tu colección en Firebase se llame exactamente "clientes"
        db.collection("clientes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Limpiamos la lista antes de cargar para no duplicar datos visualmente
                        listaClientes.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Mapeo manual (es la forma más segura para evitar crashes si los nombres no coinciden 100%)
                            Cliente cliente = new Cliente();

                            // Asegurate de que los strings en verde coincidan EXACTAMENTE con los nombres de los campos en tu Firestore
                            cliente.setNombre(document.getString("nombre"));
                            cliente.setApellido(document.getString("apellido"));
                            cliente.setCalle(document.getString("calle"));
                            cliente.setNumero(document.getString("numero"));
                            cliente.setLocalidad(document.getString("localidad"));
                            cliente.setTelefono(document.getString("telefono"));

                            listaClientes.add(cliente);
                        }
                        // Le avisamos al adapter que los datos cambiaron para que refresque la pantalla
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "Error al obtener clientes", task.getException());
                        Toast.makeText(ClienteActivity.this, "Error al cargar la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}