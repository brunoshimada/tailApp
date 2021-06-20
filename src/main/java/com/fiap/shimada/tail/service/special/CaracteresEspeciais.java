package com.fiap.shimada.tail.service.special;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CaracteresEspeciais {

    private static final List<String> CARACTERES_ESPECIAIS = List.of(
        "@",
        "#",
        "$",
        "%",
        "*",
        "(",
        ")",
        "-",
        "_",
        "=",
        "+",
        "'",
        "\"",
        "`",
        "'",
        "[",
        "{",
        "^",
        "~",
        "}",
        "]",
        "<",
        ">",
        "/",
        "|",
        "\\");

    public static String obterCaracteresEspeciais() {
        return "[@#$%*()_=+'\"`'\\[\\{^~\\}\\]<>/|]+";
    }

    public static String limparCaracteresEspeciais(final String input) {
        return input.replaceAll("[^0-9a-zA-Zçãáéíóúâêîôûàèìòù]+", "");
    }

    public static String limparCaracteresEspeciaisComposto(final String input) {
        return input.replaceAll(CaracteresEspeciais.obterCaracteresEspeciais(), "");
    }

}