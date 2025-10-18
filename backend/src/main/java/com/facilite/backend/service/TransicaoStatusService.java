package com.facilite.backend.service;

import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;

public interface TransicaoStatusService {

    void executarTransicao(Projeto projeto, StatusProjeto novoStatus);

    boolean isTransicaoPermitida(StatusProjeto statusAtual, StatusProjeto novoStatus);

    String getMensagemErroTransicao(StatusProjeto statusAtual, StatusProjeto novoStatus);
}