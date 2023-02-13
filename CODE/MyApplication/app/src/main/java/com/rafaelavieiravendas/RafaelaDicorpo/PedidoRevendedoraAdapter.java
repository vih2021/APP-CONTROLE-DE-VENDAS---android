package com.rafaelavieiravendas.RafaelaDicorpo;

import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


class PedidoRevendedoraAdapter extends RecyclerView.Adapter<PedidoRevendedoraAdapter.PedidoViewHolder> {

    private PedidoAdapterListener listener;
    private final ArrayList pedido1;


    public ArrayList getPedidos(){
        return pedido1;
    }

    public void setListener(PedidoAdapterListener listener){
        this.listener = listener;
    }

    public PedidoRevendedoraAdapter(ArrayList pedido) {
        this.pedido1 = pedido;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alerta, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        String pedidos = (String) pedido1.get(holder.getAdapterPosition());
        holder.bind(pedidos);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(holder.getAdapterPosition());
                //TelaPrincipal.setDataEscolhida((String) pedido1.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedido1.size();
    }

    class PedidoViewHolder extends RecyclerView.ViewHolder{

        private TextView dataPedido;

        public PedidoViewHolder(@NonNull View ItemView){
            super(ItemView);
            dataPedido = ItemView.findViewById(R.id.valor_view);
        }

        public void bind(String pedidos) {

            dataPedido.setText(pedidos);
        }
    }

    interface PedidoAdapterListener {
        void OnItemClick(int position);
    }

    interface PedidoRevendedoraAdapterListener {
        void OnItemClick(int position, String data);
    }
}
