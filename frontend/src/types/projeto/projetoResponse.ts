
import type { StatusProjeto } from "..";
import type { ResponsavelResponse } from "../responsavel/reponsavelResponse";



export interface ProjetoResponse {
  id: number;
  nome: string;
  status: StatusProjeto;
  responsaveis: ResponsavelResponse[];
  inicioPrevisto?: string;
  terminoPrevisto?: string;
  inicioRealizado?: string;
  terminoRealizado?: string;
  diasAtraso: number;
  percentualTempoRestante: number;
  createdAt?: string;
  updatedAt?: string;
}