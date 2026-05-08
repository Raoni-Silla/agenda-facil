package com.raoni.agenda.Service;

import com.raoni.agenda.Model.Servico;
import com.raoni.agenda.Repository.ServicoRepository;
import com.raoni.agenda.dto.ServicoRequestDTO;
import com.raoni.agenda.dto.ServicoResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    private ServicoResponseDTO transformarEmResponse (Servico servico){
        return new ServicoResponseDTO(servico.getId(),servico.getNome(),servico.getValor(),servico.getDuracaoMinutos());
    }

    @Transactional
    public ServicoResponseDTO criarServico (ServicoRequestDTO dto){
        Servico servico = new Servico();
        servico.setNome(dto.nome().trim().toLowerCase());
        servico.setValor(dto.valor());
        servico.setDuracaoMinutos(dto.duracaoMinutos());
        Servico salved = servicoRepository.save(servico);
        return transformarEmResponse(salved);
    }

    public Boolean isExistServicoPorNome (String nome){
        if (nome.trim().isEmpty()){
            throw new EntityNotFoundException("Nome passado como parametro é nulo ou vazio");
        }
        return servicoRepository.existsByNome(nome);
    }

    public Page<ServicoResponseDTO> obterServicosPaginados(int numeroPag, int tamanhoPag, String busca) {

        // 1. Dizemos a ele qual página queremos e quantos itens cabem nela.
        Pageable paginacao = PageRequest.of(numeroPag, tamanhoPag);
        Page<Servico> paginaDeClientes;

        // 2. A bifurcação: Tem busca ou não tem?
        if (busca == null || busca.trim().isEmpty()) {
            paginaDeClientes = servicoRepository.findAll(paginacao);
        } else {
            paginaDeClientes = servicoRepository.findByNomeContainingIgnoreCase(busca, paginacao);
        }
        return paginaDeClientes.map(this::transformarEmResponse);
    }

    @Transactional
    public ServicoResponseDTO atualizar(Long id, @Valid ServicoRequestDTO dto) {

        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));



        String nome = dto.nome().trim();

        if (servicoRepository.existsByNomeAndIdNot(nome, id)){
            throw new IllegalArgumentException("Este nome já pertence a outro cliente.");
        }

        // 4. Se passou pela barreira, atualiza e salva
        servico.setNome(dto.nome().trim());
        servico.setValor(dto.valor());
        servico.setDuracaoMinutos(dto.duracaoMinutos());

         Servico salvo = servicoRepository.save(servico);
        return transformarEmResponse(servico);
    }

    public void excluir(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new RuntimeException("Serviço não encontrado");
        }
        servicoRepository.deleteById(id);
    }

    public List<ServicoResponseDTO> buscarPorNome(String nome) {
        return servicoRepository.findByNomeContainingIgnoreCase(nome)
                .stream()
                .map(servico -> new ServicoResponseDTO(
                        servico.getId(),
                        servico.getNome(),
                        servico.getValor(),
                        servico.getDuracaoMinutos()
                ))
                .toList();
    }
}
