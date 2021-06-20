package com.fiap.shimada.tail.service.tfidf;

import com.fiap.shimada.tail.controller.form.TailForm;
import com.fiap.shimada.tail.model.PalavraChave;
import com.fiap.shimada.tail.model.PalavraChaveTfIdf;
import com.fiap.shimada.tail.service.numeros.Numeros;
import com.fiap.shimada.tail.service.plural.ClassificadorPluralCaseS;
import com.fiap.shimada.tail.service.plural.IClassificadorPlural;
import com.fiap.shimada.tail.service.special.CaracteresEspeciais;
import com.fiap.shimada.tail.service.split.Splits;
import com.fiap.shimada.tail.service.stopwords.StopWords;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TailTfIdfService {

    private final List<IClassificadorPlural> classificadoresPlural;
    private final ClassificadorPluralCaseS classificadorPluralCaseS;

    public TailTfIdfService(List<IClassificadorPlural> classificadoresPlural, ClassificadorPluralCaseS classificadorPluralCaseS) {
        this.classificadoresPlural = classificadoresPlural;
        this.classificadorPluralCaseS = classificadorPluralCaseS;
    }

    public List<PalavraChave> gerarPalavrasChaveEstatistico(TailForm form) {
        final List<String> palavras = prepararTexto(form.getInputText());
        palavras.removeAll(StopWords.getStopwords());

        final Map<String, Long> mapaPorPalavraEQuantidade = palavras.stream().collect(
            Collectors.groupingBy(
                Function.identity(),
                Collectors.counting())
        );

        pontuarPlural(mapaPorPalavraEQuantidade, palavras);

        final List<PalavraChave> palavrasSorted = ordernarListaDePalavra(mapaPorPalavraEQuantidade);

        final List<PalavraChaveTfIdf> ordenadosPorTfIdf = palavrasSorted.stream().map(palavraChave -> {
            final Long nOcorrencias = palavraChave.getQuantidade();
            final int quantidadeTotalConsiderada = palavras.size();

            final double fatorTf = nOcorrencias.doubleValue() / Double.valueOf(quantidadeTotalConsiderada);
            final double fatorIf = Math.log10(Double.valueOf(quantidadeTotalConsiderada) / nOcorrencias.doubleValue());

            final double nota = fatorTf * fatorIf;

            return new PalavraChaveTfIdf(palavraChave.getPalavra(), nota);
        }).sorted(Comparator.comparing(PalavraChaveTfIdf::getFatorIfDf).reversed())
                                                    .collect(Collectors.toList());

        return ordenadosPorTfIdf.stream().map(palavraChaveTfIdf -> {
            final double nota = palavraChaveTfIdf.getFatorIfDf() * 100;
            return new PalavraChave(palavraChaveTfIdf.getPalavra(), Double.valueOf(nota).longValue());
        }).collect(Collectors.toList());
    }

    private List<String> prepararTexto(final String textoEntrada) {
        final Predicate<String> stringValida = s -> !s.isEmpty();
        final Predicate<String> maiorQueUm = s -> s.length() > 1;

        return Arrays.stream(textoEntrada.split(Splits.obterCaracteresDeSeparacao()))
                   .filter(stringValida)
                   .map(String::toLowerCase)
                   .map(CaracteresEspeciais::limparCaracteresEspeciais)
                   .map(Numeros::limparNumeros)
                   .filter(stringValida)
                   .filter(maiorQueUm)
                   .collect(Collectors.toList());
    }

    private void pontuarPlural(final Map<String, Long> mapaPorPalavraEQuantidade, final List<String> palavras) {
        palavras.forEach(palavra -> {
            final IClassificadorPlural classificadorPlural = classificadoresPlural.stream()
                                                                 .filter(classificador -> classificador.isSatisfiedBy(palavra))
                                                                 .findFirst()
                                                                 .orElse(classificadorPluralCaseS);

            final String singular = classificadorPlural.singular(palavra);

            if (mapaPorPalavraEQuantidade.containsKey(singular)) {
                final Long count = mapaPorPalavraEQuantidade.get(singular);
                mapaPorPalavraEQuantidade.remove(palavra);
                mapaPorPalavraEQuantidade.replace(singular, count + 1);
            }
        });
    }

    private List<PalavraChave> ordernarListaDePalavra(final Map<String, Long> mapaPorPalavraEQuantidade) {
        return mapaPorPalavraEQuantidade.entrySet().stream()
                   .map(PalavraChave::new)
                   .sorted(Comparator.comparing(PalavraChave::getQuantidade).reversed())
                   .collect(Collectors.toList());
    }
}
