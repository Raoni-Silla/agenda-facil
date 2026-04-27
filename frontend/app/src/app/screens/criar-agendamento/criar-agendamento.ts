import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { debounceTime, distinct, distinctUntilChanged, Subject } from 'rxjs';
import { ClienteService } from '../../service/cliente-service';
import { FormsModule } from '@angular/forms';
import { ClienteResponseDTO } from '../../models/ClienteResponseDTO.dto';

@Component({
  selector: 'app-criar-agendamento',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './criar-agendamento.html',
  styleUrl: './criar-agendamento.css',
})
export class CriarAgendamento {
  

 
}
