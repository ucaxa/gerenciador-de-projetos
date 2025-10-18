package com.facilite.backend.repository;

import com.facilite.backend.dto.ResponsavelResponse;
import com.facilite.backend.model.Responsavel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponsavelRepository extends JpaRepository<Responsavel, Long> {

    Optional<Responsavel> findByEmail(String email);

    boolean existsByEmail(String email);

}