import { Injectable } from '@angular/core';
import { AgendamentoRequestDTO } from '../models/AgendamentoRequestDTO.dto';
import { HttpClient } from '@angular/common/http';
import { AgendamentoResponseDTO } from '../models/AgendamentoResponseDTO.dto';
import { catchError, Observable, of, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AgendamentoService {
  private apiUrl = 'http://localhost:8080/agendamento';

  private mudancaDadosSource = new Subject<void>();

  mudancaDados$ = this.mudancaDadosSource.asObservable();

  notificarMudanca() {
    this.mudancaDadosSource.next();
  }

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

  excluirAgendamento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deletar/${id}`);
  }

  getAgendamentosParaHoje(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/obter-agendamentos-hoje`).pipe(catchError(() => of(0)));;
  }

  getAgendamentosParaSemana(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/obter-agendamentos-semana`).pipe(catchError(() => of(0)));;
  }

  getTotalClientes(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/obter-total-clientes`).pipe(catchError(() => of(0)));;
  }

  getTotalGanho(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-ganho`).pipe(catchError(() => of(0)));;
  }

  getTotalFaturamento(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/total-faturamento`).pipe(catchError(() => of(0)));;
  }

  getProximosAgendamentos(): Observable<any>{
    return this.http.get<any>(`${this.apiUrl}/obter-proximos-agendamentos`)
  }

  getResumoMensal(inicio: string, fim: string): Observable<Record<string, number>> {
  return this.http.get<Record<string, number>>(`${this.apiUrl}/obter-resumo-mensal`, {
    params: { inicio, fim }
  });
}

}
