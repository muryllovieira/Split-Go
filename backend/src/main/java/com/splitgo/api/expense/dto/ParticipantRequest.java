package com.splitgo.api.expense.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record ParticipantRequest(
        @NotNull(message = "O integrante do participante é obrigatório") UUID memberId,

        @Digits(integer = 17, fraction = 2, message = "O valor do participante deve ter no máximo 2 casas decimais") BigDecimal value) {
}