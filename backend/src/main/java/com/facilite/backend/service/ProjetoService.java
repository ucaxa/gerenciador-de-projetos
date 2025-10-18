package com.facilite.backend.service;



import com.facilite.backend.dto.ProjetoRequest;
import com.facilite.backend.dto.ProjetoResponse;
import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjetoService {

    List<ProjetoResponse> listarTodos();
    ProjetoResponse buscarPorId(Long id);
    ProjetoResponse criarProjeto(ProjetoRequest request);
    ProjetoResponse atualizarProjeto(Long id, ProjetoRequest request);
    void excluirProjeto(Long id);
    List<ProjetoResponse> listarPorStatus(StatusProjeto status);
    ProjetoResponse transicionarStatus(Long id, StatusProjeto novoStatus);
    void calcularEAtualizarMetricas(Projeto projeto);
    Page<ProjetoResponse> listarPaginado(Pageable pageable);
}