package com.fiap.shimada.tail.service.retornador.strategy;

import com.fiap.shimada.tail.model.PalavraChave;

import java.util.List;

public interface IRetornadorDeLista {

    boolean isSatisfiedBy(Integer sizeLista, Integer quantidadeEsperada);
    List<PalavraChave> retornar(List<PalavraChave> palavrasSorted, Integer quantidadeEsperada);

}
