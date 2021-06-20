package com.fiap.shimada.tail.service.plural;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(2)
@Service
public class ClassificadorPluralCaseIs implements IClassificadorPlural{

    @Override
    public boolean isSatisfiedBy(String input) {
        return input.endsWith("is");
    }

    @Override
    public String singular(String input) {
        return input.substring(0, input.length()-2).concat("l");
    }
}
