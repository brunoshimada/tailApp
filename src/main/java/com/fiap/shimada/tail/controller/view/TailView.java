package com.fiap.shimada.tail.controller.view;

import com.fiap.shimada.tail.model.PalavraChave;

import java.util.List;

public class TailView {

    private List<PalavraChave> palavrasChave;

    private TailView(final List<PalavraChave> palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public static TailView toView(List<PalavraChave> gerarPalavrasChave) {
        return new TailView(gerarPalavrasChave);
    }

    public List<PalavraChave> getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(List<PalavraChave> palavrasChave) {
        this.palavrasChave = palavrasChave;
    }
}
