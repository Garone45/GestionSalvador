package Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Entidad.Cliente;
import frgp.utn.com.gestionsalvador.clientes.DetalleClienteActivity;
import frgp.utn.com.gestionsalvador.R;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private List<Cliente> listaClientes;

    public ClienteAdapter(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        Cliente cliente = listaClientes.get(position);

        // Unimos nombre y apellido para mostrar
        String nombreCompleto = cliente.getNombre() + " " + cliente.getApellido();
        holder.tvNombre.setText(nombreCompleto);

        // Unimos calle y altura
        String direccionCompleta = cliente.getCalle() + " " + cliente.getAltura();
        holder.tvDireccion.setText(direccionCompleta);

        holder.tvLocalidad.setText(cliente.getLocalidad());
        holder.tvTelefono.setText(cliente.getTelefono());

        // ¡ACÁ ESTÁ LA MAGIA! Agregamos el clic para ver el detalle
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, DetalleClienteActivity.class);

            // Mandamos los datos concatenados igual que como los mostramos en la tarjeta
            intent.putExtra("cliente_nombre", nombreCompleto);
            intent.putExtra("cliente_telefono", cliente.getTelefono());
            intent.putExtra("cliente_direccion", direccionCompleta + ", " + cliente.getLocalidad());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvDireccion, tvLocalidad, tvTelefono;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvItemNombre);
            tvDireccion = itemView.findViewById(R.id.tvItemDireccion);
            tvLocalidad = itemView.findViewById(R.id.tvItemLocalidad);
            tvTelefono = itemView.findViewById(R.id.tvItemTelefono);
        }
    }
}