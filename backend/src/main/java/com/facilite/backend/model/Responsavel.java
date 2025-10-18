package com.facilite.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "responsaveis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Responsavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Email(message = "Email deve ser válido")
    @Column(unique = true, nullable = false)
    private String email;

    private String cargo;

    @ManyToMany(mappedBy = "responsaveis", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Projeto> projetos = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

   }