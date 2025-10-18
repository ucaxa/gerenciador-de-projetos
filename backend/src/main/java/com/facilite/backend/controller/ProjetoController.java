package com.facilite.backend.controller;

import com.facilite.backend.dto.ProjetoRequest;
import com.facilite.backend.dto.ProjetoResponse;
import com.facilite.backend.model.StatusProjeto;
import com.facilite.backend.service.ProjetoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "API para gerenciamento de projetos Kanban")
public class ProjetoController {

    private final ProjetoService projetoService;

    @GetMapping
    @Operation(summary = "Listar todos os projetos")
    public ResponseEntity<List<ProjetoResponse>> listarProjetos() {
        return ResponseEntity.ok(projetoService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar projeto por ID")
    public ResponseEntity<ProjetoResponse> buscarProjeto(@PathVariable Long id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }
    @PostMapping
    public ResponseEntity<ProjetoResponse> criarProjeto(@Valid @RequestBody ProjetoRequest request) {
        ProjetoResponse response = projetoService.criarProjeto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar projeto existente")
    public ResponseEntity<ProjetoResponse> atualizarProjeto(
            @PathVariable Long id,
            @Valid @RequestBody ProjetoRequest projeto) {
        return ResponseEntity.ok(projetoService.atualizarProjeto(id, projeto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir projeto")
    public ResponseEntity<Void> excluirProjeto(@PathVariable Long id) {
        projetoService.excluirProjeto(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar projetos por status")
    public ResponseEntity<List<ProjetoResponse>> listarPorStatus(
            @PathVariable StatusProjeto status) {
        return ResponseEntity.ok(projetoService.listarPorStatus(status));
    }

    @PatchMapping("/{id}/status/{novoStatus}")
    @Operation(summary = "Transicionar status do projeto")
    public ResponseEntity<ProjetoResponse> transicionarStatus(
            @PathVariable Long id,
            @PathVariable StatusProjeto novoStatus) {
        return ResponseEntity.ok(projetoService.transicionarStatus(id, novoStatus));
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar projetos com paginação")
    public ResponseEntity<Page<ProjetoResponse>> listarProjetosPaginados(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(projetoService.listarPaginado(pageable));
    }

}