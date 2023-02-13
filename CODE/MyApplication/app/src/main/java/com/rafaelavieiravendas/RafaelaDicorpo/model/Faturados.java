package com.rafaelavieiravendas.RafaelaDicorpo.model;

public class Faturados {

    private String dataInicio;
    private String dataFim;
    private String valor;
    private boolean selected;

    public String getDataFim() {
        return dataFim;
    }
    public String getDataInicio() {
        return dataInicio;
    }
    public String getValor() {
        return valor;
    }
    public boolean getSelected(){ return selected;}

    public void setSelected(boolean selected){
        this.selected = selected;
    }

    public static class FaturadoBuilder {
        private String DataInicio;
        private String DataFim;
        private String Valor;
        private boolean Selected;


        private FaturadoBuilder() {
        }

        public Faturados.FaturadoBuilder setDataInicio(String dataInicio) {
            this.DataInicio = dataInicio;
            return this;
        }

        public Faturados.FaturadoBuilder setSelected(boolean selected){
            this.Selected = selected;
            return this;
        }

        public Faturados.FaturadoBuilder setDataFim(String dataFim) {
            this.DataFim = dataFim;
            return this;
        }

        public Faturados.FaturadoBuilder setValor(String valor) {
            this.Valor = valor;
            return this;
        }

        public static Faturados.FaturadoBuilder builder() {
            return new Faturados.FaturadoBuilder();
        }


        public Faturados build() {
            Faturados fatura = new Faturados();
            fatura.valor = Valor;
            fatura.dataInicio = DataInicio;
            fatura.dataFim = DataFim;
            fatura.selected = Selected;
            return fatura;
        }

    }
}
