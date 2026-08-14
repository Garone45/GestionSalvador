package frgp.utn.com.gestionsalvador.pedidos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import Entidad.Producto;
import Entidad.Cliente; // <-- Asegurate de que tu clase Cliente se llame así
import frgp.utn.com.gestionsalvador.R;

public class DatosEntregaActivity extends AppCompatActivity {

    // Declaramos las nuevas variables separadas (¡etNombre ahora es AutoCompleteTextView!)
    private AutoCompleteTextView etNombre;
    private EditText etApellido;
    private EditText etCalle;
    private EditText etAltura;
    private EditText etLocalidad;
    private EditText etTelefonoCliente;
    private EditText etObservaciones;

    // Herramientas para la base de datos y la búsqueda
    private FirebaseFirestore db;
    private List<Cliente> listaClientesGuardados;
    private List<String> nombresParaElBuscador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_entrega);

        // Inicializamos Firebase y las listas
        db = FirebaseFirestore.getInstance();
        listaClientesGuardados = new ArrayList<>();
        nombresParaElBuscador = new ArrayList<>();

        // Enlazamos los botones de volver
        ImageView btnVolverHeader = findViewById(R.id.btn_volver_datos);
        TextView btnVolverProductos = findViewById(R.id.btn_volver_productos);

        btnVolverHeader.setOnClickListener(v -> finish());
        btnVolverProductos.setOnClickListener(v -> finish());

        // Enlazamos los nuevos cuadros de texto con los IDs del XML
        etNombre = findViewById(R.id.et_nombre);
        etApellido = findViewById(R.id.et_apellido);
        etCalle = findViewById(R.id.et_calle);
        etAltura = findViewById(R.id.et_altura);
        etLocalidad = findViewById(R.id.et_localidad);
        etTelefonoCliente = findViewById(R.id.et_telefono_cliente);
        etObservaciones = findViewById(R.id.et_observaciones);

        // 1. Descargamos los clientes en silencio
        cargarClientesParaBuscador();

        // 2. Configuramos la magia: ¿Qué pasa si toca un nombre de la lista desplegable?
        etNombre.setOnItemClickListener((parent, view, position, id) -> {
            // Agarramos el nombre que ella seleccionó (Ej: "Francisco Garone")
            String nombreSeleccionado = (String) parent.getItemAtPosition(position);

            // Buscamos a ese cliente en nuestra lista descargada
            for (Cliente c : listaClientesGuardados) {
                String nombreCompleto = c.getNombre() + " " + c.getApellido();

                if (nombreCompleto.equals(nombreSeleccionado)) {
                    // ¡Lo encontramos! Rellenamos todo automáticamente:
                    etNombre.setText(c.getNombre());
                    etApellido.setText(c.getApellido());
                    etCalle.setText(c.getCalle());
                    etAltura.setText(c.getAltura());
                    etLocalidad.setText(c.getLocalidad());
                    etTelefonoCliente.setText(c.getTelefono());

                    Toast.makeText(this, "Datos autocompletados", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        });

        // Enlazamos el botón de Continuar
        Button btnContinuar = findViewById(R.id.btn_continuar_datos);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Agarramos lo que el usuario escribió en cada casillero
                String nombre = etNombre.getText().toString().trim();
                String apellido = etApellido.getText().toString().trim();
                String calle = etCalle.getText().toString().trim();
                String altura = etAltura.getText().toString().trim();
                String localidad = etLocalidad.getText().toString().trim();
                String telefono = etTelefonoCliente.getText().toString().trim();
                String observaciones = etObservaciones.getText().toString().trim();

                // Verificamos que no hayan dejado los datos importantes vacíos
                if (nombre.isEmpty() || apellido.isEmpty() || calle.isEmpty() || altura.isEmpty() || localidad.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(DatosEntregaActivity.this, "Faltan completar datos obligatorios", Toast.LENGTH_SHORT).show();
                    return; // Frenamos la ejecución acá
                }

                // ¡EL TRUCAZO! Unimos los textos para que el Paso 3 no se entere del cambio
                String nombreCompleto = nombre + " " + apellido;
                String direccionCompleta = calle + " " + altura + ", " + localidad;

                // Preparamos el viaje al Paso 3 (El Resumen Final)
                Intent intent = new Intent(DatosEntregaActivity.this, ResumenPedidoActivity.class);

                // Metemos los datos del cliente ya armaditos en la mochila
                intent.putExtra("nombre_cliente", nombreCompleto);
                intent.putExtra("direccion_cliente", direccionCompleta);
                intent.putExtra("telefono_cliente", telefono);
                intent.putExtra("observaciones_cliente", observaciones);

                // ¡EL TRANSBORDO! Rescatamos las verduras y el total que venían del Paso 1
                ArrayList<Producto> carrito = (ArrayList<Producto>) getIntent().getSerializableExtra("carrito_productos");
                double totalPedido = getIntent().getDoubleExtra("total_pedido", 0.0);

                // Metemos las verduras y el total en la mochila nueva para que sigan viaje
                intent.putExtra("carrito_productos", carrito);
                intent.putExtra("total_pedido", totalPedido);

                // ¡Arrancamos viaje al resumen!
                startActivity(intent);
            }
        });
    }

    // --- FUNCIÓN QUE DESCARGA LOS CLIENTES EN SILENCIO ---
    private void cargarClientesParaBuscador() {
        db.collection("clientes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Convertimos el documento de Firebase a nuestro objeto Cliente
                    Cliente cliente = document.toObject(Cliente.class);
                    listaClientesGuardados.add(cliente);

                    // Armamos la etiqueta visual para el buscador
                    nombresParaElBuscador.add(cliente.getNombre() + " " + cliente.getApellido());
                }

                // Le cargamos la lista de nombres al cuadro de texto inteligente
                ArrayAdapter<String> adaptadorBuscador = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        nombresParaElBuscador
                );
                etNombre.setAdapter(adaptadorBuscador);

            } else {
                Log.e("BuscadorError", "No se pudieron cargar los clientes", task.getException());
            }
        });
    }
}