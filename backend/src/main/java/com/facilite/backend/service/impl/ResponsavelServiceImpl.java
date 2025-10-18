package com.facilite.backend.service.impl;

import com.facilite.backend.dto.ResponsavelRequest;
import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.exception.EmailAlreadyExistsException;
import com.facilite.backend.exception.ResponsavelNotFoundException;
import com.facilite.backend.mapper.ResponsavelMapper;
import com.facilite.backend.model.Responsavel;
import com.facilite.backend.repository.ResponsavelRepository;
import com.facilite.backend.service.ResponsavelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResponsavelServiceImpl implements ResponsavelService {

    private final ResponsavelRepository responsavelRepository;
    private final ResponsavelMapper responsavelMapper;

    @Override
    public List<ResponsavelResponse> listarTodos() {
        return responsavelMapper.toResponseList(responsavelRepository.findAll());
    }

    @Override
    public ResponsavelResponse buscarPorId(Long id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new ResponsavelNotFoundException(id));
        return responsavelMapper.toResponse(responsavel);
    }

    @Override
    public ResponsavelResponse criarResponsavel(ResponsavelRequest request) {
        if (existePorEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        Responsavel responsavel = responsavelMapper.toEntity(request);
        Responsavel responsavelSalvo = responsavelRepository.save(responsavel);
        return responsavelMapper.toResponse(responsavelSalvo);
    }

    @Override
    public ResponsavelResponse atualizarResponsavel(Long id, ResponsavelRequest request) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new ResponsavelNotFoundException(id));

        // Verificar se email já existe (para outro responsável)
        if (!responsavel.getEmail().equals(request.getEmail()) &&
                existePorEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        responsavelMapper.updateEntityFromRequest(request, responsavel);
        Responsavel responsavelAtualizado = responsavelRepository.save(responsavel);
        return responsavelMapper.toResponse(responsavelAtualizado);
    }

    @Override
    public void excluirResponsavel(Long id) {
        Responsavel responsavel = responsavelRepository.findById(id)
                .orElseThrow(() -> new ResponsavelNotFoundException(id));
        responsavelRepository.delete(responsavel);
    }

    @Override
    public boolean existePorEmail(String email) {
        return responsavelRepository.existsByEmail(email);
    }


    @Override
    public Page<ResponsavelResponse> listarPaginado(Pageable pageable) {
        Page<Responsavel> responsaveisPage = responsavelRepository.findAll(pageable);
        return responsaveisPage.map(responsavelMapper::toResponse);
    }
}