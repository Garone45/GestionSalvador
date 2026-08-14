package frgp.utn.com.gestionsalvador.clientes;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import android.widget.ImageView;
import frgp.utn.com.gestionsalvador.R;

public class AgregarClienteActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etApellido, etCalle, etAltura, etLocalidad, etTelefono;
    private Button btnGuardar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_cliente);

        db = FirebaseFirestore.getInstance();

        // Enlazamos todos los nuevos campos
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCalle = findViewById(R.id.etCalle);
        etAltura = findViewById(R.id.etAltura);
        etLocalidad = findViewById(R.id.etLocalidad);
        etTelefono = findViewById(R.id.etTelefono);
        btnGuardar = findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCliente();
            }
        });
        ImageView btnVolver = findViewById(R.id.btn_volver_agregar_cliente);
        btnVolver.setOnClickListener(v -> finish());
    }

    private void guardarCliente() {
        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String calle = etCalle.getText().toString().trim();
        String altura = etAltura.getText().toString().trim();
        String localidad = etLocalidad.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();

        // Validamos que los campos esenciales no estén vacíos
        if (nombre.isEmpty() || apellido.isEmpty() || calle.isEmpty() || altura.isEmpty() || localidad.isEmpty()) {
            Toast.makeText(this, "Por favor, completá todos los datos de dirección y nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        // Armamos el mapa con la nueva estructura dividida
        Map<String, Object> cliente = new HashMap<>();
        cliente.put("nombre", nombre);
        cliente.put("apellido", apellido);
        cliente.put("calle", calle);
        cliente.put("altura", altura);
        cliente.put("localidad", localidad);
        cliente.put("telefono", telefono);

        db.collection("clientes")
                .add(cliente)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AgregarClienteActivity.this, "¡Cliente agregado con éxito!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AgregarClienteActivity.this, "Error al guardar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}