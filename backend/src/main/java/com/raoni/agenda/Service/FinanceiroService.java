package com.raoni.agenda.Service;

import com.raoni.agenda.Repository.AgendamentoRepository;
import com.raoni.agenda.Repository.DespesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FinanceiroService {

    private final AgendamentoRepository agendamentoRepository;
    private final DespesaRepository despesaRepository;

    public BigDecimal calcularFaturamentoLiquido (){
        BigDecimal bruto = agendamentoRepository.somarFaturamentoBruto();
        if (bruto == null) bruto = BigDecimal.ZERO;
        BigDecimal totalDespesas = despesaRepository.somarTodasDespesas();
        return bruto.subtract(totalDespesas);
    }



}

