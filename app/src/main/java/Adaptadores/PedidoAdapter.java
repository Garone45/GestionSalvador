package Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import Entidad.Producto;
import frgp.utn.com.gestionsalvador.R;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private List<Producto> listaProductos;
    private OnCarritoChangeListener listener;

    public interface OnCarritoChangeListener {
        void onCarritoCambiado(double cantidadTotal, double precioTotal);
    }

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

        // Mostramos el precio con su unidad de referencia (ej: $ 1200 / kg o $ 800 / paq.)
        String sufijoPrecio = obtenerSufijoPrecio(productoActual);
        holder.tvPrecio.setText("$ " + productoActual.getPrecio_venta() + sufijoPrecio);

        // Mostramos la cantidad con el formato correspondiente
        holder.tvCantidad.setText(formatearCantidadVisual(productoActual));

        // Determinamos el paso de suma/resta según el tipo de venta
        double paso = obtenerPaso(productoActual);

        // Botón SUMAR
        holder.btnSumar.setOnClickListener(v -> {
            productoActual.setCantidad(productoActual.getCantidad() + paso);
            holder.tvCantidad.setText(formatearCantidadVisual(productoActual));
            notificarCambio();
        });

        // Botón RESTAR
        holder.btnRestar.setOnClickListener(v -> {
            if (productoActual.getCantidad() >= paso) {
                // Redondeo de seguridad para evitar errores de precisión en double (ej: 0.5000000001)
                double nuevaCant = Math.round((productoActual.getCantidad() - paso) * 10.0) / 10.0;
                productoActual.setCantidad(nuevaCant);
                holder.tvCantidad.setText(formatearCantidadVisual(productoActual));
                notificarCambio();
            }
        });
    }

    // Calcula si suma de a 0.5 o de a 1.0
    private double obtenerPaso(Producto p) {
        if (p.getCategoria() != null && p.getCategoria().equalsIgnoreCase("Combos")) {
            return 1.0;
        }

        if (p.getTipo_venta() != null) {
            String tipo = p.getTipo_venta().trim().toUpperCase();
            if (tipo.equals("KG") || tipo.equals("KILO") || tipo.equals("KILOS")) {
                return 0.5; // Solo los kilos suben de a medio
            }
        }
        return 1.0; // PAQUETE, UNIDAD o combos van de a 1 entero
    }

    // Muestra "0.5 kg", "1 kg", "2 paq." o "1 un."
    private String formatearCantidadVisual(Producto p) {
        double cant = p.getCantidad();
        String tipo = p.getTipo_venta() != null ? p.getTipo_venta().trim().toUpperCase() : "";

        if (tipo.equals("KG") || tipo.equals("KILO") || tipo.equals("KILOS")) {
            if (cant == (long) cant) {
                return String.format(Locale.US, "%d kg", (long) cant);
            } else {
                return String.format(Locale.US, "%.1f kg", cant);
            }
        } else if (tipo.equals("PAQUETE") || tipo.equals("PAQ")) {
            return String.format(Locale.US, "%d paq.", (long) cant);
        } else if (tipo.equals("UNIDAD") || tipo.equals("UN")) {
            return String.format(Locale.US, "%d un.", (long) cant);
        } else {
            // Por defecto si no tiene tipo asignado o es combo
            if (cant == (long) cant) {
                return String.format(Locale.US, "%d", (long) cant);
            } else {
                return String.format(Locale.US, "%.1f", cant);
            }
        }
    }

    // Sufijo para el TextView del precio
    private String obtenerSufijoPrecio(Producto p) {
        if (p.getTipo_venta() == null) return "";
        String tipo = p.getTipo_venta().trim().toUpperCase();
        switch (tipo) {
            case "KG":
            case "KILO":
            case "KILOS":
                return " / kg";
            case "PAQUETE":
            case "PAQ":
                return " / paq.";
            case "UNIDAD":
            case "UN":
                return " / un.";
            default:
                return "";
        }
    }

    private void notificarCambio() {
        double totalItems = 0.0;
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