package com.raoni.agenda.dto;

import com.raoni.agenda.Enums.StatusAgendamento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AgendamentoResponseDTO(
        Long idAgendamento,
        ClienteResponseDTO cliente,
        List<ItemAgendamentoResponseDTO> itens,
        LocalDate data,
        LocalTime horaInicio,
        LocalTime horaFim,
        BigDecimal valorTotal,
        StatusAgendamento status
) {
}
