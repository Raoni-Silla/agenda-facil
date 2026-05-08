import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, of, Subject, Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';

import { AgendamentoResponseDTO } from '../../models/AgendamentoResponseDTO.dto';
import { AgendamentoService } from '../../service/agendamento-service';
import { RouterLink } from '@angular/router';
import { ItemAgendamentoResponseDTO } from '../../models/ItemAgendamentoResponse.dto';
// import { ThemeService } from '../../service/theme.service';

@Component({
  selector: 'app-agenda',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './agenda.html',
  styleUrl: './agenda.css',
})
export class Agenda implements OnInit, OnDestroy {
  agendamentos = signal<AgendamentoResponseDTO[]>([]);
  termoBusca = signal<string>('');
  ouvidor = new Subject<string>();
  paginaAtual = signal<number>(0);
  totalDePaginas = signal<number>(0);
  tamanhoPagina = 10;

  sub!: Subscription;
  corCard!: string;

  constructor(
    private agendamentoService: AgendamentoService,
    public dialog: MatDialog,
  ) {
    this.sub = this.ouvidor
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((pesquisa) => {
        this.termoBusca.set(pesquisa as string);
        this.paginaAtual.set(0);
        this.carregarListaDeAgendamentos();
      });
  }

  ngOnInit(): void {
    this.carregarListaDeAgendamentos();
  }

  carregarListaDeAgendamentos() {
    this.agendamentoService
      .obterAgendamentosPaginados(this.paginaAtual(), this.tamanhoPagina, this.termoBusca())
      .subscribe({
        next: (resposta) => {
          this.agendamentos.set([...resposta.content]);
          this.totalDePaginas.set(resposta.totalPages);
        },
        error: (err) => console.error(err),
      });
  }

  filtrarTodos() {
    this.carregarListaDeAgendamentos();
  }

  filtrarConcluidos() {
    this.agendamentoService
      .filtrarConcluidos(this.paginaAtual(), this.tamanhoPagina, this.termoBusca())
      .subscribe({
        next: (resposta) => {
          this.agendamentos.set([...resposta.content]);
          this.totalDePaginas.set(resposta.totalPages);
        },
        error: (err) => console.log(err),
      });
  }

  filtrarPendentes() {
    this.agendamentoService
      .filtrarPendentes(this.paginaAtual(), this.tamanhoPagina, this.termoBusca())
      .subscribe({
        next: (resposta) => {
          this.agendamentos.set([...resposta.content]);
          this.totalDePaginas.set(resposta.totalPages);
        },
        error: (err) => console.log(err),
      });
  }

  pegarIniciais(nome: string): string {
    if (!nome) return '';
    const nomes = nome.trim().split(' ');
    if (nomes.length === 1) {
      return nomes[0].charAt(0).toUpperCase();
    }

    return (nomes[0].charAt(0) + nomes[nomes.length - 1].charAt(0)).toUpperCase();
  }

  proximaPagina() {
    if (this.paginaAtual() < this.totalDePaginas() - 1) {
      this.paginaAtual.update((p) => p + 1);
      this.carregarListaDeAgendamentos();
    }
  }

  paginaAnterior() {
    if (this.paginaAtual() > 0) {
      this.paginaAtual.update((p) => p - 1);
      this.carregarListaDeAgendamentos();
    }
  }

  aoDigitarNaBusca(texto: string) {
    this.ouvidor.next(texto);
  }

  formatarItens(itens: ItemAgendamentoResponseDTO[]): string {
    if (!itens || itens.length === 0) return 'Nenhum serviço';

    return itens.map((item) => item.servico.nome).join(' + ');
  }

  concluir(id: number) {
    this.agendamentoService.concluirAgendamento(id).subscribe({
      next: (res) => {
        this.carregarListaDeAgendamentos();
      },
      error: (err) => {
        console.log(err);
      },
    });
  }

  excluir(id: number) {
    if (!id) return;
    if (confirm('Deseja mesmo excluir esse agendamento?')) {
      this.agendamentoService.excluirAgendamento(id).subscribe({
        next: () => {
          this.carregarListaDeAgendamentos();
        },
        error: (err) => console.error('Erro ao excluir:', err),
      });
    }
  }

  ngOnDestroy(): void {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }
}
