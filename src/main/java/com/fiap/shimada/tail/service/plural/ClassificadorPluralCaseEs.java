package com.fiap.shimada.tail.service.plural;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(1)
@Service
public class ClassificadorPluralCaseEs implements IClassificadorPlural{

    @Override
    public boolean isSatisfiedBy(String input) {
        return input.endsWith("es");
    }

    @Override
    public String singular(String input) {
        return input.substring(0, input.length()-2);
    }
}
