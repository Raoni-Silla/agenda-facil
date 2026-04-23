package com.raoni.agenda.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes") // É boa prática definir o nome da tabela no plural
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 50, message = "O nome deve ter entre 3 e 50 caracteres")
    private String nome;

    @NotBlank(message = "O telefone é obrigatório")
    @Column(unique = true)
    private String telefone;

    @Column(name = "data_cadastro", updatable = false)
    private LocalDate dataCadastro;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Agendamento> agendamentos;

    @PrePersist
    protected void onCreate(){
        this.dataCadastro = LocalDate.now();
        this.agendamentos = new ArrayList<>();
    }
}
