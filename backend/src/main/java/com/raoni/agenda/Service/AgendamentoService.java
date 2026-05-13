package com.raoni.agenda.Service;

import com.raoni.agenda.Enums.StatusAgendamento;
import com.raoni.agenda.Exceptions.ConflitoDeHorarioException;
import com.raoni.agenda.Model.*;
import com.raoni.agenda.Repository.AgendamentoRepository;
import com.raoni.agenda.Repository.ClienteRepository;
import com.raoni.agenda.Repository.ConfiguracaoAgendaRepository;
import com.raoni.agenda.Repository.ServicoRepository;
import com.raoni.agenda.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;
    private final ConfiguracaoAgendaRepository configuracaoAgendaRepository;


    private void mockarDadosConfig(){
        if (configuracaoAgendaRepository.count() == 0) {
            ConfiguracaoAgenda configuracaoAgenda = new ConfiguracaoAgenda();
            configuracaoAgenda.setHoraAbertura(LocalTime.of(8,0));
            configuracaoAgenda.setHoraFechamento(LocalTime.of(18,0));
            configuracaoAgenda.setInicioAlmoco(LocalTime.of(12,0));
            configuracaoAgenda.setFimAlmoco(LocalTime.of(13,0));
            configuracaoAgendaRepository.save(configuracaoAgenda);
        }
    }

    private void validarHorarios (LocalTime inicio, LocalTime fim){
        List<ConfiguracaoAgenda> configuracaoAgenda = configuracaoAgendaRepository.findAll();
        ConfiguracaoAgenda config = configuracaoAgenda.getFirst();

        boolean dentroExpediente = !inicio.isBefore(config.getHoraAbertura()) && !fim.isAfter(config.getHoraFechamento());
        boolean chocaComAlmoco = inicio.isBefore(config.getFimAlmoco()) && fim.isAfter(config.getInicioAlmoco());


        if (!dentroExpediente) {
            throw new ConflitoDeHorarioException("O agendamento está fora do horário de funcionamento.");
        }

        if (chocaComAlmoco) {
            throw new ConflitoDeHorarioException("O agendamento conflita com o horário de almoço do profissional.");
        }

    }

    private void validarConflitosDeAgenda(LocalTime novoInicio, LocalTime novoFim, List<Agendamento> agendamentosDoDia) {
        for (Agendamento existente : agendamentosDoDia) {
            boolean chocaComExistente = novoInicio.isBefore(existente.getHoraFim()) && novoFim.isAfter(existente.getHoraInicio());

            if (chocaComExistente) {
                throw new ConflitoDeHorarioException("Conflito de horário! Já existe um cliente marcado das "
                        + existente.getHoraInicio() + " às " + existente.getHoraFim() + ".");
            }
        }
    }

    private String formatarTelefone(String telefone) {
        if (telefone.length() == 11) {
            return telefone.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        } else if (telefone.length() == 10) {
            return telefone.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
        }
        return telefone;
    }

    private ClienteResponseDTO gerarClienteResponseDTO (Cliente cliente){
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
                cliente.getId(),
                cliente.getNome(),
                formatarTelefone(cliente.getTelefone()),
                visitas,
                ultima
        );
    }


    private ServicoResponseDTO gerarServicoResponseDTO (Servico servico){
        return new ServicoResponseDTO(servico.getId(),servico.getNome(),servico.getValor(),servico.getDuracaoMinutos());
    }

    private ItemAgendamentoResponseDTO gerarItemAgendamentoResponseDTO(ItemAgendamento itemAgendamento){
        return new ItemAgendamentoResponseDTO(itemAgendamento.getId(),gerarServicoResponseDTO(itemAgendamento.getServico()),itemAgendamento.getValorCobrado());
    }

    private AgendamentoResponseDTO criarResponseDTO (Agendamento agendamento){
        return new AgendamentoResponseDTO(agendamento.getId(), gerarClienteResponseDTO(agendamento.getCliente()),agendamento.getItens().stream().map(this::gerarItemAgendamentoResponseDTO).toList(),agendamento.getData(),agendamento.getHoraInicio(),agendamento.getHoraFim(),agendamento.getValorTotal(),agendamento.getStatus());
    }

    @Transactional
    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO dto){
        Cliente cliente = clienteRepository.findById(dto.idCliente()).orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        List<Servico> listaServicos = servicoRepository.findByIdIn(dto.servicos());

        long minTotais = listaServicos.stream().mapToLong(Servico::getDuracaoMinutos).sum();
        LocalTime horaFim = dto.horaInicio().plusMinutes(minTotais);
        BigDecimal valorTotal = listaServicos.stream().map(Servico::getValor).reduce(BigDecimal.ZERO,BigDecimal::add);
        mockarDadosConfig();
        validarHorarios(dto.horaInicio(),horaFim);
        List<Agendamento> agendamentosDoDia = agendamentoRepository.findByData(dto.data());
        validarConflitosDeAgenda(dto.horaInicio(), horaFim, agendamentosDoDia);
        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setData(dto.data());
        agendamento.setHoraInicio(dto.horaInicio());
        agendamento.setHoraFim(horaFim);
        agendamento.setValorTotal(valorTotal);
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        for (Servico servico : listaServicos){
            ItemAgendamento itemAgendamento = new ItemAgendamento();
            itemAgendamento.setAgendamento(agendamento);
            itemAgendamento.setValorCobrado(servico.getValor());
            itemAgendamento.setServico(servico);
            agendamento.getItens().add(itemAgendamento);
        }
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);

        return criarResponseDTO(agendamentoSalvo);

    }


    public Page<AgendamentoResponseDTO> obterAgendamentosPaginados(int numeroPag, int tamanhoPag, String busca) {

        Pageable paginacao = PageRequest.of(numeroPag, tamanhoPag);
        Page<Agendamento> paginaDeAgendamentos;

        if (busca == null || busca.trim().isEmpty()) {
            paginaDeAgendamentos = agendamentoRepository.findAll(paginacao);
        } else {
            paginaDeAgendamentos = agendamentoRepository.pesquisarPorNomeOuTelefone(busca, paginacao);
        }
        return paginaDeAgendamentos.map(this::criarResponseDTO);
    }

    public Page<AgendamentoResponseDTO> obterAgendamentosPaginadosConcluidos(int numeroPag, int tamanhoPag, String busca) {
        Pageable paginacao = PageRequest.of(numeroPag, tamanhoPag);
        Page<Agendamento> pagina;

        if (busca == null || busca.trim().isEmpty()) {
            pagina = agendamentoRepository.findAllByStatus(StatusAgendamento.CONCLUIDO, paginacao);
        } else {
            pagina = agendamentoRepository.pesquisarPorNomeOuTelefone(busca, paginacao);
        }

        return pagina.map(this::criarResponseDTO);
    }

    public Page<AgendamentoResponseDTO> obterAgendamentosPaginadosPendentes(int numeroPag, int tamanhoPag, String busca) {
        Pageable paginacao = PageRequest.of(numeroPag, tamanhoPag);
        Page<Agendamento> pagina;

        if (busca == null || busca.trim().isEmpty()) {
            pagina = agendamentoRepository.findAllByStatus(StatusAgendamento.PENDENTE, paginacao);
        } else {
            pagina = agendamentoRepository.pesquisarPorNomeOuTelefone(busca, paginacao);
        }

        return pagina.map(this::criarResponseDTO);
    }

    @Transactional
    public AgendamentoResponseDTO concluirAgendamento(long id){
        Agendamento agendamento = agendamentoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Não foi possivel encontrar o agendamento"));
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        Agendamento agendamentoSalvo = agendamentoRepository.save(agendamento);
        return criarResponseDTO(agendamentoSalvo);
    }

    @Transactional
    public void excluirAgendamento(long id){
        if (agendamentoRepository.existsById(id)){
            agendamentoRepository.deleteById(id);
        }else {
            throw new RuntimeException("Cliente não encontrado");
        }
    }

    public long getQuantidadeDeAgendamentosHoje(){
        return agendamentoRepository.countByData(LocalDate.now());
    }

    public long getQuantidadeAgendamentoSemana(){
        LocalDate hoje = LocalDate.now();
        LocalDate segunda = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sabado = hoje.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return agendamentoRepository.countByDataBetween(segunda, sabado);
    }

    public long getTotalDeClientes (){
        return  clienteRepository.countTotalClientes();
    }

    public BigDecimal getTotalGanho(){
        return agendamentoRepository.somarFaturamentoBruto();
    }

    public BigDecimal getTotalFaturado() {
        BigDecimal faturamentoBruto = Optional.ofNullable(agendamentoRepository.somarFaturamentoBruto())
                .orElse(BigDecimal.ZERO);

        BigDecimal resultado = faturamentoBruto.subtract(BigDecimal.valueOf(5));

        // Se o resultado for menor que zero, retorna zero para não quebrar o dashboard
        return resultado.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : resultado;
    }

    public List<AgendamentoResponseDTO> obterProximosAgendamentos(){
        List <Agendamento> agendamentos = agendamentoRepository.findByData(LocalDate.now()).stream().sorted(Comparator.comparing(Agendamento::getData)).toList();
        return agendamentos.stream().map(this::criarResponseDTO).toList();
    }


    public Map<String, Long> buscarResumoMensal(LocalDate inicio, LocalDate fim) {
        List<AgendamentoResumoDTO> lista = agendamentoRepository.contarPorPeriodo(inicio, fim);
        return lista.stream().collect(Collectors.toMap(
                item -> item.getData().toString(), // Chave: "2026-05-13"
                AgendamentoResumoDTO::getTotal    // Valor: 5
        ));
    }
}
