package com.fiap.shimada.tail.service.plural;

public interface IClassificadorPlural {

    boolean isSatisfiedBy(final String input);
    String singular(final String input);
}
