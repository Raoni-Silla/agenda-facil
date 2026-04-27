package com.raoni.agenda.Controller;

import com.raoni.agenda.Service.ClienteService;
import com.raoni.agenda.dto.ClienteRequestDTO;
import com.raoni.agenda.dto.ClienteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping("/verificar")
    public ResponseEntity<Boolean> verificarTelefone(@RequestParam String telefone) {
        boolean existe = clienteService.verificarSeClienteExiste(telefone);
        return ResponseEntity.ok(existe);
    }

    @PostMapping("/criar")
    public ResponseEntity<ClienteResponseDTO> criarCliente (@Valid @RequestBody ClienteRequestDTO cliente){
        ClienteResponseDTO clienteCriado = clienteService.criarCliente(cliente);
        return ResponseEntity.ok(clienteCriado);
    }

    @GetMapping("/obterclientes")
    public ResponseEntity<Page<ClienteResponseDTO>> listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String busca
    ) {
        Page<ClienteResponseDTO> pagina = clienteService.obterClientesPaginados(page, size, busca);
        return ResponseEntity.ok(pagina);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCliente(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build(); // Status 204: Sucesso, mas sem corpo na resposta
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> atualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto
    ) {
        ClienteResponseDTO atualizado = clienteService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

}
