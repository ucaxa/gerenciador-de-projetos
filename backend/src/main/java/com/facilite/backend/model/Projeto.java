package com.facilite.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "projetos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProjeto status = StatusProjeto.A_INICIAR;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "projeto_responsavel",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "responsavel_id")
    )
    private Set<Responsavel> responsaveis = new HashSet<>();

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inicioPrevisto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminoPrevisto;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inicioRealizado;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminoRealizado;

    private Integer diasAtraso = 0;

    private Double percentualTempoRestante = 0.0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

   }