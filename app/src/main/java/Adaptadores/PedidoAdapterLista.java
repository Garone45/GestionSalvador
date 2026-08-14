package Adaptadores;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Entidad.Pedido;
import frgp.utn.com.gestionsalvador.R;

public class PedidoAdapterLista extends RecyclerView.Adapter<PedidoAdapterLista.PedidoViewHolder> {

    private List<Pedido> listaPedidos;

    public PedidoAdapterLista(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        Pedido pedido = listaPedidos.get(position);

        holder.tvNombre.setText(pedido.getNombreCliente());
        holder.tvDireccion.setText(pedido.getDireccionCliente());
        holder.tvFecha.setText(pedido.getFecha());
        holder.tvTotal.setText("$ " + pedido.getTotal());
        holder.tvEstado.setText(pedido.getEstado());

        // 1. CAMBIO DE COLOR SEGÚN EL ESTADO
        if (pedido.getEstado() != null && pedido.getEstado().equalsIgnoreCase("Entregado")) {
            // Si ya está entregado, le ponemos un fondo verde clarito suave
            holder.itemView.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            // Si está pendiente, queda con su color blanco normal
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // 2. CLIC EN LA TARJETA PARA ABRIR EL DETALLE
        holder.itemView.setOnClickListener(v -> {
            android.content.Context context = v.getContext();
            android.content.Intent intent = new android.content.Intent(context, frgp.utn.com.gestionsalvador.pedidos.DetallePedidoActivity.class);

            // ¡IMPORTANTE! Pasamos el ID del documento para que el botón de entregado de la otra pantalla funcione
            intent.putExtra("pedido_id", pedido.getId());

            // Recorremos la lista de productos del pedido para armar el texto
            StringBuilder productosTexto = new StringBuilder();
            if (pedido.getProductos() != null && !pedido.getProductos().isEmpty()) {
                for (Entidad.Producto p : pedido.getProductos()) {
                    productosTexto.append("- ").append(p.getNombre())
                            .append(" ($").append(p.getPrecio_venta()).append(")\n");
                }
            } else {
                productosTexto.append("No hay productos detallados en este pedido.");
            }

            // Mandamos todos los datos a la pantalla de detalle
            intent.putExtra("nombre_cliente", pedido.getNombreCliente());
            intent.putExtra("direccion_cliente", pedido.getDireccionCliente());
            intent.putExtra("telefono_cliente", pedido.getTelefonoCliente());
            intent.putExtra("productos_detalle", productosTexto.toString());
            intent.putExtra("observaciones_cliente", pedido.getObservaciones());
            intent.putExtra("total_pedido", pedido.getTotal());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvFecha, tvTotal, tvEstado;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_item_pedido_nombre);
            tvDireccion = itemView.findViewById(R.id.tv_item_pedido_direccion);
            tvFecha = itemView.findViewById(R.id.tv_item_pedido_fecha);
            tvTotal = itemView.findViewById(R.id.tv_item_pedido_total);
            tvEstado = itemView.findViewById(R.id.tv_item_pedido_estado);
        }
    }
}