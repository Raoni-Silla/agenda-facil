import { CommonModule } from '@angular/common';
import { Component, computed, effect, OnDestroy, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { debounceTime, distinct, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { ClienteService } from '../../service/cliente-service';
import { FormsModule } from '@angular/forms';
import { ClienteResponseDTO } from '../../models/ClienteResponseDTO.dto';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { AgendamentoService } from '../../service/agendamento-service';
import { ServicoService } from '../../service/servico-service';
import { ServicoResponseDTO } from '../../models/ServicoResponseDTO.dto';
import { AgendamentoRequestDTO } from '../../models/AgendamentoRequestDTO.dto';
import { ClienteRequestDTO } from '../../models/ClienteRequestDTO.dto';

@Component({
  selector: 'app-criar-agendamento',
  imports: [RouterLink, CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './criar-agendamento.html',
  styleUrl: './criar-agendamento.css',
})
export class CriarAgendamento implements OnDestroy {
  nomeCliente: string = '';
  telefoneCliente: string = '';
  isInputBloqueado: boolean = true;
  isExist: boolean = true;
  isExistTelefone: boolean = false;
  erroRequisicao: boolean = false;
  idCliente: number | undefined = undefined;
  ouvidorNome = new Subject<string>();
  ouvidorTelefone = new Subject<string>();
  private subNome: Subscription;
  private subTelefone: Subscription;
  isNotValid = false;
  termoBuscaServico = '';
  servicosSelecionados = signal<ServicoResponseDTO[]>([]);
  servicosFiltrados = signal<ServicoResponseDTO[]>([]);
  ouvidorServico = new Subject<string>();
  private subServico: Subscription;
  data: string = '';
  horarioInicio = signal('');
  horarioFim: string = '';
  isHorarioFimManual: boolean = false;

  constructor(
    private agendamentoService: AgendamentoService,
    private clienteService: ClienteService,
    private servicoService: ServicoService,
  ) {
    this.subNome = this.ouvidorNome
      .pipe(debounceTime(200), distinctUntilChanged())
      .subscribe((nome) => this.buscarClientePeloNome(nome));
    this.subTelefone = this.ouvidorTelefone
      .pipe(debounceTime(200), distinctUntilChanged())
      .subscribe((telefone) => this.buscarClientePeloNumero(telefone));
    this.subServico = this.ouvidorServico
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((termo) => this.buscarServicosNoBackend(termo));

    effect(() => {
      const sugestao = this.horarioFimSugerido();

      if (!this.isHorarioFimManual) {
        this.horarioFim = sugestao;
      }
    });
  }

  marcarComoManual() {
    this.isHorarioFimManual = true;
  }

  // Opcional: botão para voltar ao horário sugerido
  resetarParaSugestao() {
    this.isHorarioFimManual = false;
    this.horarioFim = this.horarioFimSugerido();
  }

  valorTotal = computed(() => {
    return this.servicosSelecionados().reduce((acc, servico) => acc + servico.valor, 0);
  });

  duracaoTotal = computed(() => {
    return this.servicosSelecionados().reduce((acc, servico) => acc + servico.duracaoMinutos, 0);
  });

  horarioFimSugerido = computed(() => {
    const inicio = this.horarioInicio();

    if (!inicio || this.duracaoTotal() === 0) return '';

    const [horas, minutos] = inicio.split(':').map(Number);
    const minutosTotais = horas * 60 + minutos + this.duracaoTotal();

    const finalHoras = Math.floor(minutosTotais / 60) % 24;
    const finalMinutos = minutosTotais % 60;

    return `${String(finalHoras).padStart(2, '0')}:${String(finalMinutos).padStart(2, '0')}`;
  });

  ngOnDestroy(): void {
    if (this.subNome || this.subTelefone) {
      this.subNome.unsubscribe();
      this.subTelefone.unsubscribe();
    }
  }

  aoDigitarNome(nome: string) {
    this.telefoneCliente = '';
    this.isExist = true;
    this.erroRequisicao = false;
    this.idCliente = undefined;

    if (nome.length >= 2) {
      this.ouvidorNome.next(nome);
    }
  }

  aoDigitarTelefone(telefone: string) {
    this.isExistTelefone = false;
    this.erroRequisicao = false;

    if (telefone.length >= 8) {
      this.ouvidorTelefone.next(telefone);
    }
  }

  aoDigitarServico(termo: string) {
    if (termo.trim().length >= 2) {
      this.ouvidorServico.next(termo);
    } else {
      this.servicosFiltrados.set([]);
    }
  }

  buscarServicosNoBackend(termo: string) {
    this.servicoService.buscarPorNome(termo).subscribe({
      next: (servicos: ServicoResponseDTO[]) => {
        const selecionadosIds = this.servicosSelecionados().map((s) => s.id);

        const filtrados = servicos.filter((s) => !selecionadosIds.includes(s.id));

        this.servicosFiltrados.set(filtrados);
      },
      error: (err) => {
        console.error('Erro ao buscar serviços no Agenda Fácil:', err);
        this.servicosFiltrados.set([]);
      },
    });
  }

  focarInput(input: HTMLInputElement) {
    input.focus();
  }

  selecionarServico(servico: any) {
    this.servicosSelecionados.update((lista) => [...lista, servico]);

    this.termoBuscaServico = '';
    this.servicosFiltrados.set([]);
  }

  removerServico(servicoParaRemover: any) {
    this.servicosSelecionados.update((lista) =>
      lista.filter((s) => s.id !== servicoParaRemover.id),
    );
  }
  buscarClientePeloNome(nome: string) {
    this.clienteService.getClientePeloNome(nome).subscribe({
      next: (res) => {
        this.telefoneCliente = res.telefone;
        console.log(res.id)
        this.idCliente = res.id;
        this.isInputBloqueado = true;
        this.isExist = true;
        this.erroRequisicao = false;
      },
      error: (err) => {
        this.isInputBloqueado = false;
        if (err.status === 404) {
          this.isExist = false;
          this.erroRequisicao = false;
        } else {
          this.erroRequisicao = true;
          this.isExist = true;
        }
      },
    });
  }

  buscarClientePeloNumero(telefone: string) {
    this.clienteService.buscarPorTelefone(telefone).subscribe({
      next: (resposta: boolean) => {
        this.isExistTelefone = resposta;
      },
      error: (erro) => {
        this.isExistTelefone = false;
      },
    });
  }

  validarDados() {

  const nomeValido = this.nomeCliente?.trim().length >= 3;
  const telefoneValido = this.telefoneCliente?.trim().length >= 10;

  this.isNotValid = !nomeValido || !telefoneValido;
  if (this.isNotValid) return;

  
  const hoje = new Date().toISOString().split('T')[0];
  if (!this.data || this.data < hoje) {
    alert('Selecione uma data válida (hoje ou futura)!');
    return;
  }

  
  if (this.servicosSelecionados().length === 0) {
    alert('Selecione pelo menos um serviço para o agendamento.');
    return;
  }

  
  if (!this.horarioInicio() || !this.horarioFim) {
    alert('Preencha os horários de início e término.');
    return;
  }

  if (this.horarioFim <= this.horarioInicio()) {
    alert('O horário de término não pode ser antes ou igual ao horário de início.');
    return;
  }

  this.salvarDados();
}
transformarAgendamentoEmAgendamentoRequest(): AgendamentoRequestDTO {
  return {
    idCliente: this.idCliente!, 
    data: this.data,
    horaInicio: this.horarioInicio(),
    horaFim: this.horarioFim,
    servicos: this.servicosSelecionados().map(s => s.id)
  };
}

  limparFormulario() {
  // 1. Resetando variáveis de texto e controle
  this.nomeCliente = '';
  this.telefoneCliente = '';
  this.data = '';
  this.horarioFim = '';
  this.termoBuscaServico = '';
  
  // 2. Resetando Flags e IDs
  this.isExist = true;
  this.isHorarioFimManual = false;
  this.idCliente = undefined; 

  // 3. Resetando Signals 
  this.servicosSelecionados.set([]);
  this.servicosFiltrados.set([]);
  this.horarioInicio.set('');
}

  salvarDados() {
    if (this.isExist) {
      this.finalizarAgendamento();
    } else {
      const cliente = this.clienteService.transformarClienteEmClienteRequest(
        this.nomeCliente,
        this.telefoneCliente,
      );

      this.clienteService.criarCliente(cliente).subscribe({
        next: (clienteCriado) => {
          this.idCliente = clienteCriado.id;
          this.isExist = true;
          this.finalizarAgendamento();
        },
        error: (err) => {
          alert('Erro técnico: Não foi possível cadastrar o novo cliente.');
          console.error(err);
        },
      });
    }
  }

  private finalizarAgendamento() {

    console.log('ID no componente no momento do clique:', this.idCliente);
    const agendamento = this.transformarAgendamentoEmAgendamentoRequest();

    this.agendamentoService.criarNovoAgendamento(agendamento).subscribe({
      next: (resposta) => {
        console.log('Sucesso no Agenda Fácil!', resposta);
        alert('Agendamento realizado com sucesso!');
        this.limparFormulario();
      },
      error: (err) => {
        alert('Conflito: Verifique se o horário escolhido já está ocupado.');
        console.error(err);
      },
    });
  }
}
