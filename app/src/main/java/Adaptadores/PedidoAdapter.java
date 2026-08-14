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

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Producto> listaProductos;
    private OnCarritoChangeListener listener; // Nuestro "walkie-talkie"

    // 1. Modificamos la interfaz: cantidadTotal ahora es double para soportar fracciones
    public interface OnCarritoChangeListener {
        void onCarritoCambiado(double cantidadTotal, double precioTotal);
    }

    // 2. Constructor
    public PedidoAdapter(List<Producto> listaProductos, OnCarritoChangeListener listener) {
        this.listaProductos = listaProductos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalogo, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Producto productoActual = listaProductos.get(position);

        holder.tvNombre.setText(productoActual.getNombre());
        holder.tvPrecio.setText("$ " + productoActual.getPrecio_venta());

        // Mostramos la cantidad con nuestro formateador (para evitar el feo "1.0")
        holder.tvCantidad.setText(formatearNumero(productoActual.getCantidad()));

        // Botón SUMAR: Ahora suma 0.5
        holder.btnSumar.setOnClickListener(v -> {
            productoActual.setCantidad(productoActual.getCantidad() + 0.5);
            holder.tvCantidad.setText(formatearNumero(productoActual.getCantidad()));
            notificarCambio(); // Avisamos a la pantalla inferior
        });

        // Botón RESTAR: Ahora resta 0.5
        holder.btnRestar.setOnClickListener(v -> {
            if (productoActual.getCantidad() > 0) {
                productoActual.setCantidad(productoActual.getCantidad() - 0.5);
                holder.tvCantidad.setText(formatearNumero(productoActual.getCantidad()));
                notificarCambio(); // Avisamos a la pantalla inferior
            }
        });
    }

    // 3. Modificamos la suma para que cuente decimales
    private void notificarCambio() {
        double totalItems = 0.0; // Cambiado a double
        double totalDinero = 0.0;

        for (Producto p : listaProductos) {
            totalItems += p.getCantidad();
            totalDinero += (p.getCantidad() * p.getPrecio_venta());
        }

        if (listener != null) {
            listener.onCarritoCambiado(totalItems, totalDinero);
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    // Función auxiliar para que se vea "1" en lugar de "1.0", pero "0.5" quede igual
    private String formatearNumero(double numero) {
        if (numero == (long) numero) {
            return String.format("%d", (long) numero);
        } else {
            return String.valueOf(numero);
        }
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio, tvCantidad, btnSumar, btnRestar;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre_producto);
            tvPrecio = itemView.findViewById(R.id.tv_precio_producto);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad);
            btnSumar = itemView.findViewById(R.id.btn_sumar);
            btnRestar = itemView.findViewById(R.id.btn_restar);
        }
    }
}