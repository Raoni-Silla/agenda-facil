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
        Cliente cliente = new Cliente();
        cliente.setNome(clienteRequestDTO.nome().trim());
        cliente.setTelefone(normalizarTelefone(clienteRequestDTO.telefone()));
        clienteRepository.save(cliente);
        return formatarResponseDTO(cliente);
    }

    public List<ClienteResponseDTO> obterTodosClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::formatarResponseDTO)
                .toList();
    }

    public Page<ClienteResponseDTO> obterClientesPaginados (int numeroPag, int tamanhoPag){

        // Dizemos a ele qual página queremos e quantos itens cabem nela.
        Pageable paginacao = PageRequest.of(numeroPag, tamanhoPag);

        //Vamos no repositório. O findAll do Spring Data JPA já aceita paginação nativamente!
        // Ele não vai mais trazer uma List<Cliente>, e sim um Page<Cliente> (uma página com os 10 clientes dentro).
        Page<Cliente> paginaDeClientes = clienteRepository.findAll(paginacao);

        // Ele passa por cada cliente dessa página de 10 e converte em DTO,
        // mantendo toda a estrutura de paginação (total de páginas, página atual, etc).
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
        cliente.setNome(dto.nome());
        cliente.setTelefone(normalizarTelefone(dto.telefone()));
        Cliente salvo = clienteRepository.save(cliente);
        return formatarResponseDTO(salvo);
    }
}
