package com.facilite.backend.exception;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String error;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private String path;

    public ErrorResponse(int status, String message, String error, String path) {
        this.status = status;
        this.message = message;
        this.error = error;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }



}