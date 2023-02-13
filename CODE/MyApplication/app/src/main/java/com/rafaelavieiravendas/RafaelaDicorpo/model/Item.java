package com.rafaelavieiravendas.RafaelaDicorpo.model;

public class Item {

    private String cor;
    private String id;
    private String preco;
    private String produto;
    private String quantidade;
    private  String tamanho;

    public String getTamanho() {
        return tamanho;
    }

    public String getProduto() {
        return produto;
    }

    public String getCor() {
        return cor;
    }

    public String getId() {
        return id;
    }

    public String getPreco() {
        return preco;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public static class ItemBuilder{
        private String cor;
        private String id;
        private String preco;
        private String produto;
        private String quantidade;
        private String tamanho;

        private ItemBuilder(){}

        public Item.ItemBuilder setCor(String cor) {
            this.cor = cor;
            return this;
        }

        public Item.ItemBuilder setTamanho(String tamanho) {
            this.tamanho = tamanho;
            return this;
        }

        public Item.ItemBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public Item.ItemBuilder setPreco(String preco) {
            this.preco = preco;
            return this;
        }

        public Item.ItemBuilder setProduto(String produto) {
            this.produto = produto;
            return this;
        }

        public Item.ItemBuilder setQuantidade(String quantidade) {
            this.quantidade = quantidade;
            return this;
        }

        public static Item.ItemBuilder builder(){
            return new Item.ItemBuilder();
        }

        public Item build(){
            Item item = new Item();
            item.cor = cor;
            item.id = id;
            item.preco = preco;
            item.produto = produto;
            item.quantidade = quantidade;
            item.tamanho = tamanho;
            return item;
        }

    }
}
