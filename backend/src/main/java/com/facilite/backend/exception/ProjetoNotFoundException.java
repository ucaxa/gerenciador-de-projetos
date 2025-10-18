package com.facilite.backend.exception;

public class ProjetoNotFoundException extends RuntimeException {
    public ProjetoNotFoundException(Long id) {
        super("Projeto não encontrado com ID: " + id);
    }
}
