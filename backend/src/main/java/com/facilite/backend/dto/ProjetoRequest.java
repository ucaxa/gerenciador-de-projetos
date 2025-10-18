package com.facilite.backend.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "DTO de requisição para criação de um projeto")
public class ProjetoRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Schema(description = "Nome do usuário", example = "Projeto A")
    private String nome;

    @Schema(description = "Ids dos responsáveis pelo projeto", example = "1,2,3")
    private Set<Long> responsavelIds;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data prevista de inicio do projeto", example = "2025/10/15")
    private LocalDate inicioPrevisto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data prevista de fim do projeto", example = "2025/10/15")
    private LocalDate terminoPrevisto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data de inicio do projeto", example = "2025/10/15")
    private LocalDate inicioRealizado;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Data de fim do projeto", example = "2025/10/15")
    private LocalDate terminoRealizado;

    }