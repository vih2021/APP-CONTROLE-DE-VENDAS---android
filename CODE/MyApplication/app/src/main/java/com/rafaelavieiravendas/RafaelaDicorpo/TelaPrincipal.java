package com.rafaelavieiravendas.RafaelaDicorpo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Faturados;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;
import com.rafaelavieiravendas.RafaelaDicorpo.model.PedidosLista;

import java.util.ArrayList;
import java.util.Map;


public class TelaPrincipal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "Firestore";
    private static String usuarioID;

    //String Teste;
    public static boolean PedidoCriado = false;
    private RecyclerView recycler_produtos;
    //private static RecyclerView recycler_produtos2;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String UsuarioID;
    AlertDialog alerta = null;
    private PedidoRevendedoraAdapter pedidoAdapter;
    private static String dataEscolhida;
    //private static PedidoRevendedoraAdapter pedidoAdapter2;
    ArrayList<Pedido> ListaPedidos; //lista de pedidos dos clientes Tela Pedidos
    static ArrayList listagem; //lista de data dos pedidos dos clientes Tela Principal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        IniciarComponentes();
        setSupportActionBar(toolbar);

        //IniciarRecycle();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        //pedido = new PedidosRevendedoraLista();
        //pedido.RealizarConsultaDb();

        UsuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("Usuarios").document(UsuarioID).collection("Identidade").document("Usuario");;
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    toolbar.setTitle("Bem vindo(a)");
                    toolbar.setSubtitle(documentSnapshot.getString("nome"));
                }
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Intent intent2 = new Intent(TelaPrincipal.this, NotFound.class);

        switch (item.getItemId()){
            case R.id.menu_pedido:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(TelaPrincipal.this, R.style.CustomAlertDialog);
                LayoutInflater li = getLayoutInflater();
                View view = li.inflate(R.layout.loading_dialog, null);
                builder2.setView(view);
                AlertDialog alert = builder2.create();
                alert.show();

                RealizarConsultaDb_pedidos(alert);
                //Dialog_pedido();
                //IniciarRecycle();
                //Intent intent = new Intent(TelaPrincipal.this, TelaPedidos.class);
                //startActivity(intent);
                break;

            case R.id.menu_devolucao:

                startActivity(intent2);
                break;

            case R.id.menu_financeiro:

                AlertDialog.Builder builder3 = new AlertDialog.Builder(TelaPrincipal.this, R.style.CustomAlertDialog);
                LayoutInflater li2 = getLayoutInflater();
                View view2= li2.inflate(R.layout.loading_dialog, null);
                builder3.setView(view2);
                AlertDialog alert2 = builder3.create();
                alert2.show();

                RealizarConsultaDb_faturados(alert2);
                break;

            case R.id.menu_historico:

                startActivity(intent2);
                break;

            case R.id.menu_cliente:

                startActivity(intent2);
                break;

            case R.id.sair:
                FirebaseAuth.getInstance().signOut();
                Intent intent6 = new Intent(TelaPrincipal.this, FormLogin.class);
                startActivity(intent6);
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private void Dialog_pedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TelaPrincipal.this, R.style.CustomAlertDialog);
        //android.R.style.Theme_Translucent_NoTitleBar     deixa o dialog transparente

        LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.item_alerta_test, null);

        recycler_produtos = view.findViewById(R.id.recycle);
        //PedidosRevendedora pedido = new PedidosRevendedora();
        pedidoAdapter = new PedidoRevendedoraAdapter(new ArrayList(listagem));
        ArrayList lista = listagem;
        TelaCriarPedidoRevendedora.setListaPedidos(lista);
        recycler_produtos.setAdapter(pedidoAdapter);

        pedidoAdapter.setListener(new PedidoRevendedoraAdapter.PedidoAdapterListener(){
            @Override
            public void OnItemClick(int position) {
                //iniciando uma nova lista
                ListaPedidos = new ArrayList<Pedido>();

                Intent intent = new Intent(TelaPrincipal.this, TelaPedidos.class);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(TelaPrincipal.this, R.style.CustomAlertDialog);
                LayoutInflater li = getLayoutInflater();
                View view = li.inflate(R.layout.loading_dialog, null);
                builder2.setView(view);
                AlertDialog alert = builder2.create();

                String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String date = (String) lista.get(position) ;

                //seta a data escolhida na Tela de pedidos e na tela de criação de pedidos
                TelaCriarPedidosClientes.setDate(date);
                TelaEditPedido.setDate(date);
                setDataEscolhida(date);

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

                                        ListaPedidos.add(data);
                                        Log.d("BuscarNomes", "CARREGOU");
                                    }
                                    alert.dismiss();

                                    //seta os pedidos recebidos pelo banco para que apareçam na Tela de pedidos
                                    PedidosLista.setListaPedidos(ListaPedidos);
                                    TelaPedidos.setListaPedidos(ListaPedidos);
                                    TelaCriarPedidosClientes.setListaDeNomes(ListaPedidos);
                                    TelaEditPedido.setListaDeNomes(ListaPedidos);
                                    startActivity(intent);

                                } else {
                                    Log.d("BuscarNomes", "Error getting documents: ", task.getException());
                                }
                            }
                        });
                alerta.dismiss();
                alert.show();
            }
        });

        view.findViewById(R.id.button_complement).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.
                Intent intent = new Intent(TelaPrincipal.this, TelaCriarPedidoRevendedora.class);
                startActivity(intent);
                alerta.dismiss();
            }
        });

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    public static void setDataEscolhida(String data){
        dataEscolhida = data;
    }

    public static String getDataEscolhida(){
        return dataEscolhida;
    }

    //Consulta das datas dos pedidos na opção faturados
    public void RealizarConsultaDb_faturados(AlertDialog alert){
        ArrayList<Faturados> listaFaturados = new ArrayList();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios").document(usuarioID).collection("Faturados")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String dataInicio = (String) document.getData().get("DataCriacao");
                                String dataFim = (String) document.getData().get("Encerrado");
                                String valor = (String) document.getData().get("Total");
                                Faturados fatura = Faturados.FaturadoBuilder.builder()
                                                .setDataFim(dataFim)
                                                .setDataInicio(dataInicio)
                                                .setValor(valor)
                                                .setSelected(false)
                                                .build();

                                listaFaturados.add(fatura);
                            }
                            TelaFinanceiro.setFaturas(listaFaturados);
                            alert.dismiss();
                            //Intent intent = new Intent(telaAtual.this, TelaFinanceiro.class);
                            Intent intent = new Intent(TelaPrincipal.this, TelaFinanceiro.class);
                            startActivity(intent);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void RealizarConsultaDb_pedidos(AlertDialog alert){
        ArrayList listaCarregada = new ArrayList();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Usuarios").document(usuarioID).collection("PedidoRevendedora")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String data = (String) document.getData().get("data");
                                listaCarregada.add(data);
                                Log.d(TAG, "Documento carregado com sucesso");
                            }
                            listagem = listaCarregada;
                            alert.dismiss();
                            Dialog_pedido();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void IniciarComponentes(){
        toolbar = findViewById(R.id.toolBar);
        drawerLayout = findViewById(R.id.DrawerLayout);
        navigationView = findViewById(R.id.nav_view);
    }
}
