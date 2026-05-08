package com.raoni.agenda.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record AgendamentoRequestDTO(
        @NotNull Long idCliente,
        @NotNull @FutureOrPresent LocalDate data,
        @NotNull LocalTime horaInicio,
        @NotNull LocalTime horaFim,
        @NotEmpty List<Long> servicos
) {
}