package com.rafaelavieiravendas.RafaelaDicorpo.model;

import android.content.Intent;

import com.rafaelavieiravendas.RafaelaDicorpo.TelaClientes;
import com.rafaelavieiravendas.RafaelaDicorpo.TelaPrincipal;

public class Pedido {

    private String nome;
    private String data;
    private String valor;
    private String status;
    private String quantidade;

    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getValor() {
        return valor;
    }

    public String getStatus() {
        return status;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public static class PedidoBuilder{
        private String nome;
        private String data;
        private String valor;
        private String status;
        private String quantidade;

        private PedidoBuilder(){}

        public PedidoBuilder setNome(String nome) {
            this.nome = nome;
            return this;
        }

        public PedidoBuilder setData(String data) {
            this.data = data;
            return this;
        }

        public PedidoBuilder setValor(String valor) {
            this.valor = valor;
            return this;
        }

        public PedidoBuilder setStatus(String status) {
            this.status = status;
            return this;
        }

        public PedidoBuilder setQuantidade(String quantidade) {
            this.quantidade = quantidade;
            return this;
        }

        public static PedidoBuilder builder(){
            return new PedidoBuilder();
        }


        public Pedido build(){
            Pedido pedido = new Pedido();
            pedido.nome = nome;
            pedido.data = data;
            pedido.status = status;
            pedido.valor = valor;
            pedido.quantidade = quantidade;
            return pedido;
        }
    }
}
