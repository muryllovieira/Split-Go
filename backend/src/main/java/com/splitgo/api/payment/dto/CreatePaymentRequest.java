package com.splitgo.api.payment.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePaymentRequest(
        @NotNull(message = "Quem pagou é obrigatório") UUID fromMemberId,

        @NotNull(message = "Quem recebeu é obrigatório") UUID toMemberId,

        @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser maior que zero") @Digits(integer = 17, fraction = 2, message = "O valor deve ter no máximo 2 casas decimais") BigDecimal amount) {
}