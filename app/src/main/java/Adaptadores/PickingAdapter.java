package Adaptadores;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import frgp.utn.com.gestionsalvador.R;

public class PickingAdapter extends RecyclerView.Adapter<PickingAdapter.PickingViewHolder> {

    private List<String> listaTextos;
    private boolean[] marcados;
    private OnItemCheckListener listener;

    public interface OnItemCheckListener {
        void onCheckChanged(boolean estanTodosMarcados);
    }

    public PickingAdapter(List<String> listaTextos, boolean arrancarTildados, OnItemCheckListener listener) {
        this.listaTextos = listaTextos;
        this.listener = listener;
        this.marcados = new boolean[listaTextos.size()];

        if (arrancarTildados) {
            for (int i = 0; i < marcados.length; i++) {
                marcados[i] = true;
            }
        }
    }

    @NonNull
    @Override
    public PickingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_picking, parent, false);
        return new PickingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickingViewHolder holder, int position) {
        String textoProducto = listaTextos.get(position);
        boolean estaMarcado = marcados[position];

        holder.cbProducto.setText(textoProducto);

        // Desconectamos listener para evitar disparos al reciclar
        holder.cbProducto.setOnCheckedChangeListener(null);
        holder.cbProducto.setChecked(estaMarcado);
        aplicarEfectoTachado(holder.cbProducto, estaMarcado);

        holder.cbProducto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                marcados[pos] = isChecked;
                aplicarEfectoTachado(holder.cbProducto, isChecked);

                if (listener != null) {
                    listener.onCheckChanged(estanTodosMarcados());
                }
            }
        });
    }

    private void aplicarEfectoTachado(CheckBox cb, boolean tachar) {
        if (tachar) {
            cb.setPaintFlags(cb.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            cb.setAlpha(0.45f);
        } else {
            cb.setPaintFlags(cb.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            cb.setAlpha(1.0f);
        }
    }

    public boolean estanTodosMarcados() {
        for (boolean m : marcados) {
            if (!m) return false;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return listaTextos.size();
    }

    public static class PickingViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbProducto;

        public PickingViewHolder(@NonNull View itemView) {
            super(itemView);
            cbProducto = itemView.findViewById(R.id.cbProductoPicking);
        }
    }
}