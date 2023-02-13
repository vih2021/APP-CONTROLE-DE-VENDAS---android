package com.rafaelavieiravendas.RafaelaDicorpo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rafaelavieiravendas.RafaelaDicorpo.model.Faturados;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;

import java.util.ArrayList;

class FaturasAdapter extends RecyclerView.Adapter<FaturasAdapter.FaturaViewHolder> {

    private FaturasAdapter.FaturaAdapterListener listener;
    private final ArrayList<Faturados> pedido1;

    public void setListener(FaturaAdapterListener faturaAdapterListener) {
        this.listener = faturaAdapterListener;
    }

    public FaturasAdapter(ArrayList<Faturados> fatura) {
        this.pedido1 = fatura;
    }

    @NonNull
    @Override
    public FaturaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_financeiro, parent, false);
        return new FaturaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaturaViewHolder holder, int position) {

        Faturados pedidos = pedido1.get(holder.getAdapterPosition());
        holder.bind(pedidos);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(holder.getAdapterPosition(), holder.container);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedido1.size();
    }

    class FaturaViewHolder extends RecyclerView.ViewHolder {

        private TextView DataEncerrado;
        private TextView DataInicio;
        private TextView Valor;
        private RelativeLayout container;

        public FaturaViewHolder(@NonNull View ItemView) {
            super(ItemView);
            DataInicio = ItemView.findViewById(R.id.dataInicio_view);
            DataEncerrado = ItemView.findViewById(R.id.dataFim_view);
            Valor = ItemView.findViewById(R.id.preco);
            container = ItemView.findViewById(R.id.container_pedidos);

        }

        public void bind(Faturados fatura) {
            //container.setBackgroundColor(Color.BLACK);
            DataInicio.setText(fatura.getDataInicio());
            DataEncerrado.setText(fatura.getDataFim());
            Valor.setText("R$ " + fatura.getValor());

        }
    }

    interface FaturaAdapterListener {
        void OnItemClick(int position, View item);
    }
}


