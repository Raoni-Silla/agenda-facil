import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../service/cliente-service';
import { ClienteRequestDTO } from '../../models/ClienteRequestDTO.dto';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';

@Component({
  selector: 'app-modal-edicao',
  standalone: true,
  imports: [
    MatDialogModule,
    MatButtonModule,
    CommonModule,
    FormsModule,
    NgxMaskDirective,
    NgxMaskPipe,
  ],
  templateUrl: './modal-edicao.html',
  styleUrl: './modal-edicao.css',
})
export class ModalEdicao {
  public cliente: any;
  isNotValid = false;
  isExist = false;

  constructor(
    public dialogRef: MatDialogRef<ModalEdicao>,
    @Inject(MAT_DIALOG_DATA) public data: { clienteSelecionado: any },
    private clienteService: ClienteService
  ) {
    this.cliente = { ...data.clienteSelecionado };
  }

  validarDados() {
    const nomeValido = this.cliente.nome?.trim().length >= 3;
    const telefoneValido = this.cliente.telefone?.trim().length >= 10;

    this.isNotValid = !nomeValido || !telefoneValido;

    if (this.isNotValid) return;

    this.salvar();
  }

  salvar() {
    const dto: ClienteRequestDTO = {
      nome: this.cliente.nome,
      telefone: this.cliente.telefone,
    };

    
    this.clienteService.atualizarCliente(this.cliente.id, dto).subscribe({
      next: (res) => {
        this.dialogRef.close(true);
      },
      error: (err) => {
       
        console.error('Erro ao editar cliente:', err);
        const mensagemErro = typeof err.error === 'string' ? err.error : 
                             (err.error?.message || 'Este telefone já pertence a outro cliente.');
        
        alert('Atenção: ' + mensagemErro);
      },
    });
  }
}