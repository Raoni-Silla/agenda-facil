package com.raoni.agenda.Service;

import com.raoni.agenda.Model.Agendamento;
import com.raoni.agenda.Model.Cliente;
import com.raoni.agenda.Repository.ClienteRepository;
import com.raoni.agenda.dto.ClienteRequestDTO;
import com.raoni.agenda.dto.ClienteResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private String normalizarTelefone(String input) {
        if (input == null) return "";
        return input.trim().replaceAll("\\D", "");

    }

    private String sanitizarEntrada(String nome){
        if (nome.isEmpty()) throw new IllegalArgumentException("Nome passado como paramêtro é nulo");
        return nome.trim().toLowerCase();
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
        LocalDate ultima = null;


        if (cliente.getAgendamentos() != null && !cliente.getAgendamentos().isEmpty()) {
            visitas = cliente.getAgendamentos().size();
            ultima = cliente.getAgendamentos().stream()
                    .map(Agendamento::getData)
                    .max(LocalDate::compareTo)
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

        String telefoneLimpo = normalizarTelefone(clienteRequestDTO.telefone());

        if (clienteRepository.existsByTelefone(telefoneLimpo)) {
            throw new IllegalArgumentException("Este telefone já está cadastrado no sistema.");
        }

        Cliente cliente = new Cliente();
        cliente.setNome(sanitizarEntrada(clienteRequestDTO.nome()));
        cliente.setTelefone(telefoneLimpo);

        clienteRepository.save(cliente);
        return formatarResponseDTO(cliente);
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
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        String telefoneLimpo = normalizarTelefone(dto.telefone());

        if (clienteRepository.existsByTelefoneAndIdNot(telefoneLimpo, id)) {
            throw new IllegalArgumentException("Este telefone já pertence a outro cliente.");
        }

        cliente.setNome(dto.nome().trim());
        cliente.setTelefone(telefoneLimpo);

        Cliente salvo = clienteRepository.save(cliente);
        return formatarResponseDTO(salvo);
    }

    public ClienteResponseDTO obterClientePorNome(String nome){
        String nomeSanitizado = sanitizarEntrada(nome);
        Cliente cliente = clienteRepository.findByNome(nomeSanitizado).orElseThrow(() -> new EntityNotFoundException("Não foi possivel encontrar o cliente"));
        return formatarResponseDTO(cliente);
    }
}
