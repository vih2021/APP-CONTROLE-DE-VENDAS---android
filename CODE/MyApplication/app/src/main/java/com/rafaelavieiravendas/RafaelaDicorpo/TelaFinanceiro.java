package com.rafaelavieiravendas.RafaelaDicorpo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Faturados;
import com.rafaelavieiravendas.RafaelaDicorpo.model.Pedido;
import com.rafaelavieiravendas.RafaelaDicorpo.model.PedidosLista;

import java.util.ArrayList;

public class TelaFinanceiro extends AppCompatActivity {

    private RecyclerView recycler_faturas;
    private TextView message_empty;
    private static ArrayList<Faturados> listaFaturados = new ArrayList<Faturados>();
    private Button apagar;
    private static View background_card;
    private ImageView no_selection;
    private ArrayList<View> lista_selecionados = new ArrayList<View>();
    int contagem_itens_selecionados = 0;
    FrameLayout.LayoutParams parameter;
    FaturasAdapter faturasAdapter;
    private int click_botao = 0;

    int contagem_loop = 0;

    public static void setViewer( View item ){
        background_card = item;
    }

    public void cancel_selection(){
        faturasAdapter.setListener(new FaturasAdapter.FaturaAdapterListener(){
            @Override
            public void OnItemClick(int position, View item) {
            }
        });

        apagar.setText("Selecionar Itens");

        no_selection.setVisibility(View.INVISIBLE);

        for (View views : lista_selecionados){
            views.setLayoutParams(parameter);
            views.setBackgroundColor(Color.parseColor("#4CAF50"));
        }

        for (Faturados fatura : listaFaturados){
            fatura.setSelected(false);
        }

        click_botao = 0;

        lista_selecionados = new ArrayList<View>();
    }

    public void descartar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaFinanceiro.this);
        builder.setTitle("Deseja apagar estes registros?");
        builder.setMessage(contagem_itens_selecionados + " itens selecionados.");

        builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.layout),"Registros excluídos com sucesso!", Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
                //CRIAR A FUNÇÃO DELETE NO BANCO
                //SETAR NOVAMENTE A LISTA NO ADAPTER.
                deleteRegister();
                faturasAdapter = new FaturasAdapter(new ArrayList<Faturados>(listaFaturados));
                recycler_faturas.setAdapter(faturasAdapter);
            }
        });



                builder.setNegativeButton("não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancel_selection();
                    }
                });

        AlertDialog alerta = builder.create();
        alerta.show();
    }

    public static void setFaturas(ArrayList<Faturados> lista){
        listaFaturados = lista;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_financeiro);

        InicarComponentes();

        if(listaFaturados.isEmpty()){
            message_empty.setText("Não há nada faturado por aqui...");
        }

        faturasAdapter = new FaturasAdapter(new ArrayList<Faturados>(listaFaturados));
        recycler_faturas.setAdapter(faturasAdapter);
        faturasAdapter.setListener(new FaturasAdapter.FaturaAdapterListener(){
            @Override
            public void OnItemClick(int position, View item) {
            }
        });

        apagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click_botao ++;

                if(click_botao > 1 && contagem_itens_selecionados != 0){
                    descartar();
                }

                contagem_itens_selecionados = 0;

                apagar.setText("Apagar ("+contagem_itens_selecionados+")");

                no_selection.setVisibility(View.VISIBLE);

                no_selection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancel_selection();
                    }
                });

                faturasAdapter.setListener(new FaturasAdapter.FaturaAdapterListener(){
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void OnItemClick(int position, View item) {
                        boolean isSelected = listaFaturados.get(position).getSelected();

                        LayoutInflater li = getLayoutInflater();
                        View view= li.inflate(R.layout.item_financeiro, null);
                        parameter= (FrameLayout.LayoutParams) view.findViewById(R.id.container_pedidos).getLayoutParams();

                        //Log.d("selecionado????", "" + isSelected);

                        if (!isSelected){
                            //item.getLayoutParams();
                            item.setBackgroundColor(Color.parseColor("#C84CAF50"));
                            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                            );
                            params.setMargins(20, 10, 20, 57);
                            item.setLayoutParams(params);

                            listaFaturados.get(position).setSelected(true);
                            contagem_itens_selecionados = contagem_itens_selecionados + 1;
                            apagar.setText("Apagar ("+contagem_itens_selecionados+")");

                            lista_selecionados.add(item);
                        }else if (isSelected){
                            item.setBackgroundColor(Color.parseColor("#4CAF50"));
                            item.setLayoutParams(parameter);
                            listaFaturados.get(position).setSelected(false);
                            contagem_itens_selecionados = contagem_itens_selecionados - 1;
                            apagar.setText("Apagar ("+contagem_itens_selecionados+")");
                        }
                        //item.setPaddingRelative(10,10,10,10);
                    }
                });
            }
        });
    }

    public void deleteRegister(){

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for( Faturados fatura : listaFaturados){
            contagem_loop ++;
            if(fatura.getSelected() == true){

                int finalContagem_loop = contagem_loop;
                db.collection("Usuarios").document(usuarioID).collection("Faturados").document(fatura.getDataInicio().replace("/", "."))
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("FATURA EXCLUIDA", "" + fatura.getDataInicio());
                            }
                        });

                listaFaturados.remove(contagem_loop - 1);
            }
        }
    }



    public void InicarComponentes(){
        recycler_faturas = findViewById(R.id.recycle_view);
        message_empty = findViewById(R.id.message_empty);
        apagar = findViewById(R.id.apagar);
        no_selection = findViewById(R.id.s);
    }
}