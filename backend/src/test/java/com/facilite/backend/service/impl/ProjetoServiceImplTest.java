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
import com.facilite.backend.service.TransicaoStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceImplTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private MetricaService metricaService;

    @Mock
    private TransicaoStatusService transicaoStatusService;

    @Mock
    private ProjetoMapper projetoMapper; // ← ADICIONADO

    @InjectMocks
    private ProjetoServiceImpl projetoService;

    private Projeto projeto;
    private ProjetoRequest projetoRequest;
    private ProjetoResponse projetoResponse;
    private Responsavel responsavel;

    @BeforeEach
    void setUp() {
        responsavel = new Responsavel();
        responsavel.setId(1L);
        responsavel.setNome("João Silva");
        responsavel.setEmail("joao@email.com");
        responsavel.setCargo("Desenvolvedor");

        projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNome("Projeto Teste");
        projeto.setStatus(StatusProjeto.A_INICIAR);
        projeto.setInicioPrevisto(LocalDate.now().minusDays(10));
        projeto.setTerminoPrevisto(LocalDate.now().plusDays(20));
        projeto.setResponsaveis(new HashSet<>(Arrays.asList(responsavel)));

        projetoRequest = new ProjetoRequest();
        projetoRequest.setNome("Projeto Teste");
        projetoRequest.setInicioPrevisto(LocalDate.now().minusDays(10));
        projetoRequest.setTerminoPrevisto(LocalDate.now().plusDays(20));
        projetoRequest.setResponsavelIds(Set.of(1L));

        projetoResponse = new ProjetoResponse();
        projetoResponse.setId(1L);
        projetoResponse.setNome("Projeto Teste");
        projetoResponse.setStatus(StatusProjeto.A_INICIAR);
        projetoResponse.setInicioPrevisto(LocalDate.now().minusDays(10));
        projetoResponse.setTerminoPrevisto(LocalDate.now().plusDays(20));
        projetoResponse.setDiasAtraso(0);
        projetoResponse.setPercentualTempoRestante(100.0);
    }

    @Test
    void listarTodos_DeveRetornarListaDeProjetos() {
        // Arrange
        List<Projeto> projetos = Arrays.asList(projeto);
        List<ProjetoResponse> responses = Arrays.asList(projetoResponse);

        when(projetoRepository.findAll()).thenReturn(projetos);
        when(projetoMapper.toResponseList(projetos)).thenReturn(responses);

        // Act
        List<ProjetoResponse> result = projetoService.listarTodos();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Projeto Teste", result.get(0).getNome());
        verify(projetoRepository, times(1)).findAll();
        verify(projetoMapper, times(1)).toResponseList(projetos);
    }

    @Test
    void buscarPorId_QuandoProjetoExiste_DeveRetornarProjeto() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(projetoMapper.toResponse(projeto)).thenReturn(projetoResponse);

        // Act
        ProjetoResponse result = projetoService.buscarPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Projeto Teste", result.getNome());
        verify(projetoRepository, times(1)).findById(1L);
        verify(projetoMapper, times(1)).toResponse(projeto);
    }

    @Test
    void buscarPorId_QuandoProjetoNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjetoNotFoundException.class, () -> {
            projetoService.buscarPorId(1L);
        });
    }

    @Test
    void criarProjeto_ComDadosValidos_DeveCriarComSucessoERecalcularMetricas() {
        // Arrange
        when(responsavelRepository.findAllById(any())).thenReturn(Arrays.asList(responsavel));
        when(projetoMapper.toEntity(any(ProjetoRequest.class), any(Set.class))).thenReturn(projeto);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(projetoMapper.toResponse(projeto)).thenReturn(projetoResponse);
        when(metricaService.calcularStatus(any(Projeto.class))).thenReturn(StatusProjeto.A_INICIAR);
        when(metricaService.calcularDiasAtraso(any(Projeto.class))).thenReturn(0);
        when(metricaService.calcularPercentualTempoRestante(any(Projeto.class))).thenReturn(100.0);

        // Act
        ProjetoResponse result = projetoService.criarProjeto(projetoRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Projeto Teste", result.getNome());
        verify(projetoRepository, times(1)).save(any(Projeto.class));
        verify(responsavelRepository, times(1)).findAllById(any());
        verify(metricaService, times(1)).calcularStatus(any(Projeto.class));
        verify(metricaService, times(1)).calcularDiasAtraso(any(Projeto.class));
        verify(metricaService, times(1)).calcularPercentualTempoRestante(any(Projeto.class));
        verify(projetoMapper, times(1)).toEntity(any(ProjetoRequest.class), any(Set.class));
        verify(projetoMapper, times(1)).toResponse(projeto);
    }

    @Test
    void criarProjeto_SemResponsaveis_DeveCriarComSucesso() {
        // Arrange
        projetoRequest.setResponsavelIds(null);
        when(projetoMapper.toEntity(any(ProjetoRequest.class), any(Set.class))).thenReturn(projeto);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(projetoMapper.toResponse(projeto)).thenReturn(projetoResponse);
        when(metricaService.calcularStatus(any(Projeto.class))).thenReturn(StatusProjeto.A_INICIAR);
        when(metricaService.calcularDiasAtraso(any(Projeto.class))).thenReturn(0);
        when(metricaService.calcularPercentualTempoRestante(any(Projeto.class))).thenReturn(100.0);

        // Act
        ProjetoResponse result = projetoService.criarProjeto(projetoRequest);

        // Assert
        assertNotNull(result);
        verify(responsavelRepository, never()).findAllById(any());
        verify(projetoMapper, times(1)).toEntity(any(ProjetoRequest.class), eq(new HashSet<>()));
    }

    @Test
    void atualizarProjeto_ComDadosValidos_DeveAtualizarERecalcularMetricas() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(responsavelRepository.findAllById(any())).thenReturn(Arrays.asList(responsavel));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(projetoMapper.toResponse(projeto)).thenReturn(projetoResponse);
        when(metricaService.calcularStatus(any(Projeto.class))).thenReturn(StatusProjeto.EM_ANDAMENTO);
        when(metricaService.calcularDiasAtraso(any(Projeto.class))).thenReturn(2);
        when(metricaService.calcularPercentualTempoRestante(any(Projeto.class))).thenReturn(75.5);

        // Act
        ProjetoResponse result = projetoService.atualizarProjeto(1L, projetoRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Projeto Teste", result.getNome());
        verify(projetoRepository, times(1)).save(any(Projeto.class));
        verify(metricaService, times(1)).calcularStatus(any(Projeto.class));
        verify(metricaService, times(1)).calcularDiasAtraso(any(Projeto.class));
        verify(metricaService, times(1)).calcularPercentualTempoRestante(any(Projeto.class));
        verify(projetoMapper, times(1)).updateEntityFromRequest(eq(projetoRequest), eq(projeto), any(Set.class));
        verify(projetoMapper, times(1)).toResponse(projeto);
    }

    @Test
    void atualizarProjeto_QuandoProjetoNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjetoNotFoundException.class, () -> {
            projetoService.atualizarProjeto(1L, projetoRequest);
        });
    }

    @Test
    void excluirProjeto_QuandoProjetoExiste_DeveExcluirComSucesso() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));

        // Act
        projetoService.excluirProjeto(1L);

        // Assert
        verify(projetoRepository, times(1)).findById(1L);
        verify(projetoRepository, times(1)).delete(projeto); // ← CORRIGIDO
    }

    @Test
    void excluirProjeto_QuandoProjetoNaoExiste_DeveLancarExcecao() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjetoNotFoundException.class, () -> {
            projetoService.excluirProjeto(1L);
        });
    }

    
    @Test
    void transicionarStatus_ComTransicaoValida_DeveTransicionarERecalcularMetricas() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(projetoMapper.toResponse(projeto)).thenReturn(projetoResponse);
        when(metricaService.calcularStatus(any(Projeto.class))).thenReturn(StatusProjeto.EM_ANDAMENTO);
        when(metricaService.calcularDiasAtraso(any(Projeto.class))).thenReturn(0);
        when(metricaService.calcularPercentualTempoRestante(any(Projeto.class))).thenReturn(85.0);

        projeto.setStatus(StatusProjeto.EM_ANDAMENTO);

        // Act
        ProjetoResponse result = projetoService.transicionarStatus(1L, StatusProjeto.EM_ANDAMENTO);

        // Assert
        assertNotNull(result);
        verify(projetoRepository, times(1)).findById(1L);
        verify(transicaoStatusService, times(1)).executarTransicao(any(Projeto.class), eq(StatusProjeto.EM_ANDAMENTO));
        verify(metricaService, times(1)).calcularStatus(any(Projeto.class));
        verify(metricaService, times(1)).calcularDiasAtraso(any(Projeto.class));
        verify(metricaService, times(1)).calcularPercentualTempoRestante(any(Projeto.class));
        verify(projetoMapper, times(1)).toResponse(projeto);
    }

    @Test
    void transicionarStatus_QuandoStatusFinalNaoCorresponde_DeveLancarExcecao() {
        // Arrange
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);
        when(metricaService.calcularStatus(any(Projeto.class))).thenReturn(StatusProjeto.ATRASADO); // Status diferente do solicitado

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projetoService.transicionarStatus(1L, StatusProjeto.EM_ANDAMENTO);
        });

        assertTrue(exception.getMessage().contains("Transição bloqueada"));
        assertTrue(exception.getMessage().contains("status final é ATRASADO"));
    }

    @Test
    void calcularEAtualizarMetricas_DeveChamarServicosDeMetricaEAtualizarProjeto() {
        // Arrange
        when(metricaService.calcularStatus(projeto)).thenReturn(StatusProjeto.EM_ANDAMENTO);
        when(metricaService.calcularDiasAtraso(projeto)).thenReturn(0);
        when(metricaService.calcularPercentualTempoRestante(projeto)).thenReturn(75.5);

        // Act
        projetoService.calcularEAtualizarMetricas(projeto);

        // Assert
        verify(metricaService, times(1)).calcularStatus(projeto);
        verify(metricaService, times(1)).calcularDiasAtraso(projeto);
        verify(metricaService, times(1)).calcularPercentualTempoRestante(projeto);
        assertEquals(StatusProjeto.EM_ANDAMENTO, projeto.getStatus());
        assertEquals(0, projeto.getDiasAtraso());
        assertEquals(75.5, projeto.getPercentualTempoRestante());
    }

    // NOVOS TESTES PARA PAGINAÇÃO

    @Test
    void listarPaginado_DeveRetornarPaginaDeProjetos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        List<Projeto> projetos = Arrays.asList(projeto);
        Page<Projeto> paginaProjetos = new PageImpl<>(projetos, pageable, projetos.size());

        when(projetoRepository.findAll(pageable)).thenReturn(paginaProjetos);
        when(projetoMapper.toResponse(projeto)).thenReturn(projetoResponse);

        // Act
        Page<ProjetoResponse> result = projetoService.listarPaginado(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Projeto Teste", result.getContent().get(0).getNome());
        assertEquals(1, result.getTotalElements());
        verify(projetoRepository, times(1)).findAll(pageable);
        verify(projetoMapper, times(1)).toResponse(projeto);
    }

    @Test
    void listarPaginado_ComPaginaVazia_DeveRetornarPaginaVazia() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Projeto> paginaVazia = Page.empty(pageable);

        when(projetoRepository.findAll(pageable)).thenReturn(paginaVazia);

        // Act
        Page<ProjetoResponse> result = projetoService.listarPaginado(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(projetoRepository, times(1)).findAll(pageable);
        verify(projetoMapper, never()).toResponse(any());
    }

    @Test
    void listarPaginado_ComMultiplasPaginas_DeveRetornarPaginaCorreta() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 2); // Segunda página, 2 itens por página

        Projeto projeto2 = new Projeto();
        projeto2.setId(2L);
        projeto2.setNome("Projeto Teste 2");
        projeto2.setStatus(StatusProjeto.EM_ANDAMENTO);

        ProjetoResponse response2 = new ProjetoResponse();
        response2.setId(2L);
        response2.setNome("Projeto Teste 2");
        response2.setStatus(StatusProjeto.EM_ANDAMENTO);

        List<Projeto> projetosPagina2 = Arrays.asList(projeto2);
        Page<Projeto> paginaProjetos = new PageImpl<>(projetosPagina2, pageable, 5); // 5 no total

        when(projetoRepository.findAll(pageable)).thenReturn(paginaProjetos);
        when(projetoMapper.toResponse(projeto2)).thenReturn(response2);

        // Act
        Page<ProjetoResponse> result = projetoService.listarPaginado(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Projeto Teste 2", result.getContent().get(0).getNome());
        assertEquals(5, result.getTotalElements());
        assertEquals(1, result.getNumber()); // Número da página
        assertEquals(2, result.getSize()); // Tamanho da página
        verify(projetoRepository, times(1)).findAll(pageable);
    }


}