package frgp.utn.com.gestionsalvador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import frgp.utn.com.gestionsalvador.clientes.ClienteActivity;
import frgp.utn.com.gestionsalvador.mercaderia.MercaderiaActivity;
import frgp.utn.com.gestionsalvador.pedidos.PedidoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Botón Pedidos
        Button btnPedidos = findViewById(R.id.btnPedidos);
        btnPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viajeAPedidos = new Intent(MainActivity.this, PedidoActivity.class);
                startActivity(viajeAPedidos);
            }
        });

        // Botón Clientes
        Button btnClientes = findViewById(R.id.btnClientes);
        btnClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClienteActivity.class);
                startActivity(intent);
            }
        });

        // Botón Mercadería
        Button botonMercaderia = findViewById(R.id.btnMercaderia);
        botonMercaderia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viajeAMercaderia = new Intent(MainActivity.this, MercaderiaActivity.class);
                startActivity(viajeAMercaderia);
            }
        });

        // NUEVO: Botón Reportes y Caja Diaria
        Button btnReportes = findViewById(R.id.btnReportes);
        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Próximamente: Panel de Reportes", Toast.LENGTH_SHORT).show();

                // Más adelante, cuando creemos la Activity de reportes, será así:
                // Intent intent = new Intent(MainActivity.this, ReportesActivity.class);
                // startActivity(intent);
            }
        });
    }
}