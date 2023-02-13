package com.rafaelavieiravendas.RafaelaDicorpo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Item;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;
import com.rafaelavieiravendas.RafaelaDicorpo.model.PedidosLista;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class TelaPedidos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private View viewNavigationItens;
    private AlertDialog alerta_filtro;
    private ImageView filtro;
    private EditText buscar;
    private RecyclerView recycler_produtos;
    //private ImageView back_pedidos;
    private PedidoClienteAdapter pedidoClienteAdapter;
    private TextView pedidos_toolbar;
    private static ArrayList<Pedido> listaInfoCard;
    //static ArrayList ListaDeNomes =  new ArrayList();
    private ArrayList<Item> ListaDeItens;
    //private static ListaDeItens;
    private static ArrayList LISTAEMUSO;
    private FloatingActionButton criarPedido;
    private FloatingActionButton excluir;
    private FloatingActionButton finalizar;
    private TextView message_empty;

    String date = TelaPrincipal.getDataEscolhida();

    public static void setListaPedidos(ArrayList<Pedido> listaPedidos) {
        listaInfoCard = listaPedidos;
        LISTAEMUSO = listaInfoCard;
        //PedidosLista.setListaEmUso(LISTAEMUSO);

    }

    public void setAdapter(){
        pedidoClienteAdapter.setListener(new PedidoClienteAdapter.PedidoAdapterListener(){
            @Override
            public void OnItemClick(int position) {
                //pedidoClienteAdapter = new PedidoClienteAdapter(listaInfoCard);

                // recycler_produtos.setAdapter(pedidoClienteAdapter);
                setAdapter();
                TelaVisualizarPedido.setPedido((Pedido) LISTAEMUSO.get(position));
                BuscarItensDb((Pedido) LISTAEMUSO.get(position));
                TelaEditPedido.setPosition_pedido(position);
                //TelaVisualizarPedido.setPosition_pedido(position);
                //Intent intent = new Intent(TelaPedidos.this, TelaVisualizarPedido.class);
                //startActivity(intent);
            }
        });
    }

/*
    private static final ExecutorService threadpool = Executors.newFixedThreadPool(1);

    private static class GerarNumeroAleatorio implements Callable<Void> {
        @Override
        public Void call() throws Exception {
            ArrayList Lista = new ArrayList();
            String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String date = TelaPrincipal.getDataEscolhida().replace("/", ".");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Usuarios").document(usuarioID)
                    .collection("PedidoRevendedora/" + date + "/Pedidos")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String data = (String) document.getData().get("nome");
                                    Lista.add(data);
                                    Log.d("BuscarNomes", "CARREGOU");
                                }
                            } else {
                                Log.d("BuscarNomes", "Error getting documents: ", task.getException());
                            }
                        }
                    });
            ListaDeNomes = Lista;
            return null;
        }
    }

 */

    /*
    public static synchronized void Carregando() throws Exception {

        GerarNumeroAleatorio task = new GerarNumeroAleatorio();
        System.out.println("Processando a tarefa ...");
        Future<Void> future = threadpool.submit(task);
        while (!future.isDone()) {
            Log.d("aaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaa POHA");
        }
        System.out.println("Tarefa completa!");
        Void factorial = (Void) future.get();
        Log.d("aaaaaaaaaa", "aaaaaaaaaaaaaaaaaaaaaaaaaaa POHA" + factorial + ListaDeNomes);
        threadpool.shutdown();

    }

     */

    // ////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_pedidos);
        LISTAEMUSO = listaInfoCard;

        //TelaEditPedido.setListaDeNomes(listaInfoCard);

        //PedidosLista.setListaEmUso(LISTAEMUSO);
        IniciarComponentes();

        //setSupportActionBar(pedidos_toolbar);
        String date = TelaPrincipal.getDataEscolhida();
        pedidos_toolbar.setText(date);

        if(PedidosLista.GeradorPedidos().isEmpty()){
            message_empty.setText("Você ainda não registrou pedidos");
        }

        pedidoClienteAdapter = new PedidoClienteAdapter(new ArrayList<>(PedidosLista.GeradorPedidos()));
        recycler_produtos.setAdapter(pedidoClienteAdapter);


        /////////////////////////////////////////////

        AlertDialog.Builder builder = new AlertDialog.Builder(TelaPedidos.this, R.style.CustomAlertDialog);
        LayoutInflater li = getLayoutInflater();
        viewNavigationItens = li.inflate(R.layout.activity_filtro_menu, null);
        builder.setView(viewNavigationItens);
        alerta_filtro = builder.create();

        NavigationView navigationView = viewNavigationItens.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setAdapter();

        criarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaPedidos.this, TelaCriarPedidosClientes.class);
                startActivity(intent);
                finish();
            }
        });

        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filtrar(s.toString());

            }
        });

        filtro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alerta_filtro.show();

            }
        });

        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alerta;

                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(TelaPedidos.this);
                builder.setTitle("Deseja mesmo excluir esta demanda? ");
                builder.setMessage("Todos os registros dentro desta data serão perdidos.");
                builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar snackbar = Snackbar.make(v, "Pedidos excluidos", Snackbar.LENGTH_LONG);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();

                        excluirPedido();
                    }
                });
                builder.setNegativeButton("não", null);
                alerta = builder.create();
                alerta.show();
            }
        });

        finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alerta;

                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(TelaPedidos.this);
                builder.setTitle("Deseja contabilizar esta demanda? ");
                builder.setMessage("Todos os registros dentro desta data somados e contabilizados.");
                builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar snackbar = Snackbar.make(v, "Demanda concluída, salvando informações...", Snackbar.LENGTH_LONG);
                        snackbar.setBackgroundTint(Color.WHITE);
                        snackbar.setTextColor(Color.BLACK);
                        snackbar.show();

                        ContabilizarDemanda();

                    }
                });
                builder.setNegativeButton("não", null);
                alerta = builder.create();
                alerta.show();

            }
        });

    }

    public void ContabilizarDemanda(){

        //DecimalFormat df = new DecimalFormat(".##");
        //df.setRoundingMode(RoundingMode.UP);

        int soma = 0;
        String somaResult;

        for(int i = 0; i < listaInfoCard.size(); i ++){
            String somar = listaInfoCard.get(i).getValor().replace(",", "")
                    .replace(".", "");
            if(somar != ""){
                soma = soma + Integer.parseInt(somar);
            }
        }

        TelaCriarPedidosClientes telaMask = new TelaCriarPedidosClientes();
        somaResult = telaMask.MascaraPreco(String.valueOf(soma));

        /*
        //String dataFinal = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance());
        String dataFinal = "";

        DateFormat formatador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
        try {
            Date date = formatador.parse(String.valueOf(Calendar.getInstance()));
            SimpleDateFormat dfs = new SimpleDateFormat("dd/MM/yyyy");
            String data = dfs.format(date);
            dataFinal = data;

        } catch (ParseException e) {
            e.printStackTrace();
        }

         */

        Calendar calendar = Calendar.getInstance();//cria o obj calendar e atribui a hora e data do sistema
        Date data = calendar.getTime();//transforma o obj calendar em obj Date

        SimpleDateFormat sddia = new SimpleDateFormat("dd-MM-yyyy");//cria um obj de formatação de data
        String dataFinal = sddia.format(data);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("DataCriacao", date);
        usuarios.put("Total", somaResult);
        usuarios.put("Encerrado", dataFinal.replace("-", "/"));


        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID).collection("Faturados").document(date.replace("/", "."));
        documentReference.set(usuarios).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                excluirPedido();
            }
        });
    }

    public void excluirPedido(){
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        date = date.replace("/", ".");

        db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora").document(date)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                //adicionar as infos necessárias para iniciar a TelaVisualizarPedido
                            }
                        }, 1500);
                    }
                });
    }

    public void filtrar(String texto){

        ArrayList filtrados = new ArrayList();
        for (Pedido pedido : listaInfoCard){
            if (pedido.getNome().contains(texto)){
                filtro.setImageResource(R.drawable.no_filter);
                filtrados.add(pedido);
            }
        }
        pedidoClienteAdapter = new PedidoClienteAdapter(new ArrayList<>(filtrados));
        recycler_produtos.setAdapter(pedidoClienteAdapter);
        LISTAEMUSO = filtrados;
        //PedidosLista.setListaEmUso(LISTAEMUSO);

        setAdapter();
    }

    public void filtrarStatus(String texto){
        ArrayList filtrados = new ArrayList();
        for (Pedido pedido : listaInfoCard){
            if (pedido.getStatus().contains(texto)){
                filtrados.add(pedido);
            }
        }
        pedidoClienteAdapter = new PedidoClienteAdapter(new ArrayList<>(filtrados));
        recycler_produtos.setAdapter(pedidoClienteAdapter);
        LISTAEMUSO = filtrados;
        //PedidosLista.setListaEmUso(LISTAEMUSO);

        setAdapter();
    }


    protected void onStart() {
        super.onStart();
        //TelaCriarPedidosClientes.Buscar_Nomes_Pedidos();
    }

    private void BuscarItensDb(Pedido pedido){

        ListaDeItens = new ArrayList<Item>();

        Intent intent = new Intent(TelaPedidos.this, TelaVisualizarPedido.class);

        AlertDialog.Builder builder2 = new AlertDialog.Builder(TelaPedidos.this, R.style.CustomAlertDialog);
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.loading_dialog, null);
        builder2.setView(view);
        AlertDialog alert = builder2.create();
        alert.show();

        String UsuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").document(UsuarioID)
                .collection("PedidoRevendedora/" + date.replace("/", ".") + "/Pedidos/" + pedido.getNome() + "/Itens")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map path = document.getData();
                                Item data = Item.ItemBuilder.builder()
                                        .setCor((String) path.get("Cor"))
                                        .setId((String) path.get("Num/id"))
                                        .setQuantidade(String.valueOf(path.get("Quantidade")))
                                        .setPreco((String) path.get("Preco"))
                                        .setProduto((String) path.get("Produto"))
                                        .setTamanho((String) path.get("Tamanho"))
                                        .build();

                                ListaDeItens.add(data);
                                Log.d("BuscarNomes", "CARREGOU");
                            }

                            TelaVisualizarPedido.setListaDeItens(ListaDeItens);

                            alert.dismiss();
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d("BuscarNomes", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void IniciarComponentes(){
        excluir = findViewById(R.id.excluir);
        //back_pedidos = findViewById(R.id.back_pedidos);
        recycler_produtos = findViewById(R.id.recycle_view);
        pedidos_toolbar = findViewById(R.id.title_bar);
        criarPedido = findViewById(R.id.add_btn);
        buscar = findViewById(R.id.buscar);
        filtro = findViewById(R.id.filter);
        finalizar = findViewById(R.id.finalizar);
        message_empty = findViewById(R.id.message_empty);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_pagos:
                filtrarStatus("Pago");
                alerta_filtro.dismiss();
                filtro.setImageResource(R.drawable.filter_icon);
                break;

            case R.id.menu_pendentes:
                filtrarStatus("Pendente");
                alerta_filtro.dismiss();
                filtro.setImageResource(R.drawable.filter_icon);
                break;

            case R.id.menu_nenhum:
                pedidoClienteAdapter = new PedidoClienteAdapter(listaInfoCard);
                recycler_produtos.setAdapter(pedidoClienteAdapter);
                LISTAEMUSO = listaInfoCard;
                //PedidosLista.setListaEmUso(LISTAEMUSO);
                filtro.setImageResource(R.drawable.no_filter);
                alerta_filtro.dismiss();

                setAdapter();
                break;

        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}