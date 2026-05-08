import { Component, OnInit, signal } from '@angular/core';
import { ThemeService } from '../../service/theme-service';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { ServicoService } from '../../service/servico-service';
import { ServicoResponseDTO } from '../../models/ServicoResponseDTO.dto';
import { debounceTime, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { ModalEdicaoServicos } from '../../components/modal-edicao-servicos/modal-edicao-servicos';

@Component({
  selector: 'app-servicos',
  imports: [RouterLink, CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './servicos.html',
  styleUrl: './servicos.css',
})
export class Servicos implements OnInit {
  paginaAtual = signal<number>(0);
  totalDePaginas = signal<number>(0);
  tamanhoPagina = 10;
  termoBusca = signal<string>('');
  private sub: Subscription;
  ouvidor = new Subject<string>();
  corCard = '';

  constructor(
    public themeService: ThemeService,
    private servicoService: ServicoService,
    public dialog: MatDialog,
  ) {
    this.sub = this.ouvidor
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe((pesquisa) => {
        this.termoBusca.set(pesquisa as string);
        this.paginaAtual.set(0);
        this.carregarListaDeServicos();
      });
  }

  servicos = signal<ServicoResponseDTO[]>([]);

  ngOnInit(): void {
    this.carregarListaDeServicos();
    this.corCard = this.themeService.getCorAleatoria();
  }


  carregarListaDeServicos() {
    this.servicoService
      .obterServicosPaginados(this.paginaAtual(), this.tamanhoPagina, this.termoBusca())
      .subscribe({
        next: (resposta) => {
          this.servicos.set(resposta.content);
          this.totalDePaginas.set(resposta.totalPages);
        },
        error: (err) => console.error(err),
      });
  }

  aoDigitarNaBusca(texto: string) {
    this.ouvidor.next(texto);
  }

  proximaPagina() {
    if (this.paginaAtual() < this.totalDePaginas() - 1) {
      this.paginaAtual.update((p) => p + 1);
      this.carregarListaDeServicos();
    }
  }

  paginaAnterior() {
    if (this.paginaAtual() > 0) {
      this.paginaAtual.update((p) => p - 1);
      this.carregarListaDeServicos();
    }
  }

  abrirModal(servico: any) {
    if (!servico) return;
    const referenciaDoModal = this.dialog.open(ModalEdicaoServicos, {
      width: '400px',
      data: { servicoSelecionado: servico },
    });

    referenciaDoModal.afterClosed().subscribe((resultado) => {
      if (resultado === true) {
        console.log('O modal avisou que salvou! Hora de atualizar a tela.');
        this.carregarListaDeServicos();
      }
    });
  }

  deletarServico(id : number){
    if(!id) return;
    if(confirm('Tem certeza que deseja excluir esse serviço?')){
      this.servicoService.excluirServico(id).subscribe({
        next: () => this.carregarListaDeServicos(),
        error: (err) => console.log(err)
      })
    }
  }
}
