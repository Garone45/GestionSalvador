package Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import Entidad.Producto;
import frgp.utn.com.gestionsalvador.R;

public class ResumenAdapter extends RecyclerView.Adapter<ResumenAdapter.ResumenViewHolder> {

    private List<Producto> listaCarrito;

    public ResumenAdapter(List<Producto> listaCarrito) {
        this.listaCarrito = listaCarrito;
    }

    @NonNull
    @Override
    public ResumenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resumen, parent, false);
        return new ResumenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResumenViewHolder holder, int position) {
        Producto producto = listaCarrito.get(position);

        holder.tvCantidad.setText(producto.getCantidad() + "x");
        holder.tvNombre.setText(producto.getNombre());

        // Calculamos el subtotal de ese renglón (Precio x Cantidad)
        double subtotal = producto.getPrecio_venta() * producto.getCantidad();
        holder.tvSubtotal.setText("$" + subtotal);
    }

    @Override
    public int getItemCount() {
        return listaCarrito.size();
    }

    public static class ResumenViewHolder extends RecyclerView.ViewHolder {
        TextView tvCantidad, tvNombre, tvSubtotal;

        public ResumenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCantidad = itemView.findViewById(R.id.tv_item_resumen_cantidad);
            tvNombre = itemView.findViewById(R.id.tv_item_resumen_nombre);
            tvSubtotal = itemView.findViewById(R.id.tv_item_resumen_subtotal);
        }
    }
}
