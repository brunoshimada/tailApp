package com.fiap.shimada.tail.service.retornador.strategy;

import com.fiap.shimada.tail.model.PalavraChave;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListaMenorQueQuantidadeEsperada implements IRetornadorDeLista {

    @Override
    public boolean isSatisfiedBy(Integer sizeLista, Integer quantidadeEsperada) {
        return sizeLista < quantidadeEsperada;
    }

    @Override
    public List<PalavraChave> retornar(List<PalavraChave> palavrasSorted, Integer quantidadeEsperada) {
        return palavrasSorted.subList(0, palavrasSorted.size());
    }
}
