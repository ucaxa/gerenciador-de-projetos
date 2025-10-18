package com.facilite.backend.service;


import com.facilite.backend.dto.ResponsavelRequest;
import com.facilite.backend.dto.ResponsavelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResponsavelService {


    List<ResponsavelResponse> listarTodos();
    ResponsavelResponse buscarPorId(Long id);
    ResponsavelResponse criarResponsavel(ResponsavelRequest request);
    ResponsavelResponse atualizarResponsavel(Long id, ResponsavelRequest request);
    void excluirResponsavel(Long id);
    boolean existePorEmail(String email);
    Page<ResponsavelResponse> listarPaginado(Pageable pageable);
}
