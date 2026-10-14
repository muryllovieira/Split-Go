package com.splitgo.api.group.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateGroupRequest(
        @NotBlank(message = "O nome do grupo é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String name,

        @NotBlank(message = "A moeda base é obrigatória")
        @Pattern(regexp = "^[A-Z]{3}$", message = "A moeda deve ser um código ISO de 3 letras (ex: BRL)")
        String baseCurrency,
    
        @NotNull 
        @Size(min = 2, message = "O grupo precisa de pelo menos 2 integrantes")
        List<@NotBlank @Size(max = 60) String> members
    ) {
            
}