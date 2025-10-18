package com.facilite.backend.mapper;

import com.facilite.backend.dto.ProjetoRequest;
import com.facilite.backend.dto.ProjetoResponse;
import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.Responsavel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProjetoMapper {

    private final ResponsavelMapper responsavelMapper;

    public ProjetoMapper(ResponsavelMapper responsavelMapper) {
        this.responsavelMapper = responsavelMapper;
    }

    /**
     * Converte Request para Entidade (sem responsáveis)
     */
    public Projeto toEntity(ProjetoRequest request) {
        if (request == null) {
            return null;
        }

        Projeto projeto = new Projeto();
        projeto.setNome(request.getNome());
        projeto.setInicioPrevisto(request.getInicioPrevisto());
        projeto.setTerminoPrevisto(request.getTerminoPrevisto());
        projeto.setInicioRealizado(request.getInicioRealizado());
        projeto.setTerminoRealizado(request.getTerminoRealizado());
        return projeto;
    }

    /**
     * Converte Request para Entidade com responsáveis
     */
    public Projeto toEntity(ProjetoRequest request, Set<Responsavel> responsaveis) {
        Projeto projeto = toEntity(request);
        if (projeto != null) {
            projeto.setResponsaveis(responsaveis);
        }
        return projeto;
    }

    /**
     * Converte Entidade para Response
     */
    public ProjetoResponse toResponse(Projeto projeto) {
        if (projeto == null) {
            return null;
        }

        ProjetoResponse response = new ProjetoResponse();
        response.setId(projeto.getId());
        response.setNome(projeto.getNome());
        response.setStatus(projeto.getStatus());
        response.setInicioPrevisto(projeto.getInicioPrevisto());
        response.setTerminoPrevisto(projeto.getTerminoPrevisto());
        response.setInicioRealizado(projeto.getInicioRealizado());
        response.setTerminoRealizado(projeto.getTerminoRealizado());
        response.setDiasAtraso(projeto.getDiasAtraso());
        response.setPercentualTempoRestante(projeto.getPercentualTempoRestante());
        response.setCreatedAt(projeto.getCreatedAt());
        response.setUpdatedAt(projeto.getUpdatedAt());

        // Mapear responsáveis
        if (projeto.getResponsaveis() != null) {
            Set<ResponsavelResponse> responsaveisResponse = projeto.getResponsaveis()
                    .stream()
                    .map(responsavelMapper::toResponse)
                    .collect(Collectors.toSet());
            response.setResponsaveis(responsaveisResponse);
        }

        return response;
    }

    /**
     * Atualiza Entidade a partir do Request (sem responsáveis)
     */
    public void updateEntityFromRequest(ProjetoRequest request, Projeto projeto) {
        if (request == null || projeto == null) {
            return;
        }

        projeto.setNome(request.getNome());
        projeto.setInicioPrevisto(request.getInicioPrevisto());
        projeto.setTerminoPrevisto(request.getTerminoPrevisto());
        projeto.setInicioRealizado(request.getInicioRealizado());
        projeto.setTerminoRealizado(request.getTerminoRealizado());
    }

    /**
     * Atualiza Entidade a partir do Request com responsáveis
     */
    public void updateEntityFromRequest(ProjetoRequest request, Projeto projeto, Set<Responsavel> responsaveis) {
        updateEntityFromRequest(request, projeto);
        if (projeto != null) {
            projeto.setResponsaveis(responsaveis);
        }
    }

    /**
     * Converte lista de entidades para lista de responses
     */
    public List<ProjetoResponse> toResponseList(List<Projeto> projetos) {
        if (projetos == null) {
            return List.of();
        }

        return projetos.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
