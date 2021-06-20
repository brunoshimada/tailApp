package com.fiap.shimada.tail.service;

import com.fiap.shimada.tail.controller.form.TailForm;
import com.fiap.shimada.tail.model.PalavraChave;
import com.fiap.shimada.tail.service.entidades.TailEntidadesService;
import com.fiap.shimada.tail.service.palavrasestatistica.TailPalavrasEstatisticaService;
import com.fiap.shimada.tail.service.retornador.strategy.IRetornadorDeLista;

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

        final List<PalavraChave> estatisticoRecalculado = estatistico.stream()
                                               .map(palavraChave -> {
                                                   final String palavra = palavraChave.getPalavra();

                                                   final List<PalavraChave> entidadesDaPalavra = entidades.stream().filter(entidade -> entidade.getPalavra().toLowerCase().contains(palavra)).collect(Collectors.toList());

                                                   final int frequenciaNasEntidades = entidadesDaPalavra.size();

                                                   entidades.stream().filter(entidade -> entidade.getPalavra().toLowerCase().contains(palavra)).forEach(entidade -> {
                                                       entidade.setQuantidade(entidade.getQuantidade() + frequenciaNasEntidades);
                                                   });

                                                   final long novaQuantidade = frequenciaNasEntidades > 0 ? palavraChave.getQuantidade() * frequenciaNasEntidades : palavraChave.getQuantidade();

                                                   return new PalavraChave(
                                                       palavra,
                                                       novaQuantidade
                                                   );
                                               })
                                               .sorted(Comparator.comparing(PalavraChave::getQuantidade).reversed())
                                               .collect(Collectors.toList());

        final List<PalavraChave> resultado = new ArrayList<>();
        resultado.addAll(estatisticoRecalculado);
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
