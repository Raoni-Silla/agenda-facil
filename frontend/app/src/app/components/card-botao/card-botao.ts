import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-card-botao',
  imports: [CommonModule, RouterLink],
  templateUrl: './card-botao.html',
  styleUrl: './card-botao.css',
})
export class CardBotao {
  @Input() tema: 'gradiente' | 'claro' = 'claro'; 
  @Input() icone: string = '';                    
  @Input() titulo: string = '';
  @Input() subtitulo: string = '';
  @Input() rota?: string = '';
}
