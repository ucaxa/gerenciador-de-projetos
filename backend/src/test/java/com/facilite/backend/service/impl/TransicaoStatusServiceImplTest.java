package com.facilite.backend.service.impl;

import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;
import com.facilite.backend.service.MetricaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransicaoStatusServiceImplTest {

    @Mock
    private MetricaService metricaService;

    @InjectMocks
    private TransicaoStatusServiceImpl transicaoStatusService;

    private Projeto projeto;

    @BeforeEach
    void setUp() {
        projeto = new Projeto();
        projeto.setNome("Projeto Teste");
        projeto.setStatus(StatusProjeto.A_INICIAR);
    }

    // ========== TESTES TRANSIÇÕES PERMITIDAS (TABELA ATUALIZADA) ==========

    @Test
    void isTransicaoPermitida_TransicoesValidas_DeveRetornarTrue() {
        // TODAS as transições são permitidas (conforme correção feita)
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.A_INICIAR, StatusProjeto.EM_ANDAMENTO));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.A_INICIAR, StatusProjeto.ATRASADO));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.A_INICIAR, StatusProjeto.CONCLUIDO));

        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.EM_ANDAMENTO, StatusProjeto.A_INICIAR));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.EM_ANDAMENTO, StatusProjeto.ATRASADO));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.EM_ANDAMENTO, StatusProjeto.CONCLUIDO));

        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.ATRASADO, StatusProjeto.A_INICIAR));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.ATRASADO, StatusProjeto.EM_ANDAMENTO));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.ATRASADO, StatusProjeto.CONCLUIDO));

        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.CONCLUIDO, StatusProjeto.A_INICIAR));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.CONCLUIDO, StatusProjeto.EM_ANDAMENTO));
        assertTrue(transicaoStatusService.isTransicaoPermitida(StatusProjeto.CONCLUIDO, StatusProjeto.ATRASADO));
    }

    @Test
    void isTransicaoPermitida_MesmoStatus_DeveRetornarFalse() {
        assertFalse(transicaoStatusService.isTransicaoPermitida(StatusProjeto.A_INICIAR, StatusProjeto.A_INICIAR));
        assertFalse(transicaoStatusService.isTransicaoPermitida(StatusProjeto.EM_ANDAMENTO, StatusProjeto.EM_ANDAMENTO));
    }

    // ========== TESTES AÇÕES AUTOMÁTICAS ==========

    @Test
    void executarTransicao_DeAIniciarParaEmAndamento_DeveDefinirInicioRealizado() {
        LocalDate hoje = LocalDate.now();
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.EM_ANDAMENTO);
        assertEquals(hoje, projeto.getInicioRealizado());
    }

    @Test
    void executarTransicao_DeAIniciarParaConcluido_DeveDefinirTerminoRealizado() {
        LocalDate hoje = LocalDate.now();
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.CONCLUIDO);
        assertEquals(hoje, projeto.getTerminoRealizado());
    }

    @Test
    void executarTransicao_DeEmAndamentoParaAIniciar_DeveRemoverInicioRealizado() {
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        projeto.setInicioRealizado(LocalDate.now());
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.A_INICIAR);
        assertNull(projeto.getInicioRealizado());
    }

    @Test
    void executarTransicao_DeEmAndamentoParaConcluido_DeveDefinirTerminoRealizado() {
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        LocalDate hoje = LocalDate.now();
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.CONCLUIDO);
        assertEquals(hoje, projeto.getTerminoRealizado());
    }

    @Test
    void executarTransicao_DeAtrasadoParaConcluido_DeveDefinirTerminoRealizado() {
        projeto.setStatus(StatusProjeto.ATRASADO);
        LocalDate hoje = LocalDate.now();
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.CONCLUIDO);
        assertEquals(hoje, projeto.getTerminoRealizado());
    }

    @Test
    void executarTransicao_DeConcluidoParaEmAndamento_DeveRemoverTerminoRealizadoEValidar() {
        projeto.setStatus(StatusProjeto.CONCLUIDO);
        projeto.setTerminoRealizado(LocalDate.now());

        transicaoStatusService.executarTransicao(projeto, StatusProjeto.EM_ANDAMENTO);

        assertNull(projeto.getTerminoRealizado());
        verify(metricaService, times(1)).validarNaoAtrasadoAposRemocao(projeto);
    }

    @Test
    void executarTransicao_DeConcluidoParaAIniciar_DeveRemoverAmbasDatasRealizadasEValidar() {
        projeto.setStatus(StatusProjeto.CONCLUIDO);
        projeto.setInicioRealizado(LocalDate.now().minusDays(5));
        projeto.setTerminoRealizado(LocalDate.now());

        transicaoStatusService.executarTransicao(projeto, StatusProjeto.A_INICIAR);

        assertNull(projeto.getInicioRealizado());
        assertNull(projeto.getTerminoRealizado());
        verify(metricaService, times(1)).validarNaoAtrasadoAposRemocao(projeto);
    }

    @Test
    void executarTransicao_DeConcluidoParaAtrasado_DeveRemoverTerminoRealizadoEValidar() {
        projeto.setStatus(StatusProjeto.CONCLUIDO);
        projeto.setTerminoRealizado(LocalDate.now());

        transicaoStatusService.executarTransicao(projeto, StatusProjeto.ATRASADO);

        assertNull(projeto.getTerminoRealizado());
        verify(metricaService, times(1)).validarTransicaoDeConcluidoParaAtrasado(projeto);
    }

    // ========== TESTES VALIDAÇÕES ESPECÍFICAS ==========

    @Test
    void executarTransicao_DeAIniciarParaAtrasado_DeveChamarValidacaoEspecifica() {
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.ATRASADO);
        verify(metricaService, times(1)).validarTransicaoDeIniciadoParaAtrasado(projeto);
    }

    @Test
    void executarTransicao_DeEmAndamentoParaAtrasado_DeveChamarValidacaoComplexa() {
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.ATRASADO);
        verify(metricaService, times(1)).validarTransicaoDeEmAndamentoParaAtrasado(projeto);
    }

    @Test
    void executarTransicao_DeAtrasadoParaAIniciar_DeveChamarValidacaoERemoverInicioRealizado() {
        projeto.setStatus(StatusProjeto.ATRASADO);
        projeto.setInicioRealizado(LocalDate.now());

        transicaoStatusService.executarTransicao(projeto, StatusProjeto.A_INICIAR);

        verify(metricaService, times(1)).validarTransicaoDeAtrasadoParaIniciado(projeto);
        assertNull(projeto.getInicioRealizado()); // Deve remover início realizado
    }

    @Test
    void executarTransicao_DeAtrasadoParaEmAndamento_DeveChamarValidacaoEspecifica() {
        projeto.setStatus(StatusProjeto.ATRASADO);
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.EM_ANDAMENTO);
        verify(metricaService, times(1)).validarTransicaoDeAtrasadoParaEmAndamento(projeto);
    }

    // ========== TESTES RECÁLCULO DE MÉTRICAS APÓS TRANSIÇÃO ==========

    @Test
    void executarTransicao_DeveRecalcularStatusEMetricasAposTransicao() {
        // Arrange
        when(metricaService.calcularStatus(any(Projeto.class))).thenReturn(StatusProjeto.EM_ANDAMENTO);
        when(metricaService.calcularDiasAtraso(any(Projeto.class))).thenReturn(0);
        when(metricaService.calcularPercentualTempoRestante(any(Projeto.class))).thenReturn(85.5);

        // Act
        transicaoStatusService.executarTransicao(projeto, StatusProjeto.EM_ANDAMENTO);

        // Assert - Verifica que recalcula métricas após transição
        verify(metricaService, times(1)).calcularStatus(any(Projeto.class));
        verify(metricaService, times(1)).calcularDiasAtraso(any(Projeto.class));
        verify(metricaService, times(1)).calcularPercentualTempoRestante(any(Projeto.class));
    }

    // ========== TESTES MENSAGENS DE ERRO ==========

    @Test
    void getMensagemErroTransicao_DeveRetornarMensagemFormatada() {
        String mensagem = transicaoStatusService.getMensagemErroTransicao(
                StatusProjeto.EM_ANDAMENTO, StatusProjeto.ATRASADO);
        assertEquals("Transição de EM_ANDAMENTO para ATRASADO não é permitida", mensagem);
    }

    @Test
    void executarTransicao_TransicaoParaMesmoStatus_DeveLancarExcecao() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transicaoStatusService.executarTransicao(projeto, StatusProjeto.A_INICIAR);
        });
        assertTrue(exception.getMessage().contains("não é permitida"));
    }
}