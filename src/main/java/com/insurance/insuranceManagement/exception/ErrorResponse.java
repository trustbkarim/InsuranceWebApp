package com.insurance.insuranceManagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, String errorCode,

                            // Utilisé uniquement pour les erreurs de validation (@Valid)
                            Map<String, String> validationErrors) {
}
