
import type { ProjetoResponse } from "../projeto/projetoResponse";
import type { StatusProjeto } from "../projeto/statusprojeto";


export interface KanbanColumn {
  id: StatusProjeto;
  title: string;
  color: string;
  projetos: ProjetoResponse[];
}