package com.rafaelavieiravendas.RafaelaDicorpo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rafaelavieiravendas.RafaelaDicorpo.model.Item;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;

import java.util.ArrayList;

public class PedidoItensAdapter extends RecyclerView.Adapter<PedidoItensAdapter.PedidoViewHolder>{

    //private ItemAdapterListener listener;
    private final ArrayList<Item> pedido1;

    //public void setListener(ItemAdapterListener listener){
        //this.listener = listener;
    //}

    public PedidoItensAdapter(ArrayList<Item> item) {
        this.pedido1 = item;
    }

    @Override
    public PedidoItensAdapter.PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pecas_item, parent, false);
        return new PedidoItensAdapter.PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {

        Item item = pedido1.get(holder.getAdapterPosition());
        holder.bind(item);

       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //listener.OnItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedido1.size();
    }

    class PedidoViewHolder extends RecyclerView.ViewHolder{

        private TextView id;
        private TextView cor;
        private TextView quantidade;
        private TextView preco;
        private TextView tamanho;

        public PedidoViewHolder(@NonNull View ItemView){
            super(ItemView);
            id = ItemView.findViewById(R.id.nome_view);
            cor = ItemView.findViewById(R.id.cor_view);
            preco = ItemView.findViewById(R.id.valor_view);
            quantidade = ItemView.findViewById(R.id.unidades_view);
            tamanho = ItemView.findViewById(R.id.tamanho_view);
        }

        public void bind(Item item) {

            id.setText(item.getProduto() + " - " + item.getId());
            cor.setText("Cor: " + item.getCor());
            preco.setText(item.getPreco());
            quantidade.setText("Unid: " + item.getQuantidade());
            tamanho.setText("Tamanho: " + item.getTamanho());
        }
    }

    interface ItemAdapterListener {
        void OnItemClick(int position);
    }
}


