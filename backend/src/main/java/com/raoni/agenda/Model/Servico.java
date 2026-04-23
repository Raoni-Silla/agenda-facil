package com.raoni.agenda.Model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "servicos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotNull(message = "O valor é obrigatório")
    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A duração é obrigatória")
    private Integer duracaoMinutos;

    @OneToMany(mappedBy = "servico")
    private List<Agendamento> agendamentos;
}
