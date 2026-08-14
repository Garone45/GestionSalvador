package frgp.utn.com.gestionsalvador.clientes;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import frgp.utn.com.gestionsalvador.R;

public class DetalleClienteActivity extends AppCompatActivity {

    private TextView tvNombre, tvTelefono, tvDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_cliente);

        // Enlazamos vistas
        tvNombre = findViewById(R.id.tvDetalleClienteNombre);
        tvTelefono = findViewById(R.id.tvDetalleClienteTelefono);
        tvDireccion = findViewById(R.id.tvDetalleClienteDireccion);

        // Botón volver idéntico al de pedidos (cierra la pantalla actual y vuelve atrás)
        ImageView btnVolver = findViewById(R.id.btn_volver_detalle_cliente);
        btnVolver.setOnClickListener(v -> finish());

        // Recibimos los datos enviados desde el adaptador de clientes
        String nombre = getIntent().getStringExtra("cliente_nombre");
        String telefono = getIntent().getStringExtra("cliente_telefono");
        String direccion = getIntent().getStringExtra("cliente_direccion");

        // Seteamos los textos en pantalla
        tvNombre.setText("Nombre: " + (nombre != null ? nombre : "Sin nombre"));
        tvTelefono.setText("Teléfono: " + (telefono != null ? telefono : "Sin teléfono"));
        tvDireccion.setText("Dirección: " + (direccion != null ? direccion : "Sin dirección"));
    }
}