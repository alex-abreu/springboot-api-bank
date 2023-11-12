package com.project.minipay.dtos;

import jakarta.validation.constraints.NotBlank;

public record NotificationDto(@NotBlank String email, @NotBlank String mensagem) {
}
