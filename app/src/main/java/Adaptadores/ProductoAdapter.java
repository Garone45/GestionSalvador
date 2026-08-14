package Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import Entidad.Producto;
import frgp.utn.com.gestionsalvador.R;

import android.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> listaProductos;

    public ProductoAdapter(List<Producto> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ⚠️ ACÁ ESTÁ EL CAMBIO: Le decimos que use el XML nuevo que creaste recién
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mercaderia, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto productoActual = listaProductos.get(position);

        holder.tvNombre.setText(productoActual.getNombre());
        holder.tvPrecio.setText("$ " + productoActual.getPrecio_venta());

        // LA MAGIA NUEVA: Qué pasa al tocar "Editar"
        holder.btnEditar.setOnClickListener(v -> {

            // 1. Creamos la ventanita emergente
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Actualizar Precio");
            builder.setMessage("Ingresá el nuevo precio para: " + productoActual.getNombre());

            // 2. Creamos el espacio para escribir (y le decimos que solo acepte números y decimales)
            final EditText inputPrecio = new EditText(v.getContext());
            inputPrecio.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            inputPrecio.setText(String.valueOf(productoActual.getPrecio_venta())); // Muestra el precio viejo para guiarte
            builder.setView(inputPrecio);

            // 3. Configuramos el botón "Guardar" de la ventanita
            builder.setPositiveButton("Guardar", (dialog, which) -> {
                String nuevoPrecioString = inputPrecio.getText().toString();

                // Verificamos que no hayan dejado el espacio vacío
                if (!nuevoPrecioString.isEmpty()) {
                    double nuevoPrecio = Double.parseDouble(nuevoPrecioString);

                    // 4. Vamos a Firebase a buscar el producto y actualizarle el precio
                    com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

                    db.collection("productos")
                            .whereEqualTo("nombre", productoActual.getNombre())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // Agarramos el ID interno del documento en la nube
                                    String idDocumento = queryDocumentSnapshots.getDocuments().get(0).getId();

                                    // Le mandamos el precio nuevo
                                    db.collection("productos").document(idDocumento)
                                            .update("precio_venta", nuevoPrecio)
                                            .addOnSuccessListener(aVoid -> {
                                                // Si Firebase dice OK, actualizamos tu lista en pantalla al instante
                                                productoActual.setPrecio_venta(nuevoPrecio);
                                                notifyItemChanged(position);
                                                Toast.makeText(v.getContext(), "Precio actualizado con éxito", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            });
                }
            });

            // 5. Configuramos el botón "Cancelar" por si te arrepentís
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

            // 6. Mostramos la ventanita terminada en pantalla
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class ProductoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvPrecio;
        Button btnEditar;

        public ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Enlazamos con los IDs de tu archivo item_mercaderia_admin.xml
            tvNombre = itemView.findViewById(R.id.tv_nombre_admin);
            tvPrecio = itemView.findViewById(R.id.tv_precio_admin);
            btnEditar = itemView.findViewById(R.id.btn_editar_precio);
        }
    }
}