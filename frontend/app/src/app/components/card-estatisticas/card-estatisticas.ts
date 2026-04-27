import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { ThemeService } from '../../service/theme-service';

@Component({
  selector: 'app-card-estatisticas',
  imports: [CommonModule],
  templateUrl: './card-estatisticas.html',
  styleUrl: './card-estatisticas.css',
})
export class CardEstatisticas implements OnInit {
  @Input() icone: string = 'fa-calendar';
  @Input() titulo: string = 'insira um titulo';
  corCard = '';

  constructor(private themeService: ThemeService) {}

  ngOnInit() {
    // Usamos o método do serviço
    this.corCard = this.themeService.getCorAleatoria();
  }
}
