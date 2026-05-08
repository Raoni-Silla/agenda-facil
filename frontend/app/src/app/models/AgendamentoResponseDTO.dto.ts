import { ClienteResponseDTO } from './ClienteResponseDTO.dto';
import { ItemAgendamentoResponseDTO } from './ItemAgendamentoResponse.dto';


export interface AgendamentoResponseDTO {
  idAgendamento: number;
  cliente: ClienteResponseDTO;       // Aqui está o "cliente.nome" que o HTML queria!
  itens: ItemAgendamentoResponseDTO[]; // Onde ficam os serviços
  data: string;                       // LocalDate vira string no JSON
  horaInicio: string;                 // LocalTime vira string no JSON
  horaFim: string;
  valorTotal: number;                 // BigDecimal vira number
  status: string;                     // O Enum vira string
}