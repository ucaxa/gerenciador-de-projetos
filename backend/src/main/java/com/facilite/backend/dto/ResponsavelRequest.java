package com.facilite.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO de requisição para criação de um responsável")
public class ResponsavelRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Schema(description = "Email do responsável", example = "responsavel@exemplo.com")
    private String email;
    @Schema(description = "Cargo do usuário", example = "gerente de projeto")
    private String cargo;

}
