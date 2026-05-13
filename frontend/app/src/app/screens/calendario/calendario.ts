import { CommonModule } from '@angular/common';
import { Component, OnInit, signal } from '@angular/core';
import { CalendarModule } from 'angular-calendar';
import { AgendamentoService } from '../../service/agendamento-service';

@Component({
  selector: 'app-calendario',
  imports: [CalendarModule, CommonModule],
  templateUrl: './calendario.html',
  styleUrl: './calendario.css',
})
export class Calendario implements OnInit {
  dataAtual: Date = new Date();
  resumoMensal = signal<Record<string, number>>({});

  constructor(private service: AgendamentoService) {}

  ngOnInit(): void {
    // Carrega os agendamentos do mês assim que a tela abre
    this.buscarDadosDoMesAtual();
  }

  mudarMes(direcao: number) {
    const novaData = new Date(this.dataAtual);
    novaData.setMonth(novaData.getMonth() + direcao);
    this.dataAtual = novaData;
    this.buscarDadosDoMesAtual();
  }

  buscarDadosDoMesAtual() {
    const inicio = new Date(this.dataAtual.getFullYear(), this.dataAtual.getMonth(), 1);
    const fim = new Date(this.dataAtual.getFullYear(), this.dataAtual.getMonth() + 1, 0);

    this.carregarResumo(this.formatarDataIso(inicio), this.formatarDataIso(fim));
  }

  carregarResumo(inicio: string, fim: string) {
    this.service.getResumoMensal(inicio, fim).subscribe({
      next: (dados) => this.resumoMensal.set(dados),
      error: (err) => console.error('Erro ao carregar resumo', err),
    });
  }

  formatarDataIso(data: Date): string {
    const ano = data.getFullYear();
    const mes = String(data.getMonth() + 1).padStart(2, '0');
    const dia = String(data.getDate()).padStart(2, '0');
    return `${ano}-${mes}-${dia}`;
  }

  obterTotalDoDia(dataIso: string): number {
    return this.resumoMensal()[dataIso] || 0;
  }
}
