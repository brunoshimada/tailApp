package com.fiap.shimada.tail.service.entidades;

import com.fiap.shimada.tail.controller.form.TailForm;
import com.fiap.shimada.tail.model.PalavraChave;
import com.fiap.shimada.tail.service.numeros.Numeros;
import com.fiap.shimada.tail.service.special.CaracteresEspeciais;
import com.fiap.shimada.tail.service.split.Splits;
import com.fiap.shimada.tail.service.stopwords.StopWords;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class TailEntidadesService {

    public List<PalavraChave> gerarPalavrasEntidades(TailForm form) {
        final List<String> palavras = prepararTexto(form.getInputText());

        final List<String> entidades = new ArrayList<>();

        List<String> aux = new ArrayList<>();
        for (int i = 0; i < palavras.size(); i++) {
            int pos = i;
            String palavraAtual = palavras.get(i);

            if (palavraAtual.length() == 1) {
                continue;
            }

            final char c = palavraAtual.charAt(palavraAtual.length() - 1);
            if (String.valueOf(c).equals(".")) {
                if (aux.isEmpty() && Character.isUpperCase(palavraAtual.charAt(0))) {
                    aux.add(cleanBeforeInsert(palavraAtual));
                }
                entidades.add(String.join(" ", aux));
                aux.clear();
                continue;
            }

            if (!Character.isUpperCase(palavraAtual.charAt(0)) && aux.isEmpty()) {
                continue;
            }

            if (!Character.isUpperCase(palavraAtual.charAt(0)) && !aux.isEmpty() && palavraAtual.length() > 4) {
                entidades.add(String.join(" ", aux));
                aux.clear();
                continue;
            }

            if (!Character.isUpperCase(palavraAtual.charAt(0)) && !aux.isEmpty()) {
                // olho se a proxima pode ser
                int nextAhead = pos + 1;
                if (nextAhead < palavras.size()) {
                    final String nextAheadPalavra = palavras.get(nextAhead);
                    if (Character.isUpperCase(nextAheadPalavra.charAt(0))) {
                        aux.add(cleanBeforeInsert(palavraAtual));
                        continue;
                    } else {
                        if (aux.size() == 1) {
                            aux.clear();
                            continue;
                        }

                        entidades.add(String.join(" ", aux));
                        aux.clear();
                        continue;
                    }
                }
            }

            aux.add(cleanBeforeInsert(palavraAtual));
        }

        return entidades.stream()
                   .filter(entidade -> !StopWords.isStopWord(entidade))
                   .filter(entidade -> entidade.length() > 1)
                   .collect(Collectors.groupingBy(Function.identity()))
                   .keySet()
                   .stream()
                   .map(entry -> new PalavraChave(entry, 1L))
                   .collect(Collectors.toList());
    }

    private List<String> prepararTexto(final String textoEntrada) {
        final Predicate<String> stringValida = s -> !s.isEmpty();

        return Arrays.stream(textoEntrada.split(Splits.obterCaracteresDeSeparacaoEntidade()))
                   .filter(stringValida)
                   .map(CaracteresEspeciais::limparCaracteresEspeciaisComposto)
                   .filter(stringValida)
                   .collect(Collectors.toList());
    }

    private String cleanBeforeInsert(final String string) {
        return CaracteresEspeciais.limparCaracteresEspeciais(string);
    }
}
