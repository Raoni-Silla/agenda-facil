package com.raoni.agenda.Repository;

import com.raoni.agenda.Model.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface DespesaRepository extends JpaRepository <Despesa,Long> {
    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM Despesa d")
    BigDecimal somarTodasDespesas();
}
