package com.raoni.agenda.Service;

import com.raoni.agenda.Model.Agendamento;
import com.raoni.agenda.Model.Cliente;
import com.raoni.agenda.Repository.ClienteRepository;
import com.raoni.agenda.dto.ClienteRequestDTO;
import com.raoni.agenda.dto.ClienteResponseDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private String normalizarTelefone(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\D", "");

    }

    private boolean telefoneValido(String telefone) {
        return telefone.matches("\\d{10,11}");
    }

    private String formatarTelefone(String telefone) {
        if (telefone.length() == 11) {
            return telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (telefone.length() == 10) {
            return telefone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }

    private ClienteResponseDTO formatarResponseDTO(Cliente cliente) {
        int visitas = 0;
        LocalDateTime ultima = null;


        if (cliente.getAgendamentos() != null && !cliente.getAgendamentos().isEmpty()) {
            visitas = cliente.getAgendamentos().size();
            ultima = cliente.getAgendamentos().stream()
                    .map(Agendamento::getDataHora) // Supondo que a data fique no atributo 'data'
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
        }

        return new ClienteResponseDTO(
                cliente.getId(), // Adiciona o ID aqui!
                cliente.getNome(),
                formatarTelefone(cliente.getTelefone()),
                visitas,
                ultima
        );
    }

    public boolean verificarSeClienteExiste(String telefone) {
        String telefoneLimpo = normalizarTelefone(telefone);
        if (!telefoneValido(telefoneLimpo)) {
            return false;
        }
        return clienteRepository.existsByTelefone(telefoneLimpo);
    }


    @Transactional
    public ClienteResponseDTO criarCliente(ClienteRequestDTO clienteRequestDTO) {
        // 1. Limpamos o telefone que veio do Angular
        String telefoneLimpo = normalizarTelefone(clienteRequestDTO.telefone());

        // 2. A Validação: Se já existir no banco, a gente "quebra" a requisição aqui mesmo
        if (clienteRepository.existsByTelefone(telefoneLimpo)) {
            throw new IllegalArgumentException("Este telefone já está cadastrado no sistema.");
        }

        // 3. Se passou pela barreira, salva normal
        Cliente cliente = new Cliente();
        cliente.setNome(clienteRequestDTO.nome().trim());
        cliente.setTelefone(telefoneLimpo); // Passamos a variável que já está limpa

        clienteRepository.save(cliente);
        return formatarResponseDTO(cliente);
    }

    public List<ClienteResponseDTO> obterTodosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::formatarResponseDTO)
                .toList();
    }
    public Page<ClienteResponseDTO> obterClientesPaginados(int numeroPag, int tamanhoPag, String busca) {

        // 1. Dizemos a ele qual página queremos e quantos itens cabem nela.
        Pageable paginacao = PageRequest.of(numeroPag, tamanhoPag);
        Page<Cliente> paginaDeClientes;

        // 2. A bifurcação: Tem busca ou não tem?
        if (busca == null || busca.trim().isEmpty()) {
            paginaDeClientes = clienteRepository.findAll(paginacao);
        } else {
            paginaDeClientes = clienteRepository.pesquisarPorNomeOuTelefone(busca, paginacao);
        }
        return paginaDeClientes.map(this::formatarResponseDTO);
    }

    @Transactional
    public void excluir(Long id) {
        // É boa prática verificar se o cara existe antes de tentar deletar
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        // 1. Busca o cliente ou morre tentando
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 2. Limpamos o telefone que veio do Angular
        String telefoneLimpo = normalizarTelefone(dto.telefone());

        // 3. A Validação: Procura esse telefone em QUALQUER cliente, MENOS nesse ID
        if (clienteRepository.existsByTelefoneAndIdNot(telefoneLimpo, id)) {
            throw new IllegalArgumentException("Este telefone já pertence a outro cliente.");
        }

        // 4. Se passou pela barreira, atualiza e salva
        cliente.setNome(dto.nome().trim());
        cliente.setTelefone(telefoneLimpo);

        Cliente salvo = clienteRepository.save(cliente);
        return formatarResponseDTO(salvo);
    }
}
