package com.splitgo.api.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.splitgo.api.expense.SplitType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateExpenseRequest(
        @NotBlank(message = "A descrição é obrigatória") @Size(max = 140, message = "A descrição deve ter no máximo 140 caracteres") String description,

        @NotNull(message = "O pagador é obrigatório") UUID payerId,

        @NotNull(message = "O valor é obrigatório") @Positive(message = "O valor deve ser maior que zero") @Digits(integer = 17, fraction = 2, message = "O valor deve ter no máximo 2 casas decimais") BigDecimal amount,

        LocalDate expenseDate,

        @NotNull(message = "O tipo de divisão é obrigatório") SplitType splitType,

        @NotEmpty(message = "Informe pelo menos 1 participante") List<@Valid @NotNull ParticipantRequest> participants) {
}