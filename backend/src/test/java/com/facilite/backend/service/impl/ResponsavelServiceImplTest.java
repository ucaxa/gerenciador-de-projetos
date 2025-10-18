package com.facilite.backend.service.impl;

import com.facilite.backend.dto.ResponsavelRequest;
import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.exception.EmailAlreadyExistsException;
import com.facilite.backend.exception.ResponsavelNotFoundException;
import com.facilite.backend.mapper.ResponsavelMapper;
import com.facilite.backend.model.Responsavel;
import com.facilite.backend.repository.ResponsavelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResponsavelServiceImplTest {

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private ResponsavelMapper responsavelMapper;

    @InjectMocks
    private ResponsavelServiceImpl responsavelService;

    private Responsavel responsavel;
    private ResponsavelRequest responsavelRequest;
    private ResponsavelResponse responsavelResponse;

    @BeforeEach
    void setUp() {
        responsavel = new Responsavel();
        responsavel.setId(1L);
        responsavel.setNome("João Silva");
        responsavel.setEmail("joao@email.com");
        responsavel.setCargo("Desenvolvedor");
        responsavel.setCreatedAt(LocalDateTime.now());
        responsavel.setUpdatedAt(LocalDateTime.now());

        responsavelRequest = new ResponsavelRequest();
        responsavelRequest.setNome("João Silva");
        responsavelRequest.setEmail("joao@email.com");
        responsavelRequest.setCargo("Desenvolvedor");

        responsavelResponse = new ResponsavelResponse();
        responsavelResponse.setId(1L);
        responsavelResponse.setNome("João Silva");
        responsavelResponse.setEmail("joao@email.com");
        responsavelResponse.setCargo("Desenvolvedor");
    }

    // ... (mantenha todos os testes anteriores que corrigimos)

    @Test
    void listarTodos_DeveRetornarListaDeResponsaveis() {
        // Arrange
        List<Responsavel> responsaveis = Arrays.asList(responsavel);
        List<ResponsavelResponse> responses = Arrays.asList(responsavelResponse);

        when(responsavelRepository.findAll()).thenReturn(responsaveis);
        when(responsavelMapper.toResponseList(responsaveis)).thenReturn(responses);

        // Act
        List<ResponsavelResponse> result = responsavelService.listarTodos();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("João Silva", result.get(0).getNome());
        verify(responsavelRepository, times(1)).findAll();
        verify(responsavelMapper, times(1)).toResponseList(responsaveis);
    }

    @Test
    void buscarPorId_QuandoResponsavelExiste_DeveRetornarResponsavel() {
        // Arrange
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelMapper.toResponse(responsavel)).thenReturn(responsavelResponse);

        // Act
        ResponsavelResponse result = responsavelService.buscarPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getNome());
        verify(responsavelRepository, times(1)).findById(1L);
        verify(responsavelMapper, times(1)).toResponse(responsavel);
    }

    @Test
    void buscarPorId_QuandoResponsavelNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(responsavelRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponsavelNotFoundException.class, () -> {
            responsavelService.buscarPorId(1L);
        });
    }

    @Test
    void criarResponsavel_ComEmailUnico_DeveCriarComSucesso() {
        // Arrange
        when(responsavelRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(responsavelMapper.toEntity(responsavelRequest)).thenReturn(responsavel);
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);
        when(responsavelMapper.toResponse(responsavel)).thenReturn(responsavelResponse);

        // Act
        ResponsavelResponse result = responsavelService.criarResponsavel(responsavelRequest);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getNome());
        assertEquals("joao@email.com", result.getEmail());
        verify(responsavelRepository, times(1)).save(any(Responsavel.class));
        verify(responsavelMapper, times(1)).toEntity(responsavelRequest);
        verify(responsavelMapper, times(1)).toResponse(responsavel);
    }

    @Test
    void criarResponsavel_ComEmailExistente_DeveLancarExcecao() {
        // Arrange
        when(responsavelRepository.existsByEmail("joao@email.com")).thenReturn(true);

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            responsavelService.criarResponsavel(responsavelRequest);
        });
    }

    @Test
    void atualizarResponsavel_ComDadosValidos_DeveAtualizarComSucesso() {
        // Arrange
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.existsByEmail("joao.novo@email.com")).thenReturn(false);
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);
        when(responsavelMapper.toResponse(responsavel)).thenReturn(responsavelResponse);

        responsavelRequest.setEmail("joao.novo@email.com");

        // Act
        ResponsavelResponse result = responsavelService.atualizarResponsavel(1L, responsavelRequest);

        // Assert
        assertNotNull(result);
        verify(responsavelRepository, times(1)).save(any(Responsavel.class));
        verify(responsavelMapper, times(1)).updateEntityFromRequest(responsavelRequest, responsavel);
        verify(responsavelMapper, times(1)).toResponse(responsavel);
    }

    @Test
    void atualizarResponsavel_ComEmailExistente_DeveLancarExcecao() {
        // Arrange
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.existsByEmail("email.existente@email.com")).thenReturn(true);

        responsavelRequest.setEmail("email.existente@email.com");

        // Act & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> {
            responsavelService.atualizarResponsavel(1L, responsavelRequest);
        });
    }

    @Test
    void excluirResponsavel_QuandoResponsavelExiste_DeveExcluirComSucesso() {
        // Arrange
        when(responsavelRepository.findById(1L)).thenReturn(Optional.of(responsavel));

        // Act
        responsavelService.excluirResponsavel(1L);

        // Assert
        verify(responsavelRepository, times(1)).findById(1L);
        verify(responsavelRepository, times(1)).delete(responsavel);
    }

    @Test
    void excluirResponsavel_QuandoResponsavelNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(responsavelRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponsavelNotFoundException.class, () -> {
            responsavelService.excluirResponsavel(1L);
        });
    }

    @Test
    void existePorEmail_QuandoEmailExiste_DeveRetornarTrue() {
        // Arrange
        when(responsavelRepository.existsByEmail("joao@email.com")).thenReturn(true);

        // Act
        boolean result = responsavelService.existePorEmail("joao@email.com");

        // Assert
        assertTrue(result);
        verify(responsavelRepository, times(1)).existsByEmail("joao@email.com");
    }

    @Test
    void existePorEmail_QuandoEmailNaoExiste_DeveRetornarFalse() {
        // Arrange
        when(responsavelRepository.existsByEmail("inexistente@email.com")).thenReturn(false);

        // Act
        boolean result = responsavelService.existePorEmail("inexistente@email.com");

        // Assert
        assertFalse(result);
    }

    // NOVOS TESTES PARA PAGINAÇÃO

    @Test
    void listarPaginado_DeveRetornarPaginaDeResponsaveis() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        List<Responsavel> responsaveis = Arrays.asList(responsavel);
        Page<Responsavel> paginaResponsaveis = new PageImpl<>(responsaveis, pageable, responsaveis.size());

        when(responsavelRepository.findAll(pageable)).thenReturn(paginaResponsaveis);
        when(responsavelMapper.toResponse(responsavel)).thenReturn(responsavelResponse);

        // Act
        Page<ResponsavelResponse> result = responsavelService.listarPaginado(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("João Silva", result.getContent().get(0).getNome());
        assertEquals(1, result.getTotalElements());
        verify(responsavelRepository, times(1)).findAll(pageable);
        verify(responsavelMapper, times(1)).toResponse(responsavel);
    }

    @Test
    void listarPaginado_ComPaginaVazia_DeveRetornarPaginaVazia() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Responsavel> paginaVazia = Page.empty(pageable);

        when(responsavelRepository.findAll(pageable)).thenReturn(paginaVazia);

        // Act
        Page<ResponsavelResponse> result = responsavelService.listarPaginado(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(responsavelRepository, times(1)).findAll(pageable);
        verify(responsavelMapper, never()).toResponse(any());
    }

    @Test
    void listarPaginado_ComMultiplasPaginas_DeveRetornarPaginaCorreta() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2); // Segunda página, 2 itens por página

        Responsavel responsavel2 = new Responsavel();
        responsavel2.setId(2L);
        responsavel2.setNome("Maria Santos");
        responsavel2.setEmail("maria@email.com");

        ResponsavelResponse response2 = new ResponsavelResponse();
        response2.setId(2L);
        response2.setNome("Maria Santos");
        response2.setEmail("maria@email.com");

        List<Responsavel> responsaveisPagina2 = Arrays.asList(responsavel2);
        Page<Responsavel> paginaResponsaveis = new PageImpl<>(responsaveisPagina2, pageable, 5); // 5 no total

        when(responsavelRepository.findAll(pageable)).thenReturn(paginaResponsaveis);
        when(responsavelMapper.toResponse(responsavel2)).thenReturn(response2);

        // Act
        Page<ResponsavelResponse> result = responsavelService.listarPaginado(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Maria Santos", result.getContent().get(0).getNome());
        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getNumber()); // Número da página
        assertEquals(2, result.getSize()); // Tamanho da página
        verify(responsavelRepository, times(1)).findAll(pageable);
    }
}