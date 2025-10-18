package com.facilite.backend.controller;

import com.facilite.backend.dto.ResponsavelRequest;
import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.exception.ProjetoNotFoundException;
import com.facilite.backend.exception.ResponsavelNotFoundException;
import com.facilite.backend.service.ResponsavelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResponsavelController.class)
class ResponsavelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResponsavelService responsavelService;

    // ========== TESTES GET /api/responsaveis ==========

    @Test
    void listarResponsaveis_DeveRetornarLista() throws Exception {
        ResponsavelResponse response = new ResponsavelResponse();
        response.setId(1L);
        response.setNome("João Silva");
        response.setEmail("joao@email.com");
        response.setCargo("Desenvolvedor");

        List<ResponsavelResponse> responsaveis = Arrays.asList(response);
        when(responsavelService.listarTodos()).thenReturn(responsaveis);

        mockMvc.perform(get("/api/responsaveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("João Silva"))
                .andExpect(jsonPath("$[0].email").value("joao@email.com"))
                .andExpect(jsonPath("$[0].cargo").value("Desenvolvedor"));
    }

    // ========== TESTES GET /api/responsaveis/{id} ==========

    @Test
    void buscarResponsavel_QuandoExiste_DeveRetornarResponsavel() throws Exception {
        ResponsavelResponse response = new ResponsavelResponse();
        response.setId(1L);
        response.setNome("João Silva");
        response.setEmail("joao@email.com");
        when(responsavelService.buscarPorId(1L)).thenReturn(response);

        mockMvc.perform(get("/api/responsaveis/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"));
    }

    @Test
    void buscarResponsavel_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        when(responsavelService.buscarPorId(1L)).thenThrow(new ResponsavelNotFoundException(1L));

        mockMvc.perform(get("/api/responsaveis/1"))
                .andExpect(status().isNotFound());
    }

    // ========== TESTES POST /api/responsaveis ==========

    @Test
    void criarResponsavel_ComDadosValidos_DeveRetornarCreated() throws Exception {
        ResponsavelRequest request = new ResponsavelRequest();
        request.setNome("Maria Santos");
        request.setEmail("maria@email.com");
        request.setCargo("Analista");

        ResponsavelResponse response = new ResponsavelResponse();
        response.setId(1L);
        response.setNome("Maria Santos");
        response.setEmail("maria@email.com");
        response.setCargo("Analista");
        response.setCreatedAt(LocalDateTime.now());

        when(responsavelService.criarResponsavel(any(ResponsavelRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.cargo").value("Analista"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void criarResponsavel_ComEmailDuplicado_DeveRetornarBadRequest() throws Exception {
        ResponsavelRequest request = new ResponsavelRequest();
        request.setNome("Maria Santos");
        request.setEmail("maria@email.com");
        request.setCargo("Analista");

        when(responsavelService.criarResponsavel(any(ResponsavelRequest.class)))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado"));

        mockMvc.perform(post("/api/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void criarResponsavel_ComDadosInvalidos_DeveRetornarBadRequest() throws Exception {
        ResponsavelRequest request = new ResponsavelRequest(); // Nome e email em branco

        mockMvc.perform(post("/api/responsaveis")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES PUT /api/responsaveis/{id} ==========

    @Test
    void atualizarResponsavel_ComDadosValidos_DeveRetornarResponsavelAtualizado() throws Exception {
        ResponsavelRequest request = new ResponsavelRequest();
        request.setNome("Maria Santos Atualizada");
        request.setEmail("maria.nova@email.com");
        request.setCargo("Analista Sênior");

        ResponsavelResponse response = new ResponsavelResponse();
        response.setId(1L);
        response.setNome("Maria Santos Atualizada");
        response.setEmail("maria.nova@email.com");
        response.setCargo("Analista Sênior");
        response.setUpdatedAt(LocalDateTime.now());

        when(responsavelService.atualizarResponsavel(eq(1L), any(ResponsavelRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/responsaveis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria Santos Atualizada"))
                .andExpect(jsonPath("$.email").value("maria.nova@email.com"))
                .andExpect(jsonPath("$.cargo").value("Analista Sênior"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void atualizarResponsavel_ComEmailDuplicado_DeveRetornarBadRequest() throws Exception {
        ResponsavelRequest request = new ResponsavelRequest();
        request.setNome("Maria Santos");
        request.setEmail("email.existente@email.com");
        request.setCargo("Analista");

        when(responsavelService.atualizarResponsavel(eq(1L), any(ResponsavelRequest.class)))
                .thenThrow(new IllegalArgumentException("E-mail já cadastrado"));

        mockMvc.perform(put("/api/responsaveis/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES DELETE /api/responsaveis/{id} ==========

    @Test
    void excluirResponsavel_QuandoExiste_DeveRetornarNoContent() throws Exception {
        doNothing().when(responsavelService).excluirResponsavel(1L);

        mockMvc.perform(delete("/api/responsaveis/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirResponsavel_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        doThrow(new ResponsavelNotFoundException(1L))
                .when(responsavelService).excluirResponsavel(1L);

        mockMvc.perform(delete("/api/responsaveis/1"))
                .andExpect(status().isNotFound());
    }}