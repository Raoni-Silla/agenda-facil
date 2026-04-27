export interface ClienteResponseDTO {
  id?: number;
  nome: string;
  telefone: string;
  totalVisitas: number;
  ultimaVisita: string | Date; 
}