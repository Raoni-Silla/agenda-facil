package com.raoni.agenda.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_agendamento")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemAgendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @NotNull(message = "O valor cobrado é obrigatório")
    @Column(name = "valor_cobrado", precision = 10, scale = 2)
    private BigDecimal valorCobrado;
}