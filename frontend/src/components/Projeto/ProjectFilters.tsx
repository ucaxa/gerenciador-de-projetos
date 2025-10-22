// components/Projeto/ProjectFilters.tsx
import React from 'react';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';

interface ProjectFiltersProps {
  filters: {
    status: StatusProjeto | '';
    search: string;
  };
  onFilterChange: (filters: { status: StatusProjeto | ''; search: string }) => void;
  projectCount: number;
  totalCount?: number; // Adicionar esta propriedade
}

export const ProjectFilters: React.FC<ProjectFiltersProps> = ({
  filters,
  onFilterChange,
  projectCount,
  totalCount
}) => {
  const handleStatusChange = (status: StatusProjeto | '') => {
    onFilterChange({ ...filters, status });
  };

  const handleSearchChange = (search: string) => {
    onFilterChange({ ...filters, search });
  };

  const clearFilters = () => {
    onFilterChange({ status: '', search: '' });
  };

  const hasActiveFilters = filters.status !== '' || filters.search !== '';

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 mb-6">
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
        {/* Título e Contadores */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
          <h2 className="text-lg font-semibold text-gray-800">Filtros</h2>
          
          <div className="flex flex-wrap gap-4 text-sm">
            <div className="text-gray-600">
              <span className="font-medium">{projectCount}</span> projeto(s) encontrado(s)
            </div>
            {totalCount && totalCount !== projectCount && (
              <div className="text-gray-500">
                <span className="font-medium">{totalCount}</span> no total
              </div>
            )}
          </div>
        </div>

        {/* Botão Limpar Filtros */}
        {hasActiveFilters && (
          <button
            onClick={clearFilters}
            className="text-blue-600 hover:text-blue-800 text-sm font-medium flex items-center"
          >
            <span className="mr-1">✕</span>
            Limpar Filtros
          </button>
        )}
      </div>

      {/* Filtros */}
      <div className="mt-4 grid grid-cols-1 md:grid-cols-2 gap-4">
        {/* Filtro de Status */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Status do Projeto
          </label>
          <select
            value={filters.status}
            onChange={(e) => handleStatusChange(e.target.value as StatusProjeto | '')}
            className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">Todos os status</option>
            <option value="A_INICIAR">A Iniciar</option>
            <option value="EM_ANDAMENTO">Em Andamento</option>
            <option value="ATRASADO">Atrasado</option>
            <option value="CONCLUIDO">Concluído</option>
          </select>
        </div>

        {/* Filtro de Busca */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Buscar
          </label>
          <div className="relative">
            <input
              type="text"
              value={filters.search}
              onChange={(e) => handleSearchChange(e.target.value)}
              placeholder="Buscar por nome ou responsável..."
              className="w-full border border-gray-300 rounded-md pl-10 pr-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <span className="text-gray-400">🔍</span>
            </div>
          </div>
        </div>
      </div>

      {/* Indicadores de Filtros Ativos */}
      {hasActiveFilters && (
        <div className="mt-4 flex flex-wrap gap-2">
          {filters.status && (
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              Status: {filters.status.replace('_', ' ')}
              <button
                onClick={() => handleStatusChange('')}
                className="ml-1 text-blue-600 hover:text-blue-800"
              >
                ×
              </button>
            </span>
          )}
          {filters.search && (
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
              Busca: "{filters.search}"
              <button
                onClick={() => handleSearchChange('')}
                className="ml-1 text-green-600 hover:text-green-800"
              >
                ×
              </button>
            </span>
          )}
        </div>
      )}
    </div>
  );
};