package com.fiap.shimada.tail.service.palavrasestatistica;

import com.fiap.shimada.tail.controller.form.TailForm;
import com.fiap.shimada.tail.model.PalavraChave;
import com.fiap.shimada.tail.service.numeros.Numeros;
import com.fiap.shimada.tail.service.plural.ClassificadorPluralCaseS;
import com.fiap.shimada.tail.service.plural.IClassificadorPlural;
import com.fiap.shimada.tail.service.retornador.strategy.IRetornadorDeLista;
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
public class TailPalavrasEstatisticaService {

    private final List<IRetornadorDeLista> retornadores;
    private final List<IClassificadorPlural> classificadoresPlural;
    private final ClassificadorPluralCaseS classificadorPluralCaseS;

    public TailPalavrasEstatisticaService(List<IRetornadorDeLista> retornadores,
                                          List<IClassificadorPlural> classificadoresPlural, ClassificadorPluralCaseS classificadorPluralCaseS) {
        this.retornadores = retornadores;
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

        final Long minValue = calcularMin(mapaPorPalavraEQuantidade);
        final Double median = calcularMedia(mapaPorPalavraEQuantidade, minValue);

        final List<PalavraChave> palavrasSorted = ordernarListaDePalavra(mapaPorPalavraEQuantidade, median);

        final IRetornadorDeLista retornadorDeLista = retornadores.stream()
                                                         .filter(strategy -> strategy.isSatisfiedBy(palavrasSorted.size(), form.getQuantidadePalavrasChave()))
                                                         .findFirst()
                                                         .orElseThrow(() -> new RuntimeException("Erro"));

        return palavrasSorted;
//        return retornadorDeLista.retornar(palavrasSorted, form.getQuantidadePalavrasChave());
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

    private Double calcularMedia(final Map<String, Long> mapaPorPalavraEQuantidade, Long minValue) {
        return mapaPorPalavraEQuantidade.values().stream()
                   .filter(quantidade -> quantidade > minValue)
                   .mapToLong(Long::longValue)
                   .average()
                   .orElse(0.0);
    }

    private Long calcularMin(final Map<String, Long> mapaPorPalavraEQuantidade) {
        return mapaPorPalavraEQuantidade.values().stream()
                   .mapToLong(Long::longValue)
                   .min()
                   .orElse(1L);
    }

    private List<PalavraChave> ordernarListaDePalavra(final Map<String, Long> mapaPorPalavraEQuantidade, final Double median) {
        return mapaPorPalavraEQuantidade.entrySet().stream()
                   .filter(entry -> isAboveMedian(entry.getValue(), median))
                   .map(PalavraChave::new)
                   .sorted(Comparator.comparing(PalavraChave::getQuantidade).reversed())
                   .collect(Collectors.toList());
    }

    private boolean isAboveMedian(final Long count, final Double median) {
        return count.compareTo(median.longValue()) >= 0;
    }
}
