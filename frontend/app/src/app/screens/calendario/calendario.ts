import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { CalendarModule } from 'angular-calendar';


@Component({
  selector: 'app-calendario',
  imports: [CalendarModule, CommonModule],
  templateUrl: './calendario.html',
  styleUrl: './calendario.css',
})
export class Calendario {
  dataAtual: Date = new Date();

  mudarMes(direcao: number) {
    const novaData = new Date(this.dataAtual);
    novaData.setMonth(novaData.getMonth() + direcao);
    this.dataAtual = novaData;
  }
}
