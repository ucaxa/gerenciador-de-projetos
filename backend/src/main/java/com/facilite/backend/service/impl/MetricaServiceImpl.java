package com.facilite.backend.service.impl;

import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;
import com.facilite.backend.service.MetricaService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class MetricaServiceImpl implements MetricaService {

    @Override
    public StatusProjeto calcularStatus(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        // Regra: Concluído - término realizado preenchido
        if (projeto.getTerminoRealizado() != null) {
            return StatusProjeto.CONCLUIDO;
        }

        // Regra: Em andamento
        if (projeto.getInicioRealizado() != null &&
                projeto.getTerminoPrevisto() != null &&
                projeto.getTerminoPrevisto().isAfter(hoje)) {
            return StatusProjeto.EM_ANDAMENTO;
        }

        // Regra: Atrasado
        boolean atrasoInicio = projeto.getInicioPrevisto() != null &&
                projeto.getInicioPrevisto().isBefore(hoje) &&
                projeto.getInicioRealizado() == null;

        boolean atrasoTermino = projeto.getTerminoPrevisto() != null &&
                projeto.getTerminoPrevisto().isBefore(hoje) &&
                projeto.getTerminoRealizado() == null;

        if (atrasoInicio || atrasoTermino) {
            return StatusProjeto.ATRASADO;
        }

        // Regra: A iniciar (default)
        return StatusProjeto.A_INICIAR;
    }

    @Override
    public Integer calcularDiasAtraso(Projeto projeto) {
        // Só calcula atraso se:
        // 1. NÃO foi finalizado (terminoRealizado == null)
        // 2. Tem data prevista
        // 3. Está EM_ANDAMENTO (não A_INICIAR)
        if (projeto.getTerminoRealizado() != null ||
                projeto.getTerminoPrevisto() == null) {
            return 0;
        }

        LocalDate hoje = LocalDate.now();

        // Só calcula se está atrasado E não está apenas "A INICIAR"
        if (projeto.getTerminoPrevisto().isBefore(hoje) &&
                projeto.getStatus() != StatusProjeto.A_INICIAR) {
            return (int) ChronoUnit.DAYS.between(projeto.getTerminoPrevisto(), hoje);
        }

        return 0;
    }

    @Override
    public Double calcularPercentualTempoRestante(Projeto projeto) {
        // Conforme PDF: Em A iniciar (sem datas) retornar 0, Em Concluído retornar 0
        if (projeto.getInicioPrevisto() == null ||
                projeto.getTerminoPrevisto() == null ||
                projeto.getStatus() == StatusProjeto.A_INICIAR ||
                projeto.getStatus() == StatusProjeto.CONCLUIDO) {
            return 0.0;
        }

        long totalDias = ChronoUnit.DAYS.between(
                projeto.getInicioPrevisto(),
                projeto.getTerminoPrevisto()
        );

        if (totalDias <= 0) {
            return 0.0;
        }

        LocalDate hoje = LocalDate.now();
        long diasUsados = ChronoUnit.DAYS.between(projeto.getInicioPrevisto(), hoje);
        long diasRestantes = totalDias - diasUsados;

        // Se hoje > término previsto e não Concluído, retornar 0
        if (diasRestantes < 0) {
            return 0.0;
        }

        return Math.min(100.0, Math.max(0.0, (diasRestantes * 100.0) / totalDias));
    }

    // ========== VALIDAÇÕES DE TRANSIÇÃO ==========

    @Override
    public void validarTransicaoDeIniciadoParaAtrasado(Projeto projeto) {
        LocalDate hoje = LocalDate.now();
        if (projeto.getInicioPrevisto() != null &&
                projeto.getInicioPrevisto().isAfter(hoje)) {
            throw new IllegalArgumentException(
                    "Não é possível marcar como Atrasado antes da data de início prevista"
            );
        }
    }

    @Override
    public void validarTransicaoDeEmAndamentoParaAtrasado(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        boolean inicioPrevistoValido = projeto.getInicioPrevisto() != null &&
                projeto.getInicioPrevisto().isBefore(hoje);
        boolean terminoPrevistoValido = projeto.getTerminoPrevisto() != null &&
                projeto.getTerminoPrevisto().isBefore(hoje);
        boolean podeRemoverInicioRealizado = projeto.getInicioRealizado() != null;
        boolean podeAjustarDatas = inicioPrevistoValido || terminoPrevistoValido;

        if (!podeRemoverInicioRealizado && !podeAjustarDatas) {
            throw new IllegalArgumentException(
                    "Para transicionar para Atrasado: ou remova Início Realizado " +
                            "(volta a 'não iniciado' com atraso se cabível) ou ajuste Início/Término Previsto para data < hoje"
            );
        }
    }

    @Override
    public void validarTransicaoDeAtrasadoParaIniciado(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        // Deve remover início realizado
        if (projeto.getInicioRealizado() != null) {
            throw new IllegalArgumentException(
                    "Remova Início Realizado para transicionar para A Iniciar"
            );
        }

        // Datas previstas devem ser MAIORES que hoje
        if (projeto.getInicioPrevisto() != null && projeto.getInicioPrevisto().isBefore(hoje)) {
            throw new IllegalArgumentException(
                    "Ajuste Início Previsto para data > hoje"
            );
        }
        if (projeto.getTerminoPrevisto() != null && projeto.getTerminoPrevisto().isBefore(hoje)) {
            throw new IllegalArgumentException(
                    "Ajuste Término Previsto para data > hoje"
            );
        }
    }

    @Override
    public void validarTransicaoDeAtrasadoParaEmAndamento(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        if (projeto.getInicioPrevisto() == null || projeto.getTerminoPrevisto() == null) {
            throw new IllegalArgumentException("Datas previstas devem estar preenchidas");
        }

        // AMBAS as datas devem ser MAIORES que hoje
        if (projeto.getInicioPrevisto().isBefore(hoje) ||
                projeto.getTerminoPrevisto().isBefore(hoje)) {
            throw new IllegalArgumentException(
                    "Ajuste Início/Término Previsto para data > hoje para transicionar para Em Andamento"
            );
        }
    }

    @Override
    public void validarTransicaoDeConcluidoParaAIniciar(Projeto projeto) {
        LocalDate hoje = LocalDate.now();

        // Deve remover término realizado
        if (projeto.getTerminoRealizado() == null) {
            throw new IllegalArgumentException(
                    "Término Realizado já está vazio"
            );
        }

        // Datas previstas devem ser MAIORES que hoje
        if (projeto.getInicioPrevisto() != null && projeto.getInicioPrevisto().isBefore(hoje)) {
            throw new IllegalArgumentException(
                    "Ajuste Início Previsto para data > hoje"
            );
        }
        if (projeto.getTerminoPrevisto() != null && projeto.getTerminoPrevisto().isBefore(hoje)) {
            throw new IllegalArgumentException(
                    "Ajuste Término Previsto para data > hoje"
            );
        }
    }

    @Override
    public void validarTransicaoDeConcluidoParaAtrasado(Projeto projeto) {
        // Simula remoção do término realizado e verifica se fica Atrasado
        Projeto projetoSemTermino = new Projeto();
        projetoSemTermino.setInicioPrevisto(projeto.getInicioPrevisto());
        projetoSemTermino.setTerminoPrevisto(projeto.getTerminoPrevisto());
        projetoSemTermino.setInicioRealizado(projeto.getInicioRealizado());
        projetoSemTermino.setTerminoRealizado(null); // Remove término realizado

        StatusProjeto statusAposRemocao = calcularStatus(projetoSemTermino);
        if (statusAposRemocao != StatusProjeto.ATRASADO) {
            throw new IllegalArgumentException(
                    "Só permitir se, ao remover término realizado, as regras classificarem como Atrasado. " +
                            "Ajuste as datas primeiro."
            );
        }
    }

    @Override
    public void validarNaoAtrasadoAposRemocao(Projeto projeto) {
        StatusProjeto statusAposRemocao = calcularStatus(projeto);
        if (statusAposRemocao == StatusProjeto.ATRASADO) {
            throw new IllegalArgumentException(
                    "Ao remover término realizado, o projeto ficaria Atrasado. Ajuste as datas primeiro."
            );
        }
    }
}