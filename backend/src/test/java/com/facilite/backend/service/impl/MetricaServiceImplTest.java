package com.facilite.backend.service.impl;

import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MetricaServiceImplTest {

    @InjectMocks
    private MetricaServiceImpl metricaService;

    private Projeto projeto;

    @BeforeEach
    void setUp() {
        projeto = new Projeto();
        projeto.setNome("Projeto Teste");
    }

    // ========== TESTES CALCULAR STATUS ==========

    @Test
    void calcularStatus_ProjetoConcluido_DeveRetornarConcluido() {
        projeto.setTerminoRealizado(LocalDate.now());
        StatusProjeto result = metricaService.calcularStatus(projeto);
        assertEquals(StatusProjeto.CONCLUIDO, result);
    }

    @Test
    void calcularStatus_ProjetoEmAndamento_DeveRetornarEmAndamento() {
        projeto.setInicioRealizado(LocalDate.now().minusDays(5));
        projeto.setTerminoPrevisto(LocalDate.now().plusDays(10));
        projeto.setTerminoRealizado(null);
        StatusProjeto result = metricaService.calcularStatus(projeto);
        assertEquals(StatusProjeto.EM_ANDAMENTO, result);
    }

    @Test
    void calcularStatus_ProjetoAtrasadoInicio_DeveRetornarAtrasado() {
        projeto.setInicioPrevisto(LocalDate.now().minusDays(5));
        projeto.setInicioRealizado(null);
        StatusProjeto result = metricaService.calcularStatus(projeto);
        assertEquals(StatusProjeto.ATRASADO, result);
    }

    @Test
    void calcularStatus_ProjetoAtrasadoTermino_DeveRetornarAtrasado() {
        projeto.setInicioRealizado(LocalDate.now().minusDays(10));
        projeto.setTerminoPrevisto(LocalDate.now().minusDays(1));
        projeto.setTerminoRealizado(null);
        StatusProjeto result = metricaService.calcularStatus(projeto);
        assertEquals(StatusProjeto.ATRASADO, result);
    }

    @Test
    void calcularStatus_ProjetoAIniciar_DeveRetornarAIniciar() {
        projeto.setInicioPrevisto(LocalDate.now().plusDays(5));
        projeto.setTerminoPrevisto(LocalDate.now().plusDays(20));
        StatusProjeto result = metricaService.calcularStatus(projeto);
        assertEquals(StatusProjeto.A_INICIAR, result);
    }

    @Test
    void calcularStatus_ProjetoAIniciarSemDatas_DeveRetornarAIniciar() {
        StatusProjeto result = metricaService.calcularStatus(projeto);
        assertEquals(StatusProjeto.A_INICIAR, result);
    }

    // ========== TESTES CALCULAR DIAS ATRASO ==========

    @Test
    void calcularDiasAtraso_ProjetoConcluido_DeveRetornarZero() {
        projeto.setTerminoRealizado(LocalDate.now());
        Integer result = metricaService.calcularDiasAtraso(projeto);
        assertEquals(0, result);
    }

    @Test
    void calcularDiasAtraso_ProjetoEmDia_DeveRetornarZero() {
        projeto.setTerminoPrevisto(LocalDate.now().plusDays(5));
        projeto.setTerminoRealizado(null);
        Integer result = metricaService.calcularDiasAtraso(projeto);
        assertEquals(0, result);
    }

    @Test
    void calcularDiasAtraso_ProjetoAtrasado_DeveRetornarDiasAtraso() {
        // Arrange - datas relativas à data atual
        LocalDate hoje = LocalDate.now();
        LocalDate terminoPrevisto = hoje.minusDays(5);

        projeto.setTerminoPrevisto(terminoPrevisto);
        projeto.setTerminoRealizado(null);
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);

        // Act
        Integer result = metricaService.calcularDiasAtraso(projeto);

        // Assert
        assertEquals(5, result);
    }

    @Test
    void calcularDiasAtraso_ProjetoAIniciarSemTerminoPrevisto_DeveRetornarZero() {
        projeto.setStatus(StatusProjeto.A_INICIAR);
        Integer result = metricaService.calcularDiasAtraso(projeto);
        assertEquals(0, result);
    }

    // ========== TESTES CALCULAR PERCENTUAL TEMPO RESTANTE ==========

    @Test
    void calcularPercentualTempoRestante_ProjetoConcluido_DeveRetornarZero() {
        projeto.setStatus(StatusProjeto.CONCLUIDO);
        Double result = metricaService.calcularPercentualTempoRestante(projeto);
        assertEquals(0.0, result);
    }

    @Test
    void calcularPercentualTempoRestante_ProjetoAIniciar_DeveRetornarZero() {
        projeto.setStatus(StatusProjeto.A_INICIAR);
        Double result = metricaService.calcularPercentualTempoRestante(projeto);
        assertEquals(0.0, result);
    }

    @Test
    void calcularPercentualTempoRestante_ProjetoComDatas_DeveCalcularPercentual() {
        LocalDate inicio = LocalDate.now().minusDays(10);
        LocalDate termino = LocalDate.now().plusDays(10);
        projeto.setInicioPrevisto(inicio);
        projeto.setTerminoPrevisto(termino);
        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);

        Double result = metricaService.calcularPercentualTempoRestante(projeto);
        assertTrue(result > 0 && result <= 100);
    }

    @Test
    void calcularPercentualTempoRestante_TotalDiasZero_DeveRetornarZero() {
        LocalDate hoje = LocalDate.now();
        projeto.setInicioPrevisto(hoje);
        projeto.setTerminoPrevisto(hoje);
        Double result = metricaService.calcularPercentualTempoRestante(projeto);
        assertEquals(0.0, result);
    }

    @Test
    void calcularPercentualTempoRestante_TempoExcedido_DeveRetornarZero() {
        LocalDate inicio = LocalDate.now().minusDays(20);
        LocalDate termino = LocalDate.now().minusDays(10);
        projeto.setInicioPrevisto(inicio);
        projeto.setTerminoPrevisto(termino);
        Double result = metricaService.calcularPercentualTempoRestante(projeto);
        assertEquals(0.0, result);
    }

    // ========== TESTES VALIDAÇÕES DE TRANSIÇÃO ==========

    @Test
    void validarTransicaoDeIniciadoParaAtrasado_ComInicioPrevistoFuturo_DeveLancarExcecao() {
        projeto.setInicioPrevisto(LocalDate.now().plusDays(5));
        assertThrows(IllegalArgumentException.class, () -> {
            metricaService.validarTransicaoDeIniciadoParaAtrasado(projeto);
        });
    }

    @Test
    void validarTransicaoDeIniciadoParaAtrasado_ComInicioPrevistoPassado_DevePermitir() {
        projeto.setInicioPrevisto(LocalDate.now().minusDays(5));
        assertDoesNotThrow(() -> {
            metricaService.validarTransicaoDeIniciadoParaAtrasado(projeto);
        });
    }

    @Test
    void validarTransicaoDeEmAndamentoParaAtrasado_SemOpcoesValidas_DeveLancarExcecao() {
        projeto.setInicioRealizado(null);
        projeto.setInicioPrevisto(LocalDate.now().plusDays(5));
        projeto.setTerminoPrevisto(LocalDate.now().plusDays(10));
        assertThrows(IllegalArgumentException.class, () -> {
            metricaService.validarTransicaoDeEmAndamentoParaAtrasado(projeto);
        });
    }

    @Test
    void validarTransicaoDeAtrasadoParaIniciado_ComInicioRealizado_DeveLancarExcecao() {
        projeto.setInicioRealizado(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () -> {
            metricaService.validarTransicaoDeAtrasadoParaIniciado(projeto);
        });
    }

    @Test
    void validarTransicaoDeAtrasadoParaEmAndamento_ComDatasPassadas_DeveLancarExcecao() {
        projeto.setInicioPrevisto(LocalDate.now().minusDays(5));
        projeto.setTerminoPrevisto(LocalDate.now().minusDays(1));
        assertThrows(IllegalArgumentException.class, () -> {
            metricaService.validarTransicaoDeAtrasadoParaEmAndamento(projeto);
        });
    }

    @Test
    void validarNaoAtrasadoAposRemocao_QuandoFicariaAtrasado_DeveLancarExcecao() {
        projeto.setInicioPrevisto(LocalDate.now().minusDays(10));
        projeto.setTerminoPrevisto(LocalDate.now().minusDays(1));
        projeto.setInicioRealizado(null);
        assertThrows(IllegalArgumentException.class, () -> {
            metricaService.validarNaoAtrasadoAposRemocao(projeto);
        });
    }

    @Test
    void validarNaoAtrasadoAposRemocao_QuandoNaoFicariaAtrasado_DevePermitir() {
        projeto.setInicioPrevisto(LocalDate.now().plusDays(1));
        projeto.setTerminoPrevisto(LocalDate.now().plusDays(10));
        projeto.setInicioRealizado(LocalDate.now());
        assertDoesNotThrow(() -> {
            metricaService.validarNaoAtrasadoAposRemocao(projeto);
        });
    }
}