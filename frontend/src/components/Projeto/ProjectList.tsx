import React, { useState, useEffect } from 'react';
import type  {StatusProjeto} from 'src/types/projeto/statusprojeto';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import { projetoService } from '../../services/projetoService';
import { ProjectFilters } from './ProjectFilters';
import {ProjectTable} from './ProjectTable';
//import { ProjectFilters } from './ProjectFilters';

export const ProjectList: React.FC = () => {
  const [projetos, setProjetos] = useState<ProjetoResponse[]>([]);
  const [filteredProjetos, setFilteredProjetos] = useState<ProjetoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [filters, setFilters] = useState({
    status: '' as StatusProjeto | '',
    search: '',
  });

  const carregarProjetos = async () => {
    try {
      setLoading(true);
      const data = await projetoService.listarTodos();
      setProjetos(data);
      setFilteredProjetos(data);
      setError(null);
    } catch (err) {
      setError('Erro ao carregar projetos');
      console.error('Erro:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarProjetos();
  }, []);

  // Aplicar filtros
  useEffect(() => {
    let resultado = projetos;

    if (filters.status) {
      resultado = resultado.filter(p => p.status === filters.status);
    }

    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      resultado = resultado.filter(p => 
        p.nome.toLowerCase().includes(searchLower) ||
        p.responsaveis.some(r => r.nome.toLowerCase().includes(searchLower))
      );
    }

    setFilteredProjetos(resultado);
  }, [filters, projetos]);

  const handleFilterChange = (newFilters: typeof filters) => {
    setFilters(newFilters);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Tem certeza que deseja excluir este projeto?')) {
      try {
        await projetoService.excluir(id);
        await carregarProjetos(); // Recarregar lista
      } catch (error) {
        alert('Erro ao excluir projeto: ' + (error as Error).message);
      }
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg">Carregando projetos...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-red-600 text-lg">{error}</div>
        <button 
          onClick={carregarProjetos}
          className="ml-4 bg-blue-500 text-white px-4 py-2 rounded"
        >
          Tentar Novamente
        </button>
      </div>
    );
  }

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Gestão de Projetos</h1>
        <p className="text-gray-600">Gerencie todos os projetos do sistema</p>
      </div>

      <ProjectFilters 
        filters={filters}
        onFilterChange={handleFilterChange}
        projectCount={filteredProjetos.length}
      />

      <ProjectTable 
        projetos={filteredProjetos}
        onDelete={handleDelete}
        onRefresh={carregarProjetos}
      />
    </div>
  );
};