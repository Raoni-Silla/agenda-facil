package com.raoni.agenda.Model;

import com.raoni.agenda.Enums.StatusAgendamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @NotNull(message = "O serviço é obrigatório")
    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;

    @NotNull(message = "A data e hora são obrigatórias")
    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StatusAgendamento status;

    @NotNull(message = "O valor não pode ser nulo")
    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago;

}
