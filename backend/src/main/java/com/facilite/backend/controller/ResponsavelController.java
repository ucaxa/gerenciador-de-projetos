package com.facilite.backend.controller;


import com.facilite.backend.dto.ResponsavelRequest;
import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.service.ResponsavelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/responsaveis")
@Tag(name = "Responsáveis", description = "API para gerenciamento de responsáveis")
public class ResponsavelController {

    private final ResponsavelService responsavelService;

    @GetMapping
    @Operation(summary = "Listar todos os responsáveis")
    public ResponseEntity<List<ResponsavelResponse>> listarResponsaveis() {
        return ResponseEntity.ok(responsavelService.listarTodos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar responsável por ID")
    public ResponseEntity<ResponsavelResponse> buscarResponsavel(@PathVariable Long id) {
        return ResponseEntity.ok(responsavelService.buscarPorId(id));
    }

    @PostMapping
    @Operation(summary = "Criar novo responsável")
    public ResponseEntity<ResponsavelResponse> criarResponsavel(
            @Valid @RequestBody ResponsavelRequest responsavel) {
        return ResponseEntity.ok(responsavelService.criarResponsavel(responsavel));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar responsável existente")
    public ResponseEntity<ResponsavelResponse> atualizarResponsavel(
            @PathVariable Long id,
            @Valid @RequestBody ResponsavelRequest responsavel) {
        return ResponseEntity.ok(responsavelService.atualizarResponsavel(id, responsavel));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir responsável")
    public ResponseEntity<Void> excluirResponsavel(@PathVariable Long id) {
        responsavelService.excluirResponsavel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paginado")
    @Operation(summary = "Listar responsáveis com paginação")
    public ResponseEntity<Page<ResponsavelResponse>> listarResponsaveisPaginados(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(responsavelService.listarPaginado(pageable));
    }
}