package com.iotsuper.buyassist;

import android.text.BoringLayout;

public class Produto {
    private String nome;
    private Boolean checkbox;

    public Boolean getCheckbox() {
        return this.checkbox;
    }
    public String getNome() {
        return this.nome;
    }

    public Produto(){

    }

    public Produto(String nome, Boolean check){
        this.nome = nome;
        this.checkbox = check;
    }

}
