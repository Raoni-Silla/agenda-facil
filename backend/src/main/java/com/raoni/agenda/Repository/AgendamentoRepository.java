package com.raoni.agenda.Repository;

import com.raoni.agenda.Enums.StatusAgendamento;
import com.raoni.agenda.Model.Agendamento;
import com.raoni.agenda.Model.Cliente;
import com.raoni.agenda.dto.AgendamentoResumoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    long countByData(LocalDate data);

    long countByDataBetween(LocalDate inicioSemana, LocalDate fimSemana);

    @Query("SELECT SUM(a.valorTotal) FROM Agendamento a WHERE a.status = 'CONCLUIDO'")
    BigDecimal somarFaturamentoBruto();

    List<Agendamento> findByData(LocalDate data);

    @Query("SELECT DISTINCT a FROM Agendamento a " +
            "LEFT JOIN a.itens i " +
            "LEFT JOIN i.servico s " +
            "WHERE LOWER(a.cliente.nome) LIKE LOWER(CONCAT('%', :busca, '%')) " +
            "OR a.cliente.telefone LIKE CONCAT('%', :busca, '%') " +
            "OR LOWER(s.nome) LIKE LOWER(CONCAT('%', :busca, '%'))")
    Page<Agendamento> pesquisarPorNomeOuTelefone(@Param("busca") String busca, Pageable paginacao);

    Page<Agendamento> findAllByStatus(StatusAgendamento statusAgendamento, Pageable paginacao);

    @Query("SELECT a.data as data, COUNT(a) as total " +
            "FROM Agendamento a " +
            "WHERE a.data BETWEEN :inicio AND :fim " +
            "GROUP BY a.data")
    List<AgendamentoResumoDTO> contarPorPeriodo(LocalDate inicio, LocalDate fim);
}