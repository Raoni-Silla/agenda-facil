package com.raoni.agenda.dto;

import java.math.BigDecimal;

public record ItemAgendamentoResponseDTO(
        Long id,
        ServicoResponseDTO servico,
        BigDecimal valorCobrado
) {
}