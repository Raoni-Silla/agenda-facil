import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { CardEstatisticas } from '../../components/card-estatisticas/card-estatisticas';
import { CardAgendamento } from '../../components/card-agendamento/card-agendamento';
import { CardBotao } from '../../components/card-botao/card-botao';
import { RouterLink } from '@angular/router';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AgendamentoService } from '../../service/agendamento-service';
import { forkJoin, Subscription } from 'rxjs';
import { AgendamentoResponseDTO } from '../../models/AgendamentoResponseDTO.dto';

@Component({
  selector: 'app-inicio',
  imports: [
    CardEstatisticas,
    CardAgendamento,
    CardBotao,
    RouterLink,
    CommonModule,
    FormsModule,
    DatePipe,
  ],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio implements OnInit, OnDestroy {
  agendamentosHoje = signal<number>(0);
  agendamentosSemana = signal<number>(0);
  totalClientes = signal<number>(0);
  totalGanho = signal<number>(0);
  faturamento = signal<number>(0);
  proximosAgendamentos = signal<AgendamentoResponseDTO[]>([]);

  private inscricaoMudanca!: Subscription;

  constructor(private agendamentoService: AgendamentoService) {}

  ngOnInit(): void {
    this.carregarDadosIniciais();
    this.inscricaoMudanca = this.agendamentoService.mudancaDados$.subscribe(() => {
      this.carregarDadosIniciais();
    });
    this.obterProximosAgendamentos();
  }

  carregarDadosIniciais() {
    forkJoin({
      hoje: this.agendamentoService.getAgendamentosParaHoje(),
      semana: this.agendamentoService.getAgendamentosParaSemana(),
      clientes: this.agendamentoService.getTotalClientes(),
      faturamento: this.agendamentoService.getTotalFaturamento(),
      ganho: this.agendamentoService.getTotalGanho(),
    }).subscribe({
      next: (res) => {
        this.agendamentosHoje.set(res.hoje);
        this.agendamentosSemana.set(res.semana);
        this.totalClientes.set(res.clientes);
        this.faturamento.set(res.faturamento);
        this.totalGanho.set(res.ganho);
      },
      error: (err) => console.error(err),
    });
  }

  obterProximosAgendamentos() {
    this.agendamentoService.getProximosAgendamentos().subscribe({
      next: (res) => {
        console.log(res);
        this.proximosAgendamentos.set([...res]);
      },
      error: (err) => alert('Não foi possivel trazer os proximos agendamentos'),
    });
  }

  
  formatarHora(hora: string | undefined): string {
    if (!hora) return '--:--'; // Valor padrão para evitar que o layout "pule"
    return hora.slice(0, 5); // Recorta "10:30:00" para "10:30"
  }

  ngOnDestroy(): void {
    if (this.inscricaoMudanca) {
      this.inscricaoMudanca.unsubscribe();
    }
  }
}
