import React from 'react';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';

interface ProjectFiltersProps {
  filters: {
    status: StatusProjeto | '';
    search: string;
  };
  onFilterChange: (filters: { status: StatusProjeto | ''; search: string }) => void;
  projectCount: number;
}

export const ProjectFilters: React.FC<ProjectFiltersProps> = ({
  filters,
  onFilterChange,
  projectCount
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

  const getStatusDisplayText = (status: StatusProjeto) => {
    return status.replace('_', ' ');
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 mb-6">
      <div className="flex flex-col lg:flex-row lg:items-end lg:justify-between gap-6">
        <div className="flex flex-col sm:flex-row gap-4 flex-1">
          {/* Busca */}
          <div className="flex-1 min-w-[250px]">
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
              🔍 Buscar Projetos
            </label>
            <div className="relative">
              <input
                type="text"
                id="search"
                value={filters.search}
                onChange={(e) => handleSearchChange(e.target.value)}
                placeholder="Buscar por nome do projeto ou responsável..."
                className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              />
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <span className="text-gray-400">🔍</span>
              </div>
            </div>
          </div>

          {/* Filtro por Status */}
          <div className="min-w-[200px]">
            <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-2">
              📊 Filtrar por Status
            </label>
            <select
              id="status"
              value={filters.status}
              onChange={(e) => handleStatusChange(e.target.value as StatusProjeto | '')}
              className="w-full px-4 py-2.5 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors appearance-none bg-white"
            >
              <option value="">📁 Todos os status</option>
              <option value="A_INICIAR">🟦 A Iniciar</option>
              <option value="EM_ANDAMENTO">🟨 Em Andamento</option>
              <option value="ATRASADO">🟥 Atrasado</option>
              <option value="CONCLUIDO">🟩 Concluído</option>
            </select>
          </div>
        </div>

        {/* Contador e Limpar Filtros */}
        <div className="flex flex-col sm:flex-row items-start sm:items-center gap-4">
          <div className="text-sm text-gray-600 bg-gray-50 px-3 py-2 rounded-lg border">
            <span className="font-medium text-gray-800">{projectCount}</span> 
            projeto{projectCount !== 1 ? 's' : ''} encontrado{projectCount !== 1 ? 's' : ''}
          </div>
          
          {hasActiveFilters && (
            <button
              onClick={clearFilters}
              className="px-4 py-2 text-sm text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-colors flex items-center gap-2"
            >
              <span>🗑️</span>
              Limpar Filtros
            </button>
          )}
        </div>
      </div>

      {/* Indicadores de Filtros Ativos */}
      {hasActiveFilters && (
        <div className="mt-4 pt-4 border-t border-gray-200">
          <div className="flex flex-wrap items-center gap-2 text-sm">
            <span className="text-gray-600">Filtros ativos:</span>
            
            {filters.search && (
              <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                Busca: "{filters.search}"
                <button
                  onClick={() => handleSearchChange('')}
                  className="ml-1.5 text-blue-600 hover:text-blue-800"
                >
                  ×
                </button>
              </span>
            )}
            
            {filters.status && (
              <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-purple-100 text-purple-800">
                Status: {getStatusDisplayText(filters.status)}
                <button
                  onClick={() => handleStatusChange('')}
                  className="ml-1.5 text-purple-600 hover:text-purple-800"
                >
                  ×
                </button>
              </span>
            )}
          </div>
        </div>
      )}
    </div>
  );
};