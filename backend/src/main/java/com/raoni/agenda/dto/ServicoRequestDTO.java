package com.raoni.agenda.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServicoRequestDTO(
        @NotBlank(message = "O nome não pode ser nulo")
        @Size(min = 3)
        String nome,

        @NotNull(message = "O valor é obrigatório")
        @Column(precision = 10, scale = 2)
        @Min(1)
        @PositiveOrZero
        BigDecimal valor,

        @NotNull(message = "A duração é obrigatória")
        @Min(3)
        @PositiveOrZero
        Integer duracaoMinutos
) {
}
