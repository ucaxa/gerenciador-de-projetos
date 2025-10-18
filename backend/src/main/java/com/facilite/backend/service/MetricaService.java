package com.facilite.backend.service;

import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;

public interface MetricaService {

    StatusProjeto calcularStatus(Projeto projeto);

    Integer calcularDiasAtraso(Projeto projeto);

    Double calcularPercentualTempoRestante(Projeto projeto);
    // Validações de transição
    void validarTransicaoDeIniciadoParaAtrasado(Projeto projeto);

    void validarTransicaoDeEmAndamentoParaAtrasado(Projeto projeto);

    void validarTransicaoDeAtrasadoParaIniciado(Projeto projeto);

    void validarTransicaoDeAtrasadoParaEmAndamento(Projeto projeto);

    void validarTransicaoDeConcluidoParaAIniciar(Projeto projeto);

    void validarTransicaoDeConcluidoParaAtrasado(Projeto projeto);

    void validarNaoAtrasadoAposRemocao(Projeto projeto);
}