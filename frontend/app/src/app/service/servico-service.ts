import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ServicoRequestDTO } from '../models/ServicoRequestDTO.dto';
import { ServicoResponseDTO } from '../models/ServicoResponseDTO.dto';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ServicoService {
  private apiUrl = 'http://localhost:8080/servicos';
  constructor(private http: HttpClient) {}

  transformarServicoEmServicoRequest(
    nome: string,
    valor: number,
    duracaoMinutos: number,
  ): ServicoRequestDTO {
    return {
      nome: nome,
      valor: valor,
      duracaoMinutos: duracaoMinutos,
    };
  }
  isExistServicoPorNome(nome: string) {
    const params = new HttpParams().set('nome', nome);
    return this.http.get<boolean>(`${this.apiUrl}/verificar`, { params });
  }
  criarNovoServico(dto: ServicoRequestDTO) {
    return this.http.post<ServicoResponseDTO>(`${this.apiUrl}/criar`, dto);
  }
  obterServicosPaginados(page: number, size: number, termo: string = ''): Observable<any> {
    return this.http.get<any>(
      `${this.apiUrl}/obterservicos?page=${page}&size=${size}&busca=${termo}`,
    );
  }
  atualizarServico(id: number, servico: ServicoRequestDTO): Observable<ServicoResponseDTO> {
    return this.http.put<ServicoResponseDTO>(`${this.apiUrl}/${id}`, servico);
  }

  excluirServico(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  buscarPorNome(nome: string): Observable<ServicoResponseDTO[]> {
    return this.http.get<ServicoResponseDTO[]>(`${this.apiUrl}/buscar/${nome}`);
  }
}
