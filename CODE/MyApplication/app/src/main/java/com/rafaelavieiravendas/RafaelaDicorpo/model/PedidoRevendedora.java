package com.rafaelavieiravendas.RafaelaDicorpo.model;

public class PedidoRevendedora {

   private String data;

   public String getData(){
       return data;
   }

    public static class PedidoRevendedoraBuilder {

        private String data;

        public PedidoRevendedoraBuilder setData(String data) {
            this.data = data;
            return this;
        }

        private PedidoRevendedoraBuilder(){}

        public static PedidoRevendedoraBuilder builder(){
            return new PedidoRevendedoraBuilder();
        }

        public PedidoRevendedora build(){
            PedidoRevendedora pedido = new PedidoRevendedora();
            pedido.data = data;
            return pedido;

        }

    }
}
