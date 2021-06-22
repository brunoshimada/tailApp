package com.fiap.shimada.tail.service.split;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class Splits {

    private static final List<String> caracteresDeSeparacaoDePalavra = List.of(
        "!",
        ",",
        ".",
        ";",
        ":",
        "?",
        " "
    );

    public static String obterCaracteresDeSeparacao() {
        final String pattern = "[{0}]";
        final String part = String.join("", caracteresDeSeparacaoDePalavra);

        return MessageFormat.format(pattern, part);
    }

    public static String obterCaracteresDeSeparacaoEntidade() {
        final String pattern = "[{0}]";
        return MessageFormat.format(pattern, " ");
    }

    public static boolean isSplit(final String input) {
        return caracteresDeSeparacaoDePalavra.contains(input);
    }


}
