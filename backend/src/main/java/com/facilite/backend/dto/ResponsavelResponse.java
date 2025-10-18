package com.facilite.backend.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(description = "DTO de resposta para informações de responsável")
public class ResponsavelResponse {
    private Long id;
    private String nome;
    private String email;
    private String cargo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public ResponsavelResponse(Long id, String nome, String email, String cargo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
    }
}