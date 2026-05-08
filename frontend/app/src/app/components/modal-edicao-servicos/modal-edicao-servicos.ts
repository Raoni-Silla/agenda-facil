import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { ServicoService } from '../../service/servico-service';
import { ServicoRequestDTO } from '../../models/ServicoRequestDTO.dto';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';

@Component({
  selector: 'app-modal-edicao-servicos',
  imports: [
    MatDialogModule,
    MatButtonModule,
    CommonModule,
    FormsModule,
    NgxMaskDirective,
    NgxMaskPipe,
  ],
  templateUrl: './modal-edicao-servicos.html',
  styleUrl: './modal-edicao-servicos.css',
})
export class ModalEdicaoServicos {
  public servico: any;
  isNotValid = false;
  isExist = false;

  constructor(
    public dialogRef: MatDialogRef<ModalEdicaoServicos>,
    @Inject(MAT_DIALOG_DATA) public data: { servicoSelecionado: any },
    private servicoService: ServicoService,
  ) {
    this.servico = { ...data.servicoSelecionado };
  }

  validarDados() {
    const nomeValido = this.servico.nome?.trim().length >= 3;
    const valorValido = this.servico.valor > 0;
    const minutosValido = this.servico.duracaoMinutos > 0;

    this.isNotValid = !nomeValido || !valorValido || !minutosValido;

    if (this.isNotValid) return;

    this.salvar();
  }

  salvar() {
    const dto: ServicoRequestDTO = {
      nome: this.servico.nome,
      valor: this.servico.valor,
      duracaoMinutos: this.servico.duracaoMinutos,
    };

    this.servicoService.atualizarServico(this.servico.id, dto).subscribe({
      next: (res) => {
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Erro ao editar cliente:', err);
        const mensagemErro =
          typeof err.error === 'string'
            ? err.error
            : err.error?.message || 'Não foi possivel editar esse serviço';
        alert('Atenção: ' + mensagemErro);
      },
    });
  }
}
