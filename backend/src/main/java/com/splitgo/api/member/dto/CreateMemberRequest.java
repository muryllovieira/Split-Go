package com.splitgo.api.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
        @NotBlank(message = "O nome do integrante é obrigatório")
        @Size(max = 60, message = "O nome deve ter no máximo 60 caracteres")
        String name) {
}
