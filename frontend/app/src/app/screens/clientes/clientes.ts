import { Component, computed, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ClienteResponseDTO } from '../../models/ClienteResponseDTO.dto';
import { ClienteService } from '../../service/cliente-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { debounceTime, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { ThemeService } from '../../service/theme-service';
import { MatDialog } from '@angular/material/dialog';
import { ModalEdicao } from '../../components/modal-edicao/modal-edicao';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './clientes.html',
  styleUrl: './clientes.css',
})
export class Clientes implements OnInit {
  clientes = signal<ClienteResponseDTO[]>([]);
  termoBusca = signal<string>(''); //oque o usuario digitou
  ouvidor = new Subject<string>();
  paginaAtual = signal<number>(0);
  totalDePaginas = signal<number>(0);
  tamanhoPagina = 10; // Fixo em 10 por enquanto
  private sub: Subscription;
  corCard = '';

  //clientes filtrados pega a lista que ja existe
  clientesFiltrados = computed(() => {
    const termo = this.termoBusca().toLowerCase(); //pega oque o usuario digitou e joga pra minusculo

    if (!termo) {
      //se o usuario nao digitou nada, carrega a lista normal
      return this.clientes();
    }

    return this.clientes().filter(
      (
        cliente, //se não filtra pelo termo digitado
      ) => cliente.nome.toLowerCase().includes(termo) || cliente.telefone.includes(termo),
    );
  });

  constructor(
    private clientesService: ClienteService,
    public themeService: ThemeService,
    public dialog: MatDialog,
  ) {
    this.sub = this.ouvidor
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((pesquisa) => {
        this.termoBusca.set(pesquisa as string); //acorda a variavel signals
      });
  }

  //quando o usuario digita, o ouvidor acorda e a variavel com signals
  aoDigitarNaBusca(texto: string) {
    this.ouvidor.next(texto);
  }

  ngOnInit(): void {
    this.carregarListaDeClientes();
    this.corCard = this.themeService.getCorAleatoria();
  }

  carregarListaDeClientes() {
    // Passamos o valor do signal paginaAtual()
    this.clientesService.obterClientesPaginados(this.paginaAtual(), this.tamanhoPagina).subscribe({
      next: (resposta) => {
        // O Java manda os dados dentro de 'content'
        this.clientes.set(resposta.content);

        // Guardamos o total de páginas que o Java calculou pra nós
        this.totalDePaginas.set(resposta.totalPages);
      },
      error: (err) => console.error(err),
    });
  }

  proximaPagina() {
    if (this.paginaAtual() < this.totalDePaginas() - 1) {
      this.paginaAtual.update((p) => p + 1);
      this.carregarListaDeClientes();
    }
  }

  paginaAnterior() {
    if (this.paginaAtual() > 0) {
      this.paginaAtual.update((p) => p - 1);
      this.carregarListaDeClientes();
    }
  }

  pegarIniciais(nome: string): string {
    if (!nome) return '';
    const nomes = nome.trim().split(' ');
    if (nomes.length === 1) {
      return nomes[0].charAt(0).toUpperCase();
    }

    return (nomes[0].charAt(0) + nomes[nomes.length - 1].charAt(0)).toUpperCase();
  }

  deletarCliente(id: number | undefined) {
    // Aceita undefined aqui
    if (!id) return; // Se não tiver ID, nem continua

    if (confirm('Tem certeza que deseja excluir este cliente?')) {
      this.clientesService.excluirCliente(id).subscribe({
        next: () => this.carregarListaDeClientes(),
        error: (err) => console.error(err),
      });
    }
  }

  abrirModal(cliente: any) {
    if (!cliente) return;
    const referenciaDoModal = this.dialog.open(ModalEdicao, {
      width: '400px',
      data: { clienteSelecionado: cliente },
    });

    referenciaDoModal.afterClosed().subscribe((resultado) => {
      if (resultado === true) {
        console.log('O modal avisou que salvou! Hora de atualizar a tela.');
        this.carregarListaDeClientes();
      }
    });
  }
}
