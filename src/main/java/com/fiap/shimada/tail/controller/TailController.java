package com.fiap.shimada.tail.controller;

import com.fiap.shimada.tail.controller.form.TailForm;
import com.fiap.shimada.tail.controller.view.TailView;
import com.fiap.shimada.tail.service.TailService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tail")
public class TailController {

    private final TailService tailService;

    public TailController(TailService tailService) {
        this.tailService = tailService;
    }

    @PostMapping
    public TailView gerarPalavarChave(@RequestBody final TailForm form) {
        return TailView.toView(tailService.gerarMapa(form));
    }
}
