package Adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import frgp.utn.com.gestionsalvador.R;

import java.util.List;

public class PickingAdapter extends RecyclerView.Adapter<PickingAdapter.PickingViewHolder> {

    private List<String> listaProductos;

    public PickingAdapter(List<String> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public PickingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picking, parent, false);
        return new PickingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickingViewHolder holder, int position) {
        String productoTexto = listaProductos.get(position);

        // Seteamos el texto del producto con la cantidad para Javi
        holder.cbProducto.setText(productoTexto);

        // Limpiamos el listener previo para evitar bugs de reciclaje de vistas en Android
        holder.cbProducto.setOnCheckedChangeListener(null);

        // Acá podés agregar lógica si querés que al tildar cambie de color o se tache
        holder.cbProducto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Opcional: registrar que este producto ya fue metido al cajón
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    public static class PickingViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbProducto;

        public PickingViewHolder(@NonNull View itemView) {
            super(itemView);
            cbProducto = itemView.findViewById(R.id.cbProductoPicking);
        }
    }
}