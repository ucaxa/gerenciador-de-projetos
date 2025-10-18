package com.facilite.backend.controller;

import com.facilite.backend.dto.ProjetoRequest;
import com.facilite.backend.model.StatusProjeto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProjetoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ========== TESTES CRIAÇÃO ==========

    @Test
    void criarProjeto_ComDadosValidos_DeveRetornarCreatedComMetricasCalculadas() throws Exception {
        ProjetoRequest request = new ProjetoRequest();
        request.setNome("Projeto Integration Test");
        request.setInicioPrevisto(LocalDate.now().plusDays(1));
        request.setTerminoPrevisto(LocalDate.now().plusDays(30));

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Projeto Integration Test"))
                .andExpect(jsonPath("$.status").exists()) // Verifica que status foi calculado
                .andExpect(jsonPath("$.diasAtraso").exists()) // Verifica que dias atraso foi calculado
                .andExpect(jsonPath("$.percentualTempoRestante").exists()); // Verifica que percentual foi calculado
    }

    @Test
    void criarProjeto_ComDadosInvalidos_DeveRetornarBadRequest() throws Exception {
        ProjetoRequest request = new ProjetoRequest(); // Nome em branco
        request.setInicioPrevisto(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES LISTAGEM ==========

    @Test
    void listarProjetos_DeveRetornarLista() throws Exception {
        mockMvc.perform(get("/api/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listarPorStatus_ComStatusValido_DeveRetornarLista() throws Exception {
        mockMvc.perform(get("/api/projetos/status/A_INICIAR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void listarPorStatus_ComStatusInvalido_DeveRetornarBadRequest() throws Exception {
        mockMvc.perform(get("/api/projetos/status/STATUS_INEXISTENTE"))
                .andExpect(status().isBadRequest());
    }

    // ========== TESTES BUSCA ==========

    @Test
    void buscarProjetoPorId_QuandoExiste_DeveRetornarProjeto() throws Exception {
        // Primeiro cria um projeto
        ProjetoRequest request = new ProjetoRequest();
        request.setNome("Projeto para Busca");
        request.setInicioPrevisto(LocalDate.now().plusDays(1));
        request.setTerminoPrevisto(LocalDate.now().plusDays(10));

        String response = mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        // Extrai o ID da resposta e busca
        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/projetos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Projeto para Busca"));
    }

    @Test
    void buscarProjetoPorId_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        mockMvc.perform(get("/api/projetos/999999"))
                .andExpect(status().isNotFound());
    }

    // ========== TESTES ATUALIZAÇÃO ==========

    @Test
    void atualizarProjeto_ComDadosValidos_DeveRetornarProjetoAtualizado() throws Exception {
        // Cria projeto
        ProjetoRequest createRequest = new ProjetoRequest();
        createRequest.setNome("Projeto Original");
        createRequest.setInicioPrevisto(LocalDate.now().plusDays(1));
        createRequest.setTerminoPrevisto(LocalDate.now().plusDays(10));

        String response = mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // Atualiza projeto
        ProjetoRequest updateRequest = new ProjetoRequest();
        updateRequest.setNome("Projeto Atualizado");
        updateRequest.setInicioPrevisto(LocalDate.now().plusDays(2));
        updateRequest.setTerminoPrevisto(LocalDate.now().plusDays(15));

        mockMvc.perform(put("/api/projetos/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Projeto Atualizado"))
                .andExpect(jsonPath("$.status").exists()) // Verifica recálculo
                .andExpect(jsonPath("$.updatedAt").exists()); // Verifica auditoria
    }

    // ========== TESTES EXCLUSÃO ==========

    @Test
    void excluirProjeto_QuandoExiste_DeveRetornarNoContent() throws Exception {
        // Cria projeto
        ProjetoRequest request = new ProjetoRequest();
        request.setNome("Projeto para Excluir");
        request.setInicioPrevisto(LocalDate.now().plusDays(1));
        request.setTerminoPrevisto(LocalDate.now().plusDays(10));

        String response = mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/projetos/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirProjeto_QuandoNaoExiste_DeveRetornarNotFound() throws Exception {
        mockMvc.perform(delete("/api/projetos/999999"))
                .andExpect(status().isNotFound());
    }

    // ========== TESTES TRANSIÇÃO DE STATUS ==========

    @Test
    void transicionarStatus_ComTransicaoValida_DeveRetornarProjetoAtualizado() throws Exception {
        // Cria projeto em A_INICIAR
        ProjetoRequest request = new ProjetoRequest();
        request.setNome("Projeto para Transição");
        request.setInicioPrevisto(LocalDate.now().plusDays(1));
        request.setTerminoPrevisto(LocalDate.now().plusDays(10));

        String response = mockMvc.perform(post("/api/projetos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        // Transiciona para EM_ANDAMENTO
        mockMvc.perform(patch("/api/projetos/{id}/status/{novoStatus}", id, "EM_ANDAMENTO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.inicioRealizado").exists()); // Verifica ação automática
    }

    @Test
    void transicionarStatus_ComTransicaoInvalida_DeveRetornarBadRequest() throws Exception {
        mockMvc.perform(patch("/api/projetos/{id}/status/{novoStatus}", 999, "STATUS_INVALIDO"))
                .andExpect(status().isBadRequest());
    }
}