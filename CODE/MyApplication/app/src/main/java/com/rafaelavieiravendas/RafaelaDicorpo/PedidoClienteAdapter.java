package com.rafaelavieiravendas.RafaelaDicorpo;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class PedidoClienteAdapter extends RecyclerView.Adapter<PedidoClienteAdapter.PedidoViewHolder> {

    private PedidoAdapterListener listener;
    private final ArrayList<Pedido> pedido1;
    //ArrayList ListaDeNomes = new ArrayList();

    public void setListener(PedidoAdapterListener listener){
        this.listener = listener;
    }

    public PedidoClienteAdapter(ArrayList<Pedido> pedido) {
        this.pedido1 = pedido;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido_item, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {

        Pedido pedidos = pedido1.get(holder.getAdapterPosition());
        holder.bind(pedidos);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedido1.size();
    }

    class PedidoViewHolder extends RecyclerView.ViewHolder{

        private TextView nome;
        private TextView data;
        private TextView valor;
        private TextView status;

        public PedidoViewHolder(@NonNull View ItemView){
            super(ItemView);
            nome = ItemView.findViewById(R.id.nome_view);
            data = ItemView.findViewById(R.id.data_view);
            valor = ItemView.findViewById(R.id.valor_view);
            status = ItemView.findViewById(R.id.status_view);
        }

        public void bind(Pedido pedidos) {
            nome.setText(pedidos.getNome());

            data.setText(pedidos.getData());

            //Float value = Float.parseFloat(pedidos.getValor().replace(",", "."));

            valor.setText("R$ " + pedidos.getValor());

            final String statusValue = pedidos.getStatus();

            switch (statusValue) {
                case "Pago":
                    status.setText("Status : " + pedidos.getStatus() + " ");
                    status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done_icon, 0);
                    break;
                case "Pendente":
                    status.setText("Status : " + pedidos.getStatus() + " ");
                    status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pending_icon, 0);
                    break;
            }
        }
    }

    interface PedidoAdapterListener {
        void OnItemClick(int position);
    }
}
