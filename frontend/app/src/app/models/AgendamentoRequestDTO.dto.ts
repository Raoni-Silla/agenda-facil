export interface AgendamentoRequestDTO {
  idCliente: number;
  data: string;
  horaInicio: string;
  horaFim: string;
  servicos: number[];
}