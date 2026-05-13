import { DatePipe, TitleCasePipe, UpperCasePipe } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { ThemeService } from '../../service/theme-service';

@Component({
  selector: 'app-card-agendamento',
  imports: [TitleCasePipe, UpperCasePipe, DatePipe],
  templateUrl: './card-agendamento.html',
  styleUrl: './card-agendamento.css',
})
export class CardAgendamento implements OnInit {
  @Input() nomeCliente: string = 'admin';
  @Input() servico: string = 'cabelo + barba';
  @Input() horas: string = '14:00';
  @Input() abreviacao: string = 'ad';
  corCard = '';
  
  constructor(private themeService: ThemeService) {}

  ngOnInit() {
    // Usamos o método do serviço
    this.corCard = this.themeService.getCorAleatoria();
  }
}
