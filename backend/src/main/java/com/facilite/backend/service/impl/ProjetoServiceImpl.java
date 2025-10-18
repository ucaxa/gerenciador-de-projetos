package com.facilite.backend.service.impl;

import com.facilite.backend.dto.ProjetoRequest;
import com.facilite.backend.dto.ProjetoResponse;
import com.facilite.backend.exception.ProjetoNotFoundException;
import com.facilite.backend.mapper.ProjetoMapper;
import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.Responsavel;
import com.facilite.backend.model.StatusProjeto;
import com.facilite.backend.repository.ProjetoRepository;
import com.facilite.backend.repository.ResponsavelRepository;
import com.facilite.backend.service.MetricaService;
import com.facilite.backend.service.ProjetoService;
import com.facilite.backend.service.TransicaoStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetoServiceImpl implements ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final MetricaService metricaService;
    private final TransicaoStatusService transicaoStatusService;
    private final ProjetoMapper projetoMapper;

    @Override
    public List<ProjetoResponse> listarTodos() {
        return projetoMapper.toResponseList(projetoRepository.findAll());
    }

    @Override
    public ProjetoResponse buscarPorId(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNotFoundException(id));
        return projetoMapper.toResponse(projeto);
    }

    @Override
    public ProjetoResponse criarProjeto(ProjetoRequest request) {
        Set<Responsavel> responsaveis = obterResponsaveisPorIds(request.getResponsavelIds());

        Projeto projeto = projetoMapper.toEntity(request, responsaveis);
        calcularEAtualizarMetricas(projeto);

        Projeto projetoSalvo = projetoRepository.save(projeto);
        return projetoMapper.toResponse(projetoSalvo);
    }

    @Override
    public ProjetoResponse atualizarProjeto(Long id, ProjetoRequest request) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNotFoundException(id));

        Set<Responsavel> responsaveis = obterResponsaveisPorIds(request.getResponsavelIds());

        projetoMapper.updateEntityFromRequest(request, projeto, responsaveis);
        calcularEAtualizarMetricas(projeto);

        Projeto projetoAtualizado = projetoRepository.save(projeto);
        return projetoMapper.toResponse(projetoAtualizado);
    }

    @Override
    public void excluirProjeto(Long id) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNotFoundException(id));
        projetoRepository.delete(projeto);
    }

    @Override
    public List<ProjetoResponse> listarPorStatus(StatusProjeto status) {
        return projetoMapper.toResponseList(projetoRepository.findByStatus(status));
    }

    @Override
    public ProjetoResponse transicionarStatus(Long id, StatusProjeto novoStatus) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new ProjetoNotFoundException(id));

        transicaoStatusService.executarTransicao(projeto, novoStatus);
        calcularEAtualizarMetricas(projeto);

        Projeto projetoAtualizado = projetoRepository.save(projeto);

        // Validar se status final corresponde ao solicitado
        if (projetoAtualizado.getStatus() != novoStatus) {
            throw new IllegalArgumentException(
                    "Transição bloqueada: status final é " + projetoAtualizado.getStatus() +
                            ". Ajuste as datas para realizar esta transição."
            );
        }

        return projetoMapper.toResponse(projetoAtualizado);
    }

    @Override
    public void calcularEAtualizarMetricas(Projeto projeto) {
        projeto.setStatus(metricaService.calcularStatus(projeto));
        projeto.setDiasAtraso(metricaService.calcularDiasAtraso(projeto));
        projeto.setPercentualTempoRestante(metricaService.calcularPercentualTempoRestante(projeto));
    }


    private Set<Responsavel> obterResponsaveisPorIds(Set<Long> responsavelIds) {
        if (responsavelIds == null || responsavelIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(responsavelRepository.findAllById(responsavelIds));
    }

    @Override
    public Page<ProjetoResponse> listarPaginado(Pageable pageable) {
        Page<Projeto> projetosPage = projetoRepository.findAll(pageable);
        return projetosPage.map(projetoMapper::toResponse);
    }
}