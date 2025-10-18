package com.facilite.backend.exception;


public class ResponsavelNotFoundException extends RuntimeException {
    public ResponsavelNotFoundException(Long id) {
        super("Responsável não encontrado com ID: " + id);
    }
}
