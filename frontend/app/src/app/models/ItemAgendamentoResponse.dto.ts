import { ServicoResponseDTO } from './ServicoResponseDTO.dto';

export interface ItemAgendamentoResponseDTO {
  id: number;
  servico: ServicoResponseDTO; 
  valorCobrado: number;        
}