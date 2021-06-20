package com.fiap.shimada.tail.model;

public class PalavraChaveTfIdf {

    private String palavra;
    private Double fatorIfDf;

    public PalavraChaveTfIdf(String palavra, Double fatorIfDf) {
        this.palavra = palavra;
        this.fatorIfDf = fatorIfDf;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public Double getFatorIfDf() {
        return fatorIfDf;
    }

    public void setFatorIfDf(Double fatorIfDf) {
        this.fatorIfDf = fatorIfDf;
    }
}
