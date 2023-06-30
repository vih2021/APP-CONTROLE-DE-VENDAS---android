package com.rafaelavieiravendas.RafaelaDicorpo;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.rafaelavieiravendas.RafaelaDicorpo.model.FilterLista;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Item;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;
import com.rafaelavieiravendas.RafaelaDicorpo.model.PedidosLista;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TelaEditPedido extends AppCompatActivity {

    //private PedidoClienteFilterAdapter itemAdapter;
    private Button botao_adicionar;
    //private FilterLista pedido;
    //public RecyclerView recycler_itens;
    public ChipGroup chip;
    //public Chip chips ;
    private TextView Input_data;
    private EditText Input_nome;
    private TextView Input_valor;
    private TextView Input_quantidade;
    private Button btn_registrar;
    private Button btn_descartar;
    private AlertDialog alerta;
    private String usuarioID;
    static String date;
    private int ContagemItens;
    private TextView title;
    static int Position_pedido;
    public static ArrayList<String> ListaDeNomes = new ArrayList<String>();

    ArrayList<InformationItem> ListaDeItens = new ArrayList<>(); //novos itens
    ArrayList<Pedido> ListaPedidosAtualizada = new ArrayList<Pedido>();
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static ArrayList<Item> ListaDeItensExistentes = new ArrayList<>(); //itens existentes
    private static String NomeAtual;
    private static String DataAtual;


    public static void setItensExistentes(ArrayList lista){
        ListaDeItensExistentes = lista;
    }

    public static void setNomeExistente(String nome){
        NomeAtual = nome;
    }

    public static void setDataExistente(String data){
        DataAtual = data;
    }

    public static void setPosition_pedido(int position){
        Position_pedido = position;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setListaDeNomes(ArrayList<Pedido> lista){
        ArrayList nomes = new ArrayList();
        for(Pedido item : lista){
            nomes.add(item.getNome());
        }
        ListaDeNomes = nomes;
    }

    class Sum{
        public int id;
        public int preco;
        public int quantidade;

        public Sum(int id, int preco, int quantidade){
            this.id = id;
            this.preco = preco;
            this.quantidade = quantidade;
        }

        public int getId(){
            return this.id;
        }

        public int getPreco(){
            return this.preco;
        }

        public int getQuantidade(){
            return this.quantidade;
        }
    }

    class InformationItem{
        private int Index;
        private String id;
        private String tamaho;
        private String cor;
        private String preco;
        private int quantidade;
        private String produto;

        public InformationItem(Integer index, String id, String tamanho, String cor, String preco, int quantidade, String produto){
            this.Index = index;
            this.id = id;
            this.tamaho = tamanho;
            this.cor = cor;
            this.preco = preco;
            this.quantidade = quantidade;
            this.produto = produto;
        }

        public void setIndex(int index){
            this.Index = index;
        }

        public int getIndex(){
            return this.Index;
        }

        public String getId(){
            return id;
        }

        public void setId(String id){
            this.id = id;
        }

        public String getTamaho() {
            return tamaho;
        }

        public void setTamaho(String tamaho) {
            this.tamaho = tamaho;
        }

        public String getCor() {
            return cor;
        }

        public void setCor(String cor) {
            this.cor = cor;
        }

        public String getPreco() {
            return preco;
        }

        public void setPreco(String preco) {
            this.preco = preco;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }

        public String getProduto() {
            return produto;
        }

        public void setProduto(String produto) {
            this.produto = produto;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void AdicionarInformacaoExistente(){

        btn_registrar.setText("Atualizar");
        Input_nome.setText(NomeAtual);
        Input_data.setText(DataAtual);
        title.setText("Atualizar pedido");

        for(int i = 0; i < ListaDeItensExistentes.size(); i++){
            Item item = ListaDeItensExistentes.get(i);
            ListaDeItens.add(new InformationItem(ContagemItens , item.getId(), item.getTamanho(), item.getCor(), item.getPreco(), Integer.parseInt(item.getQuantidade()), item.getProduto()));
            ContagemItens += 1;
        }

        for(int i = 0; i < ListaDeItens.size() ; i++){
            InformationItem item = ListaDeItens.get(i);
            addItem(item.getId(), item.getIndex());
        }

        somarTodosItens();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ContagemItens = 0;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos_clientes);

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        IniciarComponentes();

        AdicionarInformacaoExistente();

        botao_adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_pedido_item();
            }
        });

        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        Input_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "Material_date_Picker");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        DateFormat formatador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
                        try {
                            Date date = formatador.parse(datePicker.getHeaderText());
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            String data = df.format(date);

                            Input_data.setText(data);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Input_data.setTextColor(Color.BLACK);

                    }
                });
            }
        });

        btn_registrar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean result = false;

                String nome = Input_nome.getText().toString();
                String data = Input_data.getText().toString();

                for(String item : ListaDeNomes){
                    Log.d("batata" , "" + item);
                    if(!nome.equals(NomeAtual) && item.equals(nome)){
                        Log.d("testando debug", ""+ nome + " " + NomeAtual);
                        result = true;
                        break;
                    }else{
                        result = false;
                    }
                }

                if(nome.isEmpty() | data.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Preencha todos os campos obrigatórios", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else if(result == true){
                    Snackbar snackbar = Snackbar.make(v, "Já existe um registro neste nome", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }else if(ListaDeItens.size() == 0) {
                    Snackbar snackbar = Snackbar.make(v, "Você precisa incluir itens no pedido", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    Snackbar snackbar = Snackbar.make(v,"Pedido atualizado com sucesso!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    btn_registrar.setEnabled(false);

                    btn_descartar.setEnabled(false);

                    SalvarAlteracoes();
                }
            }
        });

        btn_descartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descartar();
            }
        });
    }

// VERIFICAR ESSA CHAMAR POIS ELA PODE SER INUTIL
    /*
    static void Buscar_Nomes_Pedidos(){
        ListaDeNomes = new ArrayList();
        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String date = TelaPrincipal.getDataEscolhida().replace("/", ".");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora/"+ date +"/Pedidos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String data = (String) document.getData().get("nome");
                                ListaDeNomes.add(data);
                                Log.d("BuscarNomes", "Documento carregado com sucesso");
                            }
                        } else {
                            Log.d("BuscarNomes", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

     */

    //.2f PONTO FLUTUANTE AJUSTAR NO PRECO PARA 2 CASAS DEPOIS DO PONTO
    public static void setDate(String data){
        date = data;
    }

    public void SalvarAlteracoes(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        date = date.replace("/", ".");
        db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora").document(date).collection("Pedidos")
                .document(NomeAtual)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //PedidosLista.removePedido(Position_pedido);
                        //PedidosLista.removePedidoListaEmUso(Position_pedido);
                        Log.d("DATAAAAAAAAAAAAAA", "Documento Excluido "+ date + NomeAtual);
                        SalvarDados();
                        //Log.d("POSICAO", "AAA" + Position_pedido);
                        //TelaVisualizarPedido.UpdateListaDeItens();
                    }
                });

        db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora").document(date).collection("Pedidos")
                .document(NomeAtual).collection("Itens").document()
                .delete();
    }

    public void SalvarDados(){

        String nome = Input_nome.getText().toString();
        String data = Input_data.getText().toString();
        String valor = Input_valor.getText().toString();
        String quantidade = Input_quantidade.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("DATAAAAAAAAAAAAAA", ""+date);

        date = date.replace("/", ".");

        WriteBatch batch = db.batch();

        Map<String,Object> pedidos = new HashMap<>();
        pedidos.put("status", "Pendente");
        pedidos.put("nome", nome);
        pedidos.put("data", data);
        pedidos.put("valor", valor);
        pedidos.put("quantidade", quantidade);
        //pedidos.put("qntd", quantidade);

        DocumentReference InfoPedido = db.collection("Usuarios").document(usuarioID)
                .collection("PedidoRevendedora").document(date).collection("Pedidos")
                .document(nome);

        for(int i = 0; i < ListaDeItensExistentes.size(); i++){
            db.collection("Usuarios").document(usuarioID)
                    .collection("PedidoRevendedora").document(date).collection("Pedidos")
                    .document(nome).collection("Itens").document(ListaDeItensExistentes.get(i).getId()).delete();
        }

        batch.set(InfoPedido, pedidos);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("db", "Sucesso ao salvar dados no Banco");
            }
        });



        ///////////////////////////////////////////////////////////////////////////////////////////////////////////

        Map<String,Object> item = new HashMap<>();

        for(int i = 0; i < ListaDeItens.size(); i++){
            item.put("Produto", ListaDeItens.get(i).getProduto());
            item.put("Quantidade", ListaDeItens.get(i).getQuantidade());
            item.put("Preco", ListaDeItens.get(i).getPreco());
            item.put("Cor", ListaDeItens.get(i).getCor());
            item.put("Num/id", ListaDeItens.get(i).getId());
            item.put("Tamanho", ListaDeItens.get(i).getTamaho());

            DocumentReference documentReferenceItem = db.collection("Usuarios").document(usuarioID)
                    .collection("PedidoRevendedora").document(date).collection("Pedidos")
                    .document(nome).collection("Itens").document(ListaDeItens.get(i).getId());

            //.collection(nome)
            //.document("Itens").collection(ListaDeItens.get(i).getId()).document("Propriedades");

            documentReferenceItem.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("db", "Sucesso ao salvar dados no Banco");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("db_error", "Erro ao salvar dados no Banco" + e.toString());
                        }
                    });
        }
         RealizarConsultaDb();

        //quando adicionado um novo pedido, o mesmo é injeto automaticamente no lista de pedidos ja existe para evitar
        //uma nova consulta no banco de dados
        /*
        Pedido atualizacao = Pedido.PedidoBuilder.builder()
                .setNome(nome)
                .setData(data)
                .setValor(valor)
                .setQuantidade(quantidade)
                .setStatus("Pendente")
                .build();

        PedidosLista.setAtualizacaoPedidos(atualizacao);

         */
    }

    public void descartar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaEditPedido.this);
        builder.setTitle("Deseja descartar as alterações?");
        builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TelaEditPedido.this.finish();
                Intent tela = new Intent(TelaEditPedido.this, TelaPedidos.class);
                startActivity(tela);
            }
        });
        builder.setNegativeButton("não", null);
        alerta = builder.create();
        alerta.show();
    }

    public void addItem(String id, int index){
        Chip chipx = new Chip(this);
        chipx.setText(id);
        chipx.setCheckable(false);
        chipx.setCloseIcon(getDrawable(R.drawable.close_icon));
        chipx.setCloseIconVisible(true);
        chipx.setClickable(false);
        chipx.setId(index);

        chipx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_pedido(chipx.getId());
            }
        });

        chipx.setOnCloseIconClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {

                for(int i = 0; i < ListaDeItens.size(); i++ ) {
                    if (ListaDeItens.get(i).getIndex() == chipx.getId()) { //////CUEIO
                        ListaDeItens.remove(i);
                    }
                    Log.d("procurando BUG", "          " + ListaDeItens.size());
                }

                chip.removeView(chipx);
                somarTodosItens();
            }
        });

        chip.addView(chipx);
        chip.setVisibility(View.VISIBLE);
    }

    private void Dialog_pedido(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,  R.style.CustomAlertDialog);
        //android.R.style.Theme_Translucent_NoTitleBar     deixa o dialog transparente

        LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view

        View view = li.inflate(R.layout.chip_item, null);

        TextView item_tamanho = view.findViewById(R.id.tamanho);
        TextView item_cor = view.findViewById(R.id.cor);
        TextView item_preco = view.findViewById(R.id.preco);
        TextView item_quantidade = view.findViewById(R.id.quantidade);
        TextView item_produto = view.findViewById(R.id.produto);

        InformationItem item = null;

        for(int i = 0; i < ListaDeItens.size(); i++){
            if(ListaDeItens.get(i).getIndex() == position){ //////CUEIO
                item = ListaDeItens.get(i);
            }
        }

        item_tamanho.setText("Tamanho : " + item.getTamaho() );
        item_cor.setText("Cor : " + item.getCor());
        item_preco.setText("Preço : " + item.getPreco());
        item_quantidade.setText("Quantidade : " + item.getQuantidade());
        item_produto.setText("Produto : " + item.getProduto());


        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    private void Dialog_pedido_item() {

        AlertDialog.Builder builder = new AlertDialog.Builder(TelaEditPedido.this, R.style.CustomAlertDialog);
        //android.R.style.Theme_Translucent_NoTitleBar     deixa o dialog transparente

        LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.input_item, null);

        EditText tamanho_element = view.findViewById(R.id.editext_tamanho);
        EditText id_element = view.findViewById(R.id.editext_id);
        EditText cor_element = view.findViewById(R.id.editext_cor);
        EditText preco_element = view.findViewById(R.id.editext_preco);
        EditText quantidade_element = view.findViewById(R.id.editext_quantidade);
        EditText produto_element = view.findViewById(R.id.editext_produto);

        preco_element.addTextChangedListener(new Mask3(preco_element));

        view.findViewById(R.id.botao_descartar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alerta.dismiss();
            }
        });

        view.findViewById(R.id.botao_adicionar).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("UseCompatTextViewDrawableApis")
            public void onClick(View arg0) {
                String id = id_element.getText().toString();
                String produto = produto_element.getText().toString();
                String quantidade = quantidade_element.getText().toString();
                String preco = preco_element.getText().toString();
                String cor = cor_element.getText().toString();
                String tamanho = tamanho_element.getText().toString();

                if(id.isEmpty() | produto.isEmpty() | quantidade.isEmpty()| preco.isEmpty()| cor.isEmpty() | tamanho.isEmpty()){
                    Snackbar snackbar1 = Snackbar.make(view, "Preencha todos os campos", Snackbar.LENGTH_SHORT);
                    snackbar1.setTextColor(Color.WHITE);
                    snackbar1.setBackgroundTint(Color.rgb(151,68,68));
                    snackbar1.setAnchorView(view.findViewById(R.id.botao_adicionar));
                    snackbar1.show();


                    if(id.isEmpty()){
                        id_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invalidated, 0);
                    }else{
                        id_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.validated, 0);
                    }

                    if(produto.isEmpty()){
                        produto_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invalidated, 0);
                    }else {
                        produto_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.validated, 0);
                    }

                    if(quantidade.isEmpty()){
                        quantidade_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invalidated, 0);
                    }else {
                        quantidade_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.validated, 0);
                    }

                    if(preco.isEmpty()){
                        preco_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invalidated, 0);
                    }else {
                        preco_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.validated, 0);
                    }

                    if(cor.isEmpty()){
                        cor_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invalidated, 0);
                    }else {
                        cor_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.validated, 0);
                    }

                    if(tamanho.isEmpty()){
                        tamanho_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invalidated, 0);
                    }else {
                        tamanho_element.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.validated, 0);
                    }

                }else{
                    InformationItem item = new InformationItem(ContagemItens ,id, tamanho, cor, preco, Integer.parseInt(quantidade), produto);
                    ListaDeItens.add(item);
                    addItem(id, ContagemItens);
                    ContagemItens = ContagemItens + 1;
                    somarTodosItens();

                    alerta.dismiss();
                }
            }
        });

        builder.setView(view);

        alerta = builder.create();
        alerta.show();
    }

    public void somarTodosItens() {
        ArrayList<Sum> Sum = new ArrayList<>();

        int PrecoFinalPedido = 0;
        int QuantidadeFinalPedido = 0;


        for (int i = 0; i < ListaDeItens.size(); i++) {
            String precoInicial = (ListaDeItens.get(i).getPreco()).replace("R$", "")
                    .replace(",", ".")
                    .replace(".", "");
            int precoNormal = Integer.parseInt(precoInicial);
            int precoFinal = precoNormal * ListaDeItens.get(i).getQuantidade();

            Sum soma = new Sum(ListaDeItens.get(i).getIndex(), precoFinal, ListaDeItens.get(i).getQuantidade());
            Sum.add(soma);
        }

        for (int i = 0; i < Sum.size(); i++) {
            PrecoFinalPedido = PrecoFinalPedido + Sum.get(i).getPreco();
            QuantidadeFinalPedido = QuantidadeFinalPedido + Sum.get(i).getQuantidade();
        }

        Input_quantidade.setText(String.valueOf(QuantidadeFinalPedido));

        Input_valor.setText(MascaraPreco(String.valueOf(PrecoFinalPedido)));
    }

    public String MascaraPreco(String value){
        String valor = value;

        int tamanho = valor.length();

        for (int i= valor.length() -1; i > -1; i--) {
            if (valor.length() == 2){
                valor = "0" + valor;
                tamanho = valor.length();
            }
            if(i == valor.length() - 2) {
                valor = valor.substring(0, i) + "," + valor.substring(i, tamanho);
                tamanho = valor.length();
            }if(i == valor.length() - 6 && i != 0){
                tamanho = valor.length();
                valor = valor.substring(0, i) + "." + valor.substring(i, tamanho);
            }if(i == valor.length() - 9 && i != 0) {
                tamanho = valor.length();
                valor = valor.substring(0, i) + "." + valor.substring(i, tamanho);
            }
        }

        if (valor.length() == 1){
            valor = "0,0" + valor;
        }else if (valor.length() == 3){
            valor = valor + "0";
        }

        Log.d("valorrr", ""+valor);
        return valor;
    };

    public void RealizarConsultaDb(){
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
                                    Intent intent = new Intent(TelaEditPedido.this, TelaPedidos.class);
                                    startActivity(intent);
                                }
                            }, 1500);

                        } else {
                            Log.d("BuscarNomes", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void IniciarComponentes(){
        chip = findViewById(R.id.chips);
        Input_data = findViewById(R.id.input_date);
        Input_nome = findViewById(R.id.input_nome);
        Input_valor = findViewById(R.id.input_valor);
        Input_quantidade = findViewById(R.id.input_qntd);
        btn_registrar = findViewById(R.id.button_registrar);
        btn_descartar = findViewById(R.id.button_descartar);
        botao_adicionar = findViewById(R.id.botao_adicionar);
        title = findViewById(R.id.title_bar);

    }


}
