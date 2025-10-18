package com.facilite.backend.mapper;

import com.facilite.backend.dto.ResponsavelRequest;
import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.model.Responsavel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResponsavelMapper {

    /**
     * Converte Request para Entidade
     */
    public Responsavel toEntity(ResponsavelRequest request) {
        if (request == null) {
            return null;
        }

        Responsavel responsavel = new Responsavel();
        responsavel.setNome(request.getNome());
        responsavel.setEmail(request.getEmail());
        responsavel.setCargo(request.getCargo());
        return responsavel;
    }

    /**
     * Converte Entidade para Response
     */
    public ResponsavelResponse toResponse(Responsavel responsavel) {
        if (responsavel == null) {
            return null;
        }

        ResponsavelResponse response = new ResponsavelResponse();
        response.setId(responsavel.getId());
        response.setNome(responsavel.getNome());
        response.setEmail(responsavel.getEmail());
        response.setCargo(responsavel.getCargo());
        response.setCreatedAt(responsavel.getCreatedAt());
        response.setUpdatedAt(responsavel.getUpdatedAt());
        return response;
    }

    /**
     * Atualiza Entidade a partir do Request
     */
    public void updateEntityFromRequest(ResponsavelRequest request, Responsavel responsavel) {
        if (request == null || responsavel == null) {
            return;
        }

        responsavel.setNome(request.getNome());
        responsavel.setEmail(request.getEmail());
        responsavel.setCargo(request.getCargo());
    }

    /**
     * Converte lista de entidades para lista de responses
     */
    public List<ResponsavelResponse> toResponseList(List<Responsavel> responsaveis) {
        if (responsaveis == null) {
            return List.of();
        }

        return responsaveis.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}