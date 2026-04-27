import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ClienteService } from '../../service/cliente-service';
import { ClienteRequestDTO } from '../../models/ClienteRequestDTO.dto';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Router } from '@angular/router';

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
export class ModalEdicao implements OnDestroy {
  public cliente: any;
  public telefoneOriginal: string; // Guardamos o original para comparar depois

  isNotValid = false;
  isExist = false;

  ouvidor = new Subject<string>();
  private sub: Subscription;

  constructor(
    public dialogRef: MatDialogRef<ModalEdicao>,
    @Inject(MAT_DIALOG_DATA) public data: { clienteSelecionado: any },
    private clienteService: ClienteService,
    private router: Router,
  ) {
    this.cliente = { ...data.clienteSelecionado };

    this.telefoneOriginal = this.limparTelefone(this.cliente.telefone);

    this.sub = this.ouvidor
      .pipe(debounceTime(500), distinctUntilChanged())
      .subscribe((numeroFinal) => {
        this.buscarClientePeloNumero(numeroFinal);
      });
  }

  ngOnDestroy() {
    if (this.sub) {
      this.sub.unsubscribe();
    }
  }

  aoDigitar(texto: string) {
    this.isExist = false;

    const textoLimpo = this.limparTelefone(texto);

   
    if (textoLimpo === this.telefoneOriginal) {
      return; 
    }

    if (textoLimpo.length >= 8) {
      this.ouvidor.next(textoLimpo);
    }
  }

  buscarClientePeloNumero(telefone: string) {
    this.clienteService.buscarPorTelefone(telefone).subscribe({
      next: (resposta: boolean) => {
        this.isExist = resposta;
      },
      error: () => {
        this.isExist = false;
      },
    });
  }

  validarDados() {
    // 1. Usa o this.cliente.nome e this.cliente.telefone (que estão atrelados ao ngModel do HTML)
    const nomeValido = this.cliente.nome?.trim().length >= 3;
    const telefoneValido = this.cliente.telefone?.trim().length >= 10;

    this.isNotValid = !nomeValido || !telefoneValido;

    if (this.isNotValid) return;
    if (this.isExist) return;

    this.salvar();
  }

  salvar() {
    const dto: ClienteRequestDTO = {
      nome: this.cliente.nome,
      telefone: this.cliente.telefone,
    };

    // 2. Muda para o método de ATUALIZAR, passando o ID do cliente e o DTO
    this.clienteService.atualizarCliente(this.cliente.id, dto).subscribe({
      next: (res) => {
        // 3. Fecha o modal avisando o PAI que deu sucesso (true)
        this.dialogRef.close(true);
      },
      error: (err) => {
        console.error('Erro ao editar cliente:', err);
      },
    });
  }

  private limparTelefone(tel: string): string {
    if (!tel) return '';
    return tel.replace(/\D/g, ''); // Remove parênteses, traços, espaços, etc.
  }
}
