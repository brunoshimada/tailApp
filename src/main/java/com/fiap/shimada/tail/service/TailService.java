package com.fiap.shimada.tail.service;

import com.fiap.shimada.tail.controller.form.TailForm;
import com.fiap.shimada.tail.model.PalavraChave;
import com.fiap.shimada.tail.service.entidades.TailEntidadesService;
import com.fiap.shimada.tail.service.palavrasestatistica.TailPalavrasEstatisticaService;
import com.fiap.shimada.tail.service.retornador.strategy.IRetornadorDeLista;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TailService {

    private final TailPalavrasEstatisticaService tailPalavrasEstatisticaService;
    private final TailEntidadesService tailEntidadesService;
    private final List<IRetornadorDeLista> retornadores;


    public TailService(TailPalavrasEstatisticaService tailPalavrasEstatisticaService, TailEntidadesService tailEntidadesService, List<IRetornadorDeLista> retornadores) {
        this.tailPalavrasEstatisticaService = tailPalavrasEstatisticaService;
        this.tailEntidadesService = tailEntidadesService;
        this.retornadores = retornadores;
    }

    public List<PalavraChave> gerarMapa(TailForm form) {
        final List<PalavraChave> estatistico = tailPalavrasEstatisticaService.gerarPalavrasChaveEstatistico(form);
        final List<PalavraChave> entidades = tailEntidadesService.gerarPalavrasEntidades(form);

        estatistico.forEach(palavraChave -> {
            final String palavra = palavraChave.getPalavra();
            final List<Integer> levenshteinDistance = entidades.stream()
                                                          .filter(entidade -> entidade.getPalavra().toLowerCase().contains(palavra))
                                                          .map(entidadeSimilar -> StringUtils.getLevenshteinDistance(entidadeSimilar.getPalavra(), palavra))
                                                          .collect(Collectors.toList());

            final int divideBy = levenshteinDistance.size() > 0 ? levenshteinDistance.size() : 1;
            final int simpleMedian = levenshteinDistance.stream().reduce(0, Integer::sum) / divideBy;

            entidades.stream()
                .filter(entidade -> entidade.getPalavra().toLowerCase().contains(palavra))
                .filter(entidade -> StringUtils.getLevenshteinDistance(entidade.getPalavra(), palavra) <= simpleMedian)
                .forEach(entidade -> {
                entidade.setQuantidade(palavraChave.getQuantidade());
            });
        });

        final List<PalavraChave> estatisticoFiltradoEntidade = estatistico.stream().filter(palavraChave -> {
            final String palavra = palavraChave.getPalavra();
            return !entidades.stream().anyMatch(entidade -> entidade.getPalavra().equalsIgnoreCase(palavra) || entidade.getPalavra().toLowerCase().contains(palavra));
        }).collect(Collectors.toList());

        final List<PalavraChave> resultado = new ArrayList<>();
        resultado.addAll(estatisticoFiltradoEntidade);
        resultado.addAll(entidades);

        final IRetornadorDeLista retornadorDeLista = retornadores.stream()
                                                         .filter(strategy -> strategy.isSatisfiedBy(resultado.size(), form.getQuantidadePalavrasChave()))
                                                         .findFirst()
                                                         .orElseThrow(() -> new RuntimeException("Erro"));

        return retornadorDeLista.retornar(
            resultado.stream().sorted(Comparator.comparing(PalavraChave::getQuantidade).reversed()).collect(Collectors.toList()),
            form.getQuantidadePalavrasChave());

    }
}
