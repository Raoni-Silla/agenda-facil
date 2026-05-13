package com.raoni.agenda.Controller;

import com.raoni.agenda.Service.AgendamentoService;
import com.raoni.agenda.dto.AgendamentoRequestDTO;
import com.raoni.agenda.dto.AgendamentoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agendamento")
@CrossOrigin(origins = "http://localhost:4200")
public class AgendamentoController {

    private final AgendamentoService service;

    @PostMapping("/criar")
    public ResponseEntity<AgendamentoResponseDTO> criarCliente (@Valid @RequestBody AgendamentoRequestDTO dto){
        AgendamentoResponseDTO agendamentoCriado = service.criarAgendamento(dto);
        return ResponseEntity.ok(agendamentoCriado);
    }

    @GetMapping("/obteragendamentos")
    public ResponseEntity<Page<AgendamentoResponseDTO>> listarAgendamentos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca
    ) {
        Page<AgendamentoResponseDTO> pagina = service.obterAgendamentosPaginados(page, size, busca);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/obteragendamentosconcluidos")
    public ResponseEntity<Page<AgendamentoResponseDTO>> listarAgendamentosConcluidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca
    ) {
        Page<AgendamentoResponseDTO> pagina = service.obterAgendamentosPaginadosConcluidos(page, size, busca);
        return ResponseEntity.ok(pagina);
    }

    @GetMapping("/obteragendamentospendentes")
    public ResponseEntity<Page<AgendamentoResponseDTO>> listarAgendamentosPendentes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca
    ) {
        Page<AgendamentoResponseDTO> pagina = service.obterAgendamentosPaginadosPendentes(page, size, busca);
        return ResponseEntity.ok(pagina);
    }

    @PostMapping("/concluir/{id}")
    public ResponseEntity<AgendamentoResponseDTO> concluirAgendamento(@PathVariable Long id) {
        AgendamentoResponseDTO dto = service.concluirAgendamento(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletarAgendamento(@PathVariable Long id) {
        service.excluirAgendamento(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/obter-agendamentos-hoje")
    public ResponseEntity<Long> getAgendamentosParaHoje(){
        long agendamentosParaHoje = service.getQuantidadeDeAgendamentosHoje();
        return ResponseEntity.ok(agendamentosParaHoje);
    }

    @GetMapping("/obter-agendamentos-semana")
    public ResponseEntity<Long> getAgendamentosDaSemana (){
        long agendamentosDaSemana = service.getQuantidadeAgendamentoSemana();
        return ResponseEntity.ok(agendamentosDaSemana);
    }

    @GetMapping("/obter-total-clientes")
    public ResponseEntity<Long> getTotalClientes (){
        long totalClientes = service.getTotalDeClientes();
        return ResponseEntity.ok(totalClientes);
    }

    @GetMapping("/total-ganho")
    public ResponseEntity<BigDecimal> getTotalGanho (){
        BigDecimal totalGanho = service.getTotalGanho();
        return ResponseEntity.ok(Objects.requireNonNullElse(totalGanho, BigDecimal.ZERO));
    }

    @GetMapping("/total-faturamento")
    public ResponseEntity<BigDecimal> getFaturamento(){
        BigDecimal faturamento = service.getTotalFaturado();
        return ResponseEntity.ok(Objects.requireNonNullElse(faturamento, BigDecimal.ZERO));
    }

    @GetMapping("/obter-proximos-agendamentos")
    public ResponseEntity<List<AgendamentoResponseDTO>> obterProximosAgendamentos(){
        List<AgendamentoResponseDTO> agendamentosHoje = service.obterProximosAgendamentos();
        return ResponseEntity.ok(agendamentosHoje);
    }

    @GetMapping("/obter-resumo-mensal")
    public ResponseEntity<Map<String, Long>> buscarResumoMensal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

        Map<String, Long> resumo =service.buscarResumoMensal(inicio,fim);
        return ResponseEntity.ok(resumo);
    }
}
