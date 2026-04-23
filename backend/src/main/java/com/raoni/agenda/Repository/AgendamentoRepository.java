package com.raoni.agenda.Repository;

import com.raoni.agenda.Model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.dataHora >= :inicio AND a.dataHora <= :fim")
    long countAgendamentosDoDia(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.dataHora >= :inicioSemana AND a.dataHora <= :fimSemana")
    long countAgendamentosDaSemana(LocalDateTime inicioSemana, LocalDateTime fimSemana);

    @Query("SELECT COUNT(c) FROM Cliente c")
    long countTotalClientes();

    @Query("SELECT SUM(a.valorPago) FROM Agendamento a WHERE a.status = 'CONCLUIDO'")
    BigDecimal somarFaturamentoBruto();

}
