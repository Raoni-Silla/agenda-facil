package com.raoni.agenda.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String telefone,
        int totalVisitas,
        LocalDateTime ultimaVisita
) {}
