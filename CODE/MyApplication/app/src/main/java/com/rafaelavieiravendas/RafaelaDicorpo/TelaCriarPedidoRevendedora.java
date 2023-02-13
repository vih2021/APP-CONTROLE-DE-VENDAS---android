package com.rafaelavieiravendas.RafaelaDicorpo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class TelaCriarPedidoRevendedora extends AppCompatActivity {

    private TextView input_date;
    private TextView input_data_final;
    //private TextView input_qntd;
    private EditText input_descricao;
    //private EditText textoValor;
    private AppCompatButton button_descartar;
    private AppCompatButton button_registrar;
    private String usuarioID;
    private String date;
    private static ArrayList pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_revendedora);

        IniciarComponentes();

        //String data = input_date.getText().toString();
        //String valor = input_data_final.getText().toString();
        //String quantidade = input_qntd.getText().toString();
        //String descricao = input_descricao.getText().toString();

        //quando apaga tod* o texto ele remove a formatação na mask, tem que ver isso
        //textoValor.addTextChangedListener(Mask.monetario(textoValor));

        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        input_date.setOnClickListener(new View.OnClickListener() {
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

                            input_date.setText(data);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        input_date.setTextColor(Color.BLACK);

                    }
                });

            }
        });

        MaterialDatePicker datePicker1 = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        input_data_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker1.show(getSupportFragmentManager(), "Material_date_Picker");
                datePicker1.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        DateFormat formatador = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
                        try {
                            Date date = formatador.parse(datePicker1.getHeaderText());
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            String data = df.format(date);

                            input_data_final.setText(data);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        input_data_final.setTextColor(Color.BLACK);
                    }
                });
            }
        });

        button_descartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descartar();
            }
        });

        button_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = input_date.getText().toString();
                String data2 = input_data_final.getText().toString();
                //String quantidade = input_qntd.getText().toString();

                if(data.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Preencha os campos obrigatórios", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else if(pedido.contains(data)){
                    Snackbar snackbar = Snackbar.make(v, "Esta data de pedido ja está registrada", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    SalvarDadosPedidoRevendedora();
                    Snackbar snackbar = Snackbar.make(v,"Demanda Registrada com sucesso!", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                    button_registrar.setEnabled(false);

                    button_descartar.setEnabled(false);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);
                }
            }
        });
    }

    public static void setListaPedidos(ArrayList lista){
        pedido = lista;
    }

    public void descartar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TelaCriarPedidoRevendedora.this);
        builder.setTitle("Deseja descartar este Registro?");
        builder.setPositiveButton("sim",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TelaCriarPedidoRevendedora.this.finish();
            } });
        builder.setNegativeButton("não", null);
        AlertDialog alerta = builder.create();
        alerta.show();
    }
    
    private void SalvarDadosPedidoRevendedora(){

        String data = input_date.getText().toString();
        String valor = input_data_final.getText().toString();
        //String quantidade = input_qntd.getText().toString();
        String descricao = input_descricao.getText().toString();

        /*
        class PedidoValue {

            public String data;
            public String valor;
            public String quantidade;
            public String descricao;

            public PedidoValue(String data, String valor, String quantidade, String descricao) {
                this.data = data;
                this.valor = valor;
                this.quantidade = quantidade;
                this.descricao = descricao;
            }
        }


        PedidoValue novoPedido = new PedidoValue(data, valor, quantidade, descricao);
        String dataRefactor = data.replace("/", "-");
        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("PedidoRevendedora");

        myRef.child(usuarioID).child(dataRefactor).setValue(novoPedido);
        */

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String,Object> pedidos = new HashMap<>();
        pedidos.put("data", data);
        pedidos.put("data final", valor);
        //pedidos.put("qntd", quantidade);
        if(!descricao.isEmpty()){
            pedidos.put("descricao", descricao);
        }

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        date = data.replace("/", ".");

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID).collection("PedidoRevendedora").document(date);
        documentReference.set(pedidos).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void IniciarComponentes(){
        input_date = findViewById(R.id.input_date);
        //textoValor = findViewById(R.id.input_valor);
        button_descartar = findViewById(R.id.button_descartar);
        button_registrar = findViewById(R.id.button_registrar);
        input_descricao = findViewById(R.id.input_descricao);
        input_data_final= findViewById(R.id.input_data_final);
        //input_qntd = findViewById(R.id.input_qntd);
    }

}
