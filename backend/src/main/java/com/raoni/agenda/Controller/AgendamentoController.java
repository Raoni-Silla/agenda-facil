package com.raoni.agenda.Controller;

import com.raoni.agenda.Service.AgendamentoService;
import com.raoni.agenda.dto.AgendamentoRequestDTO;
import com.raoni.agenda.dto.AgendamentoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
