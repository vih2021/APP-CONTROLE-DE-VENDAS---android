package com.rafaelavieiravendas.RafaelaDicorpo.model;

import java.util.ArrayList;

public class FilterLista {

    private String productId;
    private String price;


    public FilterLista(String productId, String price){
        this.productId = productId;
        this.price = price;
    }

    public String getProductId(){
        return productId;
    }

    public String getPrice(){
        return price;
    }

}
