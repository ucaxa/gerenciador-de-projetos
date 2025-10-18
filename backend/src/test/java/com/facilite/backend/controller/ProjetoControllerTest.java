package com.facilite.backend.controller;

import com.facilite.backend.dto.ProjetoRequest;
import com.facilite.backend.dto.ProjetoResponse;
import com.facilite.backend.exception.ProjetoNotFoundException;
import com.facilite.backend.model.StatusProjeto;
import com.facilite.backend.service.ProjetoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjetoController.class)
class ProjetoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjetoService projetoService;

    // ========== TESTES GET /api/projetos ==========

    @Test
    void listarProjetos_DeveRetornarLista() throws Exception {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(1L);
        response.setNome("Projeto Teste");
        response.setStatus(StatusProjeto.A_INICIAR);

        List<ProjetoResponse> projetos = Arrays.asList(response);
        when(projetoService.listarTodos()).thenReturn(projetos);

        mockMvc.perform(get("/api/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Projeto Teste"))
                .andExpect(jsonPath("$[0].status").value("A_INICIAR"));
    }

    // ========== TESTES GET /api/projetos/{id} ==========

    @Test
    void buscarProjeto_QuandoExiste_DeveRetornarProjeto() throws Exception {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(1L);
        response.setNome("Projeto Teste");
        when(projetoService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/projetos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Projeto Teste"));
    }

    @Test
    void buscarProjeto_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        // CORREÇÃO: Use uma exception específica que resulte em 404
        when(projetoService.buscarPorId(1L)).thenThrow(
                new ProjetoNotFoundException(1L) {
                    // Ou crie uma exception personalizada com @ResponseStatus(HttpStatus.NOT_FOUND)
                });

        mockMvc.perform(get("/api/projetos/1"))
                .andExpect(status().isNotFound());
    }

    // ========== TESTES POST /api/projetos ==========

    @Test
    void criarProjeto_ComDadosValidos_DeveRetornarCreated() throws Exception {
        ProjetoRequest request = new ProjetoRequest();
        request.setNome("Novo Projeto");
        request.setInicioPrevisto(LocalDate.now().plusDays(1));
        request.setTerminoPrevisto(LocalDate.now().plusDays(30));

        ProjetoResponse response = new ProjetoResponse();
        response.setId(1L);
        response.setNome("Novo Projeto");
        response.setStatus(StatusProjeto.A_INICIAR);

        when(projetoService.criarProjeto(any(ProjetoRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()) // CORREÇÃO: Mudado de isOk() para isCreated()
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Novo Projeto"))
                .andExpect(jsonPath("$.status").value("A_INICIAR"));
    }

    @Test
    void criarProjeto_ComDadosInvalidos_DeveRetornarBadRequest() throws Exception {
        ProjetoRequest request = new ProjetoRequest(); // Nome em branco - dados inválidos

        // CORREÇÃO: Configure o mock para lançar exception de validação
        when(projetoService.criarProjeto(any(ProjetoRequest.class)))
                .thenThrow(new IllegalArgumentException("Dados inválidos"));

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES PUT /api/projetos/{id} ==========

    @Test
    void atualizarProjeto_ComDadosValidos_DeveRetornarProjetoAtualizado() throws Exception {
        ProjetoRequest request = new ProjetoRequest();
        request.setNome("Projeto Atualizado");
        request.setInicioPrevisto(LocalDate.now().plusDays(1));
        request.setTerminoPrevisto(LocalDate.now().plusDays(30));

        ProjetoResponse response = new ProjetoResponse();
        response.setId(1L);
        response.setNome("Projeto Atualizado");
        response.setStatus(StatusProjeto.EM_ANDAMENTO);

        when(projetoService.atualizarProjeto(eq(1L), any(ProjetoRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/projetos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Projeto Atualizado"))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void atualizarProjeto_ComDadosInvalidos_DeveRetornarBadRequest() throws Exception {
        ProjetoRequest request = new ProjetoRequest(); // Nome em branco - dados inválidos

        // CORREÇÃO: Configure o mock para lançar exception de validação
        when(projetoService.atualizarProjeto(eq(1L), any(ProjetoRequest.class)))
                .thenThrow(new IllegalArgumentException("Dados inválidos"));

        mockMvc.perform(put("/api/projetos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES DELETE /api/projetos/{id} ==========

    @Test
    void excluirProjeto_QuandoExiste_DeveRetornarNoContent() throws Exception {
        doNothing().when(projetoService).excluirProjeto(1L);

        mockMvc.perform(delete("/api/projetos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirProjeto_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        // Use a exception específica que já tem @ResponseStatus(HttpStatus.NOT_FOUND)
        doThrow(new ProjetoNotFoundException(1L))
                .when(projetoService).excluirProjeto(1L);

        mockMvc.perform(delete("/api/projetos/1"))
                .andExpect(status().isNotFound());
    }
    // ========== TESTES GET /api/projetos/status/{status} ==========

    @Test
    void listarPorStatus_ComStatusValido_DeveRetornarLista() throws Exception {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(1L);
        response.setNome("Projeto Teste");
        response.setStatus(StatusProjeto.A_INICIAR);

        List<ProjetoResponse> projetos = Arrays.asList(response);
        when(projetoService.listarPorStatus(StatusProjeto.A_INICIAR)).thenReturn(projetos);

        mockMvc.perform(get("/api/projetos/status/A_INICIAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Projeto Teste"))
                .andExpect(jsonPath("$[0].status").value("A_INICIAR"));
    }

    // ========== TESTES PATCH /api/projetos/{id}/status/{novoStatus} ==========

    @Test
    void transicionarStatus_ComTransicaoValida_DeveRetornarProjetoAtualizado() throws Exception {
        ProjetoResponse response = new ProjetoResponse();
        response.setId(1L);
        response.setNome("Projeto Teste");
        response.setStatus(StatusProjeto.EM_ANDAMENTO);

        when(projetoService.transicionarStatus(1L, StatusProjeto.EM_ANDAMENTO)).thenReturn(response);

        mockMvc.perform(patch("/api/projetos/1/status/EM_ANDAMENTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void transicionarStatus_ComTransicaoInvalida_DeveRetornarBadRequest() throws Exception {
        when(projetoService.transicionarStatus(1L, StatusProjeto.EM_ANDAMENTO))
                .thenThrow(new IllegalArgumentException("Transição de status não permitida"));

        mockMvc.perform(patch("/api/projetos/1/status/EM_ANDAMENTO"))
                .andExpect(status().isBadRequest());
       }
}