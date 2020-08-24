package com.iotsuper.buyassist;

import java.util.ArrayList;

public class Data {
    private ArrayList<Produto> produtos;

    public Data(){

    }

    public Data(ArrayList<Produto> produtos) {
        this.produtos = produtos;
    }

    public ArrayList<Produto> getProdutos() {
        return this.produtos;
    }
}
