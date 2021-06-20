package com.fiap.shimada.tail.service.plural;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(Ordered.LOWEST_PRECEDENCE)
@Service
public class ClassificadorPluralCaseS implements IClassificadorPlural {

    @Override
    public boolean isSatisfiedBy(String input) {
        return input.endsWith("s");
    }

    @Override
    public String singular(String input) {
        return input.substring(0, input.length()-1);
    }
}
