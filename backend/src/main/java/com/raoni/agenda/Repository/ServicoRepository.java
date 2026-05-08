package com.raoni.agenda.Repository;

import com.raoni.agenda.Model.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository <Servico, Long> {
    boolean existsByNome(String nome);

    Page<Servico> findByNomeContainingIgnoreCase(String busca, Pageable paginacao);

    boolean existsByNomeAndIdNot(String nome, Long id);

    List<Servico> findByIdIn(List<Long> ids);

    List<Servico> findByNomeContainingIgnoreCase(String nome);
}
