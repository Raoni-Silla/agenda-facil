import { Component } from '@angular/core';
import { CardEstatisticas } from '../../components/card-estatisticas/card-estatisticas';
import { CardAgendamento } from '../../components/card-agendamento/card-agendamento';
import { CardBotao } from '../../components/card-botao/card-botao';

@Component({
  selector: 'app-inicio',
  imports: [CardEstatisticas, CardAgendamento, CardBotao],
  templateUrl: './inicio.html',
  styleUrl: './inicio.css',
})
export class Inicio {}
