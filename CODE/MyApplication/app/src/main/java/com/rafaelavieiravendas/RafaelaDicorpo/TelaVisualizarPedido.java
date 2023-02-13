package com.rafaelavieiravendas.RafaelaDicorpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Item;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;
import com.rafaelavieiravendas.RafaelaDicorpo.model.PedidosLista;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelaVisualizarPedido extends AppCompatActivity {

    static Pedido lista;
    private RecyclerView recycler_produtos;
    private TextView nome;
    private TextView data;
    private TextView pecas;
    private TextView total;
    private TextView status;
    private Button editar;
    private Button contabilizar;
    private Button excluir;
    private static ArrayList ListaDeItens;
    String date = TelaPrincipal.getDataEscolhida();
    private static int Position_pedido;
    private ImageView back;
    private String usuarioID;

    public static void UpdateListaDeItens(int id, Object element){
        ListaDeItens.set(id, element);
    }

    public static void setPosition_pedido(int posicao){
        Position_pedido = posicao;
    }

    public static void setListaDeItens(ArrayList<Item> lista){
        ListaDeItens = lista;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_visualizar_pedido);
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        InicarComponentes();

        //seta o adapter dos itens na telavisualizarpedido
        PedidoItensAdapter pedidoClienteAdapter = new PedidoItensAdapter(new ArrayList<Item>(ListaDeItens));
        recycler_produtos.setAdapter(pedidoClienteAdapter);

        nome.setText(" Cliente:    " + lista.getNome());
        data.setText("  Data:        " + lista.getData());
        pecas.setText("Peças:      " + lista.getQuantidade());
        total.setText(" Total:        " + lista.getValor());

        status.setText(lista.getStatus());
        if(lista.getStatus().contains("Pendente")){
            status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pending_icon, 0);
        }else{
            status.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done_icon, 0);
        }

        // Desabilita os botões caso o pedido ja tenha sido pago
        verificaStatus();

        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alerta;

                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(TelaVisualizarPedido.this);
                builder.setTitle("Deseja mesmo excluir esse pedido? ");
                builder.setMessage("Quando você encerrar esta demanda de pedidos, este não será contabilizado na soma.");
                builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar snackbar = Snackbar.make(v, "Pedido excluido", Snackbar.LENGTH_LONG);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();

                        excluirPedido();;
                    }
                });
                builder.setNegativeButton("não", null);
                alerta = builder.create();
                alerta.show();
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finishActivity(R.layout.activity_pedidos_clientes);
                TelaEditPedido.setNomeExistente(lista.getNome());
                TelaEditPedido.setDataExistente(lista.getData());
                TelaEditPedido.setItensExistentes(ListaDeItens);
                Intent editar = new Intent(TelaVisualizarPedido.this, TelaEditPedido.class);
                startActivity(editar);
                finish();
            }
        });

        contabilizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alerta;

                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(TelaVisualizarPedido.this);
                builder.setTitle("Deseja alterar o status para PAGO? ");
                builder.setMessage("Você não poderá realizar alterações nesse pedido após a confirmação.");
                builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alterarStatus(v);
                    }
                });
                builder.setNegativeButton("não", null);
                alerta = builder.create();
                alerta.show();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent TelaPedidos = new Intent(TelaVisualizarPedido.this, TelaPedidos.class);
                startActivity(TelaPedidos);
            }
        });
    }

    public void alterarStatus(View v){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference docRef = db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora").document(date.replace("/", ".")).collection("Pedidos")
                .document(lista.getNome());

        Map<String,Object> updates = new HashMap<>();
        updates.put("status", "Pago");

        docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Snackbar snackbar = Snackbar.make(v, "Pedido contabilizado com Sucesso!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            }
        });

        RealizarConsultaDb();
    }

    public void excluirPedido(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        date = date.replace("/", ".");

        db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora").document(date).collection("Pedidos")
                .document(lista.getNome()).collection("Itens").document()
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        db.collection("Usuarios").document(usuarioID)
                                .collection("PedidoRevendedora").document(date).collection("Pedidos")
                                .document(lista.getNome())
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        RealizarConsultaDb();
                                    }
                                });
                    }
                });
    }

    public void RealizarConsultaDb(){
        ArrayList<Pedido> ListaPedidosAtualizada = new ArrayList<Pedido>();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora/" + date.replace("/", ".") + "/Pedidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map path = document.getData();
                                Pedido data = Pedido.PedidoBuilder.builder()
                                        .setNome((String) path.get("nome"))
                                        .setData((String) path.get("data"))
                                        .setValor((String) path.get("valor"))
                                        .setQuantidade((String) path.get("quantidade"))
                                        .setStatus((String) path.get("status"))
                                        .build();

                                ListaPedidosAtualizada.add(data);
                                Log.d("BuscarNomes", "CARREGOU");
                            }

                            //seta os pedidos recebidos pelo banco para que apareçam na Tela de pedidos
                            PedidosLista.setListaPedidos(ListaPedidosAtualizada);
                            TelaPedidos.setListaPedidos(ListaPedidosAtualizada);
                            TelaCriarPedidosClientes.setListaDeNomes(ListaPedidosAtualizada);
                            //TelaEditPedido.setListaDeNomes(ListaPedidosAtualizada);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    //adicionar as infos necessárias para iniciar a TelaVisualizarPedido
                                    Intent intent = new Intent(TelaVisualizarPedido.this, TelaPedidos.class);
                                    startActivity(intent);
                                }
                            }, 1500);

                        } else {
                            Log.d("BuscarNomes", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public static void setPedido(Pedido listapedidos){
        lista = listapedidos;
    }

    public void verificaStatus(){
        if(lista.getStatus().contains("Pago")){
            contabilizar.setEnabled(false);
            editar.setEnabled(false
            );
        }
    }

    public void InicarComponentes(){
        recycler_produtos = findViewById(R.id.recycle_view);
        nome = findViewById(R.id.Nome);
        data = findViewById(R.id.Data);
        pecas = findViewById(R.id.Pecas);
        total = findViewById(R.id.Total);
        status = findViewById(R.id.status_view);
        editar = findViewById(R.id.editar);
        contabilizar = findViewById(R.id.contabilizar);
        back = findViewById(R.id.back_cadastro);
        excluir = findViewById(R.id.excluir);
    }


}