package com.raoni.agenda.dto;
import java.math.BigDecimal;

public record ServicoResponseDTO (
        Long id,
        String nome,
        BigDecimal valor,
        Integer duracaoMinutos
)
{}
