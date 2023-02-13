package com.rafaelavieiravendas.RafaelaDicorpo.model;

import com.rafaelavieiravendas.RafaelaDicorpo.TelaPedidos;
import com.rafaelavieiravendas.RafaelaDicorpo.TelaPrincipal;

import java.util.ArrayList;
import java.util.List;

public class PedidosLista {

    static ArrayList<Pedido> lista;
    static ArrayList<Pedido> listaEmUso;

    public static void setAtualizacaoPedidos(Pedido novospedidos){
        lista.add(novospedidos);
    }

    public static void removePedido(int position){
        lista.remove(position);
        TelaPedidos.setListaPedidos(lista);
    }

    /*
    public static void setListaEmUso(ArrayList lista){
        listaEmUso = lista;
    }

    public static void removePedidoListaEmUso(int position){
        listaEmUso.remove(position);
    }

     */

    public static void setListaPedidos(ArrayList<Pedido> listapedidos){
        lista = listapedidos;
    }

    public static List<Pedido> GeradorPedidos(){
        return lista;
    }
}
