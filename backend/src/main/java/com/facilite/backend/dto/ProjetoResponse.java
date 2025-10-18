package com.facilite.backend.dto;





import com.facilite.backend.model.StatusProjeto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "DTO de resposta para informações de projeto")
public class ProjetoResponse {
    private Long id;
    private String nome;
    private StatusProjeto status;
    private Set<ResponsavelResponse> responsaveis;
    private LocalDate inicioPrevisto;
    private LocalDate terminoPrevisto;
    private LocalDate inicioRealizado;
    private LocalDate terminoRealizado;
    private Integer diasAtraso;
    private Double percentualTempoRestante;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtores, Getters e Setters
    public ProjetoResponse() {}

    public ProjetoResponse(Long id, String nome, StatusProjeto status, Integer diasAtraso,
                           Double percentualTempoRestante) {
        this.id = id;
        this.nome = nome;
        this.status = status;
        this.diasAtraso = diasAtraso;
        this.percentualTempoRestante = percentualTempoRestante;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public StatusProjeto getStatus() { return status; }
    public void setStatus(StatusProjeto status) { this.status = status; }

    public Set<ResponsavelResponse> getResponsaveis() { return responsaveis; }
    public void setResponsaveis(Set<ResponsavelResponse> responsaveis) { this.responsaveis = responsaveis; }

    public LocalDate getInicioPrevisto() { return inicioPrevisto; }
    public void setInicioPrevisto(LocalDate inicioPrevisto) { this.inicioPrevisto = inicioPrevisto; }

    public LocalDate getTerminoPrevisto() { return terminoPrevisto; }
    public void setTerminoPrevisto(LocalDate terminoPrevisto) { this.terminoPrevisto = terminoPrevisto; }

    public LocalDate getInicioRealizado() { return inicioRealizado; }
    public void setInicioRealizado(LocalDate inicioRealizado) { this.inicioRealizado = inicioRealizado; }

    public LocalDate getTerminoRealizado() { return terminoRealizado; }
    public void setTerminoRealizado(LocalDate terminoRealizado) { this.terminoRealizado = terminoRealizado; }

    public Integer getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(Integer diasAtraso) { this.diasAtraso = diasAtraso; }

    public Double getPercentualTempoRestante() { return percentualTempoRestante; }
    public void setPercentualTempoRestante(Double percentualTempoRestante) { this.percentualTempoRestante = percentualTempoRestante; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}