export interface ProjetoRequest {
  nome: string;
  responsavelIds?: number[];
  inicioPrevisto?: string;
  terminoPrevisto?: string;
  inicioRealizado?: string;
  terminoRealizado?: string;
}