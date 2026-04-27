import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { debounceTime, distinctUntilChanged, Subject, Subscription } from 'rxjs';
import { ClienteService } from '../../service/cliente-service';
import { ClienteResponseDTO } from '../../models/ClienteResponseDTO.dto';

@Component({
  selector: 'app-criar-cliente',
  imports: [RouterLink, CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './criar-cliente.html',
  styleUrl: './criar-cliente.css',
})

/*O usuário digita → O Angular segura a onda por 500ms → Manda pro Java → O Java faz um existsBy super rápido no banco de dados e devolve true/false → O Angular libera ou bloqueia o botão → Ao salvar, o Java finalmente grava a informação no banco com o save().*/

export class CriarCliente implements OnDestroy {
  nome = '';
  telefone = ''; 
  isNotValid = false;
  isCreated = false;
  isExist = false;

  ouvidor = new Subject<string>();

  private sub: Subscription;

  constructor(
    private clientesService: ClienteService,
    private router: Router,
  ) { //debounceTime diz pra chamar o metodo apenas meio segundo depois que o usuario parar de interagir, e se for diferente do ultimo numero testado
    this.sub = this.ouvidor.pipe(debounceTime(500), distinctUntilChanged()).subscribe((numeroFinal) => {
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
    if (texto.length >= 8) {
      this.ouvidor.next(texto); //Se o número digitado tiver 8 ou mais caracteres, ele envia o texto para o ouvidor (this.ouvidor.next(texto))
    }
  }

  //Após o usuário digitar 8+ números e fazer a pausa de meio segundo, o ouvidor desperta e aciona este método silenciosamente.
  buscarClientePeloNumero(telefone: string) {
    this.clientesService.buscarPorTelefone(telefone).subscribe({
      next: (resposta: boolean) => {
        this.isExist = resposta;
      },
      error: (erro) => {
        this.isExist = false;
      },
    });
  }

  validarDados() {

    const nomeValido = this.nome?.trim().length >= 3; //Verifica se tanto o nome quanto o telefone possuem pelo menos 3 caracteres removendo os espaços vazios (trim())

    const telefoneValido = this.telefone?.trim().length >= 10; //Se a validação falhar, ou se o sistema já souber que o telefone é duplicado (if(this.isExist) return;), a função é interrompida imediatamente e não faz nada

    this.isNotValid = !nomeValido || !telefoneValido;

    if (this.isNotValid) return;

    if(this.isExist) return;

    this.salvarDados();

  }

  salvarDados() {
    const cliente = this.clientesService.transformarClienteEmClienteRequest(
      this.nome,
      this.telefone,
    );

    this.clientesService.criarCliente(cliente).subscribe({
      next: (res) => {
        console.log('Cliente criado:', res);
        this.isCreated = true;
        this.nome = '';
        this.telefone = '';
        this.router.navigate(['clientes']);
      },
      error: (err) => {
        console.error('Erro ao criar cliente:', err);
        this.isCreated = false;
      },
    });
  }

  
}
