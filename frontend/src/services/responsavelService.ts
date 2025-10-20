
import { api } from './api';
import type {ResponsavelResponse } from '../types/responsavel/responsavelResponse';
import type  {ResponsavelRequest} from '../types/responsavel/responsavelRequest';

export const responsavelService = {
  listarTodos: (): Promise<ResponsavelResponse[]> => 
    api.get('/responsaveis').then(response => response.data),

  buscarPorId: (id: number): Promise<ResponsavelResponse> => 
    api.get(`/responsaveis/${id}`).then(response => response.data),

  criar: (responsavel: ResponsavelRequest): Promise<ResponsavelResponse> => 
    api.post('/responsaveis', responsavel).then(response => response.data),

  atualizar: (id: number, responsavel: ResponsavelRequest): Promise<ResponsavelResponse> => 
    api.put(`/responsaveis/${id}`, responsavel).then(response => response.data),

  excluir: (id: number): Promise<void> => 
    api.delete(`/responsaveis/${id}`),

  listarPaginado: (page: number = 0, size: number = 10) => 
    api.get(`/responsaveis/paginado?page=${page}&size=${size}`).then(response => response.data),
};