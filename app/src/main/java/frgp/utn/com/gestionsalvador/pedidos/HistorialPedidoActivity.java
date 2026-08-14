package frgp.utn.com.gestionsalvador.pedidos;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import Adaptadores.PedidoAdapterLista;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import Entidad.Pedido;
import frgp.utn.com.gestionsalvador.R;

public class HistorialPedidoActivity extends AppCompatActivity {

    private RecyclerView rvHistorial;
    private ImageButton btnVolver;
    private FirebaseFirestore db;
    private List<Pedido> listaPedidosHistorial;
    private PedidoAdapterLista adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial); // Asegurate que tu XML se llame activity_historial.xml

        db = FirebaseFirestore.getInstance();
        listaPedidosHistorial = new ArrayList<>();

        rvHistorial = findViewById(R.id.rvHistorialPedidos);
        btnVolver = findViewById(R.id.btnVolverHistorial);

        rvHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Inicializás tu adaptador con la lista vacía
        adapter = new PedidoAdapterLista(listaPedidosHistorial);
        rvHistorial.setAdapter(adapter);

        btnVolver.setOnClickListener(v -> finish());

        cargarPedidosHistorial();
    }





    private void cargarPedidosHistorial() {
        // 1. Calculamos el límite de hace 24 horas
        long hace24Horas = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        db.collection("pedidos").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaPedidosHistorial.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Pedido pedido = document.toObject(Pedido.class);

                    // 2. Convertimos el String de tu entidad a Milisegundos
                    try {
                        Date fechaPedido = sdf.parse(pedido.getFecha());
                        if (fechaPedido != null && fechaPedido.getTime() < hace24Horas) {
                            listaPedidosHistorial.add(pedido);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


}