package com.utfpr.amiltonjr.revisaoautomotiva.modelo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Veiculo {

    @DatabaseField(generatedId=true)
    private int id;

    @DatabaseField(canBeNull=false, unique=true)
    private String placa;

    @DatabaseField(canBeNull=false)
    private String marca;

    @DatabaseField(canBeNull=false)
    private String modelo;

    @DatabaseField(canBeNull=false)
    private String cor;

    @DatabaseField(canBeNull=false)
    private String categoria;

    public Veiculo() {
        // ORMLite necessita de um construtor sem parâmetros
    }

    public Veiculo(String texto){
        setPlaca(texto);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }


    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }


    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }


    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }


    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }


    @Override
    public String toString(){
        return getPlaca();
    }
}
