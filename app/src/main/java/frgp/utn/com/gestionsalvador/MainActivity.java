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
import frgp.utn.com.gestionsalvador.reportes.ReportesActivity;


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

        // BOTÓN REPORTES Y CAJA DIARIA (¡Activado!)
        Button btnReportes = findViewById(R.id.btnReportes);
        btnReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reemplazamos el Toast por el Intent que viaja a la pantalla de reporte
                Intent intent = new Intent(MainActivity.this, ReportesActivity.class); // Cambiá "ReporteActivity" si tu clase se llama distinto
                startActivity(intent);
            }
        });
    }
}