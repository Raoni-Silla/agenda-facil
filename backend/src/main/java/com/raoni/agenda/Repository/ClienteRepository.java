package com.raoni.agenda.Repository;

import com.raoni.agenda.Model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByTelefone(String telefone);

    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR c.telefone LIKE CONCAT('%', :busca, '%')")
    Page<Cliente> pesquisarPorNomeOuTelefone(@Param("busca") String busca, Pageable paginacao);

    // Verifica se o telefone existe, MAS ignorando o ID passado (usado na hora de EDITAR um cliente)
    boolean existsByTelefoneAndIdNot(String telefone, Long id);
}
