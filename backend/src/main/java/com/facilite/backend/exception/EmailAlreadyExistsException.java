package com.facilite.backend.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Já existe um responsável com o email: " + email);
    }
}