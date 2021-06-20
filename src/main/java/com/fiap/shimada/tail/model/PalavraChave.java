package com.fiap.shimada.tail.model;

import java.util.Map;

public class PalavraChave {

    private String palavra;
    private Long quantidade;

    public PalavraChave(String palavra, Long quantidade) {
        this.palavra = palavra;
        this.quantidade = quantidade;
    }

    public PalavraChave(Map.Entry<String, Long> par) {
        palavra = par.getKey();
        quantidade = par.getValue();
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
