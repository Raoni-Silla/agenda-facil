import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ClienteResponseDTO } from '../models/ClienteResponseDTO.dto';
import { ClienteRequestDTO } from '../models/ClienteRequestDTO.dto';

@Injectable({
  providedIn: 'root',
})
export class ClienteService {
  private apiUrl = 'http://localhost:8080/clientes';

  constructor(private http: HttpClient) {}

  transformarClienteEmClienteRequest(nome: string, telefone: string): ClienteRequestDTO {
    return {
      nome,
      telefone,
    };
  }

  buscarPorTelefone(telefone: string): Observable<boolean> {
    const params = new HttpParams().set('telefone', telefone);
    return this.http.get<boolean>(`${this.apiUrl}/verificar`, { params });
  }

  obterClientesPaginados(page: number, size: number, termo: string = ''): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/obterclientes?page=${page}&size=${size}&busca=${termo}`);
  }

  criarCliente(cliente: ClienteRequestDTO): Observable<ClienteResponseDTO> {
    return this.http.post<ClienteResponseDTO>('http://localhost:8080/clientes/criar', cliente);
  }

  excluirCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  atualizarCliente(id: number, cliente: ClienteRequestDTO): Observable<ClienteResponseDTO> {
    return this.http.put<ClienteResponseDTO>(`${this.apiUrl}/${id}`, cliente);
  }
}
