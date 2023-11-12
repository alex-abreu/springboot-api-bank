package com.project.minipay.dtos;

import java.math.BigDecimal;

import Enums.UserType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record UserDto(@NotBlank String firstname, @NotBlank String lastname, @NotBlank String document, @NotBlank String email, @NotBlank String password,
                      @NotNull UserType userType, @NotNull BigDecimal balance) {
}

