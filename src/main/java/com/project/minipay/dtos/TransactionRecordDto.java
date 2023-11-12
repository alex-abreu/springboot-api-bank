package com.project.minipay.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;


public record TransactionRecordDto(@NotNull UUID sender, @NotNull BigDecimal amount, @NotNull UUID receiver) {

}
