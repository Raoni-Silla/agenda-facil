import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterLink } from '@angular/router'; 
import { NgxMaskDirective, NgxMaskPipe } from 'ngx-mask';
import { ServicoService } from '../../service/servico-service';

@Component({
  selector: 'app-criar-servico',
  imports: [RouterLink, CommonModule, FormsModule, NgxMaskDirective, NgxMaskPipe],
  templateUrl: './criar-servico.html',
  styleUrl: './criar-servico.css',
})
export class CriarServico {
  nome: string = '';
  valor: number = 0.0;
  duracao: number = 0;
  isNotValid = true; 
  isExist = false;

  constructor(
    private servicoService: ServicoService,
    private router: Router 
  ) {}

  verificarValidade() {
    this.isExist = false; 
    const nomeLimpo = this.nome?.trim();
    this.isNotValid = !nomeLimpo || nomeLimpo.length < 3 || this.valor <= 0 || this.duracao <= 0;
  }

  onSubmit(form: NgForm) {
    if (this.isNotValid) return; 

    const nomeLimpo = this.nome?.trim();

    this.servicoService.isExistServicoPorNome(nomeLimpo).subscribe({
      next: (existe) => {
        if (existe) {
          this.isExist = true;
          console.log('Esse Serviço já existe');
        } else {
          this.salvarServico();
        }
      },
      error: (err) => {
        console.error('Erro na validação:', err);
        alert('Erro ao verificar disponibilidade do nome.');
      },
    });
  }

  salvarServico() {
    const servico = this.servicoService.transformarServicoEmServicoRequest(
      this.nome,
      this.valor,
      this.duracao,
    );

    this.servicoService.criarNovoServico(servico).subscribe({
      next: (res) => {
        console.log('Serviço criado', res);
        
        // 3. Redireciona o usuário para a rota '/servicos'
        this.router.navigate(['/servicos']);
      },
      error: (err) => {
        console.error('Erro ao criar serviço:', err);
        const mensagemErro = err.error || 'Ocorreu um erro ao tentar salvar o serviço.';
        alert('Atenção: ' + mensagemErro);
      },
    });
  }
}