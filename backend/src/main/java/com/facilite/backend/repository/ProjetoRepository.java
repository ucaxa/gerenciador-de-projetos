package com.facilite.backend.repository;


import com.facilite.backend.model.Projeto;
import com.facilite.backend.model.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {

    List<Projeto> findByStatus(StatusProjeto status);

    @Query("SELECT p FROM Projeto p WHERE p.nome LIKE %:nome%")
    List<Projeto> findByNomeContaining(String nome);

    boolean existsByNome(String nome);
}