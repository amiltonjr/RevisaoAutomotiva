package com.utfpr.amiltonjr.revisaoautomotiva.modelo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class Manutencao {

    @DatabaseField(generatedId=true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String descricao;

    @DatabaseField(canBeNull = false)
    private String tipo;

    @DatabaseField(canBeNull = false)
    private int quilometragem;

    @DatabaseField(canBeNull = false)
    private double valor;

    // As opções foreignAutoCreate e foreignAutoRefresh estão no ORMLite
    @DatabaseField(foreign = true) // o descricao da chave estrangeira gerado pelo ORMLite será veiculo_id
    private Veiculo veiculo;

    public Manutencao() {
        // ORMLite necessita de um construtor sem parâmetros
    }

    public Manutencao(String descricao){
        setDescricao(descricao);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }


    public int getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(int quilometragem) {
        this.quilometragem = quilometragem;
    }


    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }


    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }


    @Override
    public String toString() {
        return getDescricao();
    }
}
