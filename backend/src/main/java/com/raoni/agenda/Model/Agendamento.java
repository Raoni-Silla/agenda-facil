package com.raoni.agenda.Model;

import com.raoni.agenda.Enums.StatusAgendamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agendamentos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O cliente é obrigatório")
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Saiu o Servico único e entrou a lista de Itens
    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAgendamento> itens = new ArrayList<>();

    // Separamos Data, Início e Fim para facilitar a validação e o Front-end
    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @NotNull(message = "O horário de início é obrigatório")
    private LocalTime horaInicio;

    @NotNull(message = "O horário de fim é obrigatório")
    private LocalTime horaFim;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusAgendamento status;

    @NotNull(message = "O valor total não pode ser nulo")
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;
}