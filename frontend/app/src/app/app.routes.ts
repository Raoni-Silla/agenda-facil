import { Routes } from '@angular/router';
import { Inicio } from './screens/inicio/inicio';
import { CriarAgendamento } from './screens/criar-agendamento/criar-agendamento';
import { Clientes } from './screens/clientes/clientes';
import { CriarCliente } from './screens/criar-cliente/criar-cliente';
import { Servicos } from './screens/servicos/servicos';
import { CriarServico } from './screens/criar-servico/criar-servico';

export const routes: Routes = [
    {path:'inicio', component:Inicio},
    {path:'clientes', component:Clientes},
    {path: 'criarAgendamento', component:CriarAgendamento},
    {path: 'criarCliente', component:CriarCliente},
    {path: 'servicos', component:Servicos},
    {path: 'criarServico', component: CriarServico},
    {path: '', redirectTo: 'inicio', pathMatch:'full'}
];
