import { api } from './api';
import type {ProjetoRequest } from '../types/projeto/projetoRequest';
import type {ProjetoResponse} from '../types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';

export const projetoService = {
  // CRUD Básico
  listarTodos: (): Promise<ProjetoResponse[]> => 
    api.get('/projetos').then(response => response.data),

  buscarPorId: (id: number): Promise<ProjetoResponse> => 
    api.get(`/projetos/${id}`).then(response => response.data),

  criar: (projeto: ProjetoRequest): Promise<ProjetoResponse> => 
    api.post('/projetos', projeto).then(response => response.data),

  atualizar: (id: number, projeto: ProjetoRequest): Promise<ProjetoResponse> => 
    api.put(`/projetos/${id}`, projeto).then(response => response.data),

  excluir: (id: number): Promise<void> => 
    api.delete(`/projetos/${id}`),

  // Kanban e Status
  listarPaginado: (page: number = 0, size: number = 10): Promise<{ content: ProjetoResponse[] }> => 
    api.get(`/projetos/paginado?page=${page}&size=${size}`).then(response => response.data),

  transicionarStatus: (id: number, novoStatus: StatusProjeto): Promise<ProjetoResponse> => 
    api.patch(`/projetos/${id}/status/${novoStatus}`).then(response => response.data),
};

