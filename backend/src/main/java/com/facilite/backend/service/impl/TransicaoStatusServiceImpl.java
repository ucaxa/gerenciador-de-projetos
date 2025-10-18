package com.facilite.backend.service.impl;

import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;
import com.facilite.backend.service.MetricaService;
import com.facilite.backend.service.TransicaoStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TransicaoStatusServiceImpl implements TransicaoStatusService {

    private final MetricaService metricaService;

    // Transições permitidas conforme tabela do PDF
    private final Map<StatusProjeto, Set<StatusProjeto>> transicoesPermitidas = inicializarTransicoes();

    private Map<StatusProjeto, Set<StatusProjeto>> inicializarTransicoes() {
        Map<StatusProjeto, Set<StatusProjeto>> transicoes = new EnumMap<>(StatusProjeto.class);

        // A_INICIAR pode ir para todos os outros status
        transicoes.put(StatusProjeto.A_INICIAR,
                Set.of(StatusProjeto.EM_ANDAMENTO, StatusProjeto.ATRASADO, StatusProjeto.CONCLUIDO));

        // EM_ANDAMENTO pode ir para todos os outros status
        transicoes.put(StatusProjeto.EM_ANDAMENTO,
                Set.of(StatusProjeto.A_INICIAR, StatusProjeto.ATRASADO, StatusProjeto.CONCLUIDO));

        // ATRASADO pode ir para todos os outros status
        transicoes.put(StatusProjeto.ATRASADO,
                Set.of(StatusProjeto.A_INICIAR, StatusProjeto.EM_ANDAMENTO, StatusProjeto.CONCLUIDO));

        // CONCLUIDO pode ir para todos os outros status
        transicoes.put(StatusProjeto.CONCLUIDO,
                Set.of(StatusProjeto.A_INICIAR, StatusProjeto.EM_ANDAMENTO, StatusProjeto.ATRASADO));

        return transicoes;
    }

    @Override
    public void executarTransicao(Projeto projeto, StatusProjeto novoStatus) {
        StatusProjeto statusAtual = projeto.getStatus();

        if (!isTransicaoPermitida(statusAtual, novoStatus)) {
            throw new IllegalArgumentException(getMensagemErroTransicao(statusAtual, novoStatus));
        }

        LocalDate hoje = LocalDate.now();

        switch (statusAtual) {
            case A_INICIAR:
                executarTransicaoDeAIniciar(projeto, novoStatus, hoje);
                break;
            case EM_ANDAMENTO:
                executarTransicaoDeEmAndamento(projeto, novoStatus, hoje);
                break;
            case ATRASADO:
                executarTransicaoDeAtrasado(projeto, novoStatus, hoje);
                break;
            case CONCLUIDO:
                executarTransicaoDeConcluido(projeto, novoStatus, hoje);
                break;
        }

        // Recalcular status após transição (conforme PDF)
        StatusProjeto statusRecalculado = metricaService.calcularStatus(projeto);
        projeto.setStatus(statusRecalculado);

        // Recalcular métricas
        projeto.setDiasAtraso(metricaService.calcularDiasAtraso(projeto));
        projeto.setPercentualTempoRestante(metricaService.calcularPercentualTempoRestante(projeto));
    }

    @Override
    public boolean isTransicaoPermitida(StatusProjeto statusAtual, StatusProjeto novoStatus) {
        return transicoesPermitidas.get(statusAtual).contains(novoStatus);
    }

    @Override
    public String getMensagemErroTransicao(StatusProjeto statusAtual, StatusProjeto novoStatus) {
        return String.format("Transição de %s para %s não é permitida", statusAtual, novoStatus);
    }

    private void executarTransicaoDeAIniciar(Projeto projeto, StatusProjeto novoStatus, LocalDate hoje) {
        switch (novoStatus) {
            case EM_ANDAMENTO:
                // Ação automática: Definir Início Realizado = data de hoje
                projeto.setInicioRealizado(hoje);
                break;
            case ATRASADO:
                // Validação específica
                metricaService.validarTransicaoDeIniciadoParaAtrasado(projeto);
                break;
            case CONCLUIDO:
                // Ação automática: Definir Término Realizado = hoje
                projeto.setTerminoRealizado(hoje);
                break;
        }
    }

    private void executarTransicaoDeEmAndamento(Projeto projeto, StatusProjeto novoStatus, LocalDate hoje) {
        switch (novoStatus) {
            case A_INICIAR:
                // Ação automática: Início Realizado = null
                projeto.setInicioRealizado(null);
                break;
            case ATRASADO:
                // Validação complexa
                metricaService.validarTransicaoDeEmAndamentoParaAtrasado(projeto);
                break;
            case CONCLUIDO:
                // Ação automática: Definir Término Realizado = hoje
                projeto.setTerminoRealizado(hoje);
                break;
        }
    }

    private void executarTransicaoDeAtrasado(Projeto projeto, StatusProjeto novoStatus, LocalDate hoje) {
        switch (novoStatus) {
            case A_INICIAR:
                // Validação específica
                metricaService.validarTransicaoDeAtrasadoParaIniciado(projeto);
                // Ação: remover início realizado se existir
                projeto.setInicioRealizado(null);
                break;
            case EM_ANDAMENTO:
                // Validação específica
                metricaService.validarTransicaoDeAtrasadoParaEmAndamento(projeto);
                break;
            case CONCLUIDO:
                // Ação automática: Definir Término Realizado = hoje
                projeto.setTerminoRealizado(hoje);
                break;
        }
    }

    private void executarTransicaoDeConcluido(Projeto projeto, StatusProjeto novoStatus, LocalDate hoje) {
        switch (novoStatus) {
            case A_INICIAR:
                // Remove ambos: término realizado e início realizado
                projeto.setTerminoRealizado(null);
                projeto.setInicioRealizado(null);
                metricaService.validarTransicaoDeConcluidoParaAIniciar(projeto);
                metricaService.validarNaoAtrasadoAposRemocao(projeto);
                break;
            case EM_ANDAMENTO:
                // Ação automática: Término Realizado = null
                projeto.setTerminoRealizado(null);
                // Validação: não pode ficar Atrasado
                metricaService.validarNaoAtrasadoAposRemocao(projeto);
                break;
            case ATRASADO:
                // Ação automática: Término Realizado = null
                projeto.setTerminoRealizado(null);
                // Validação específica
                metricaService.validarTransicaoDeConcluidoParaAtrasado(projeto);
                break;
        }
    }
}