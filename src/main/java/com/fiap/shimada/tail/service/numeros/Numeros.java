package com.fiap.shimada.tail.service.numeros;

public class Numeros {

    public static String limparNumeros(String input) {
        if (input.replaceAll("[^0-9]+", "").length() == input.length()) {
            return input;
        }

        return input.replaceAll("[^a-zA-Zçãáéíóúâêîôûàèìòù]+", "");
    }

}
