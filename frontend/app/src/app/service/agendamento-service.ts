import { Injectable } from '@angular/core';
import { AgendamentoRequestDTO } from '../models/AgendamentoRequestDTO.dto';
import { HttpClient } from '@angular/common/http';
import { AgendamentoResponseDTO } from '../models/AgendamentoResponseDTO.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AgendamentoService {
  private apiUrl = 'http://localhost:8080/agendamento';

  constructor(private http: HttpClient) {}

  criarNovoAgendamento(dto: AgendamentoRequestDTO) {
    return this.http.post<AgendamentoRequestDTO>(`${this.apiUrl}/criar`, dto);
  }

  obterAgendamentosPaginados(page: number, size: number, termo: string = ''): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/obteragendamentos?page=${page}&size=${size}&busca=${termo}`,
    );
  }

 filtrarConcluidos(page: number, size: number, termo: string = ''): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/obteragendamentosconcluidos?page=${page}&size=${size}&busca=${termo}`,
    );
  }

  filtrarPendentes(page: number, size: number, termo: string = ''): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/obteragendamentospendentes?page=${page}&size=${size}&busca=${termo}`,
    );
  }

  concluirAgendamento(id: number): Observable<AgendamentoResponseDTO> {
    return this.http.post<AgendamentoResponseDTO>(`${this.apiUrl}/concluir/${id}`, null);
  }

  excluirAgendamento(id : number) : Observable<void>{
    return this.http.delete<void>(`${this.apiUrl}/deletar/${id}`)
  }
}
