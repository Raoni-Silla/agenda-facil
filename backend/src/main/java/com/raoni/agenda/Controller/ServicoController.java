package com.raoni.agenda.Controller;

import com.raoni.agenda.Service.ServicoService;
import com.raoni.agenda.dto.ClienteRequestDTO;
import com.raoni.agenda.dto.ClienteResponseDTO;
import com.raoni.agenda.dto.ServicoRequestDTO;
import com.raoni.agenda.dto.ServicoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    @PostMapping(path = "/criar")
    public ResponseEntity<ServicoResponseDTO> criarServico (@Valid @RequestBody ServicoRequestDTO servico){
        ServicoResponseDTO servicoCriado = servicoService.criarServico(servico);
        return ResponseEntity.ok(servicoCriado);
    }

    @GetMapping(path = "/verificar")
    public ResponseEntity<Boolean> isExistServicoPorNome (@RequestParam String nome){
        Boolean isExist = servicoService.isExistServicoPorNome(nome);
        return ResponseEntity.ok(isExist);
    }

    @GetMapping("/obterservicos")
    public ResponseEntity<Page<ServicoResponseDTO>> listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca
    ) {
        Page<ServicoResponseDTO> pagina = servicoService.obterServicosPaginados(page, size, busca);
        return ResponseEntity.ok(pagina);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ServicoRequestDTO dto
    ) {
        ServicoResponseDTO atualizado = servicoService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
        servicoService.excluir(id);
        return ResponseEntity.noContent().build(); // Status 204: Sucesso, mas sem corpo na resposta
    }

    @GetMapping("/buscar/{nome}")
    public ResponseEntity<List<ServicoResponseDTO>> buscarPorNome(@PathVariable String nome) {
        List<ServicoResponseDTO> servicos = servicoService.buscarPorNome(nome);
        return ResponseEntity.ok(servicos);
    }
}
