import React from 'react';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';

interface ProjectFiltersProps {
  filters: {
    status: StatusProjeto | '';
    search: string;
  };
  onFilterChange: (filters: any) => void;
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

  return (
    <div className="bg-white p-4 rounded-lg shadow mb-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
        <div className="flex flex-col sm:flex-row gap-4 flex-1">
          {/* Busca */}
          <div className="flex-1">
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-1">
              Buscar
            </label>
            <input
              type="text"
              id="search"
              value={filters.search}
              onChange={(e) => handleSearchChange(e.target.value)}
              placeholder="Buscar por nome ou responsável..."
              className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          {/* Filtro por Status */}
          <div>
            <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-1">
              Status
            </label>
            <select
              id="status"
              value={filters.status}
              onChange={(e) => handleStatusChange(e.target.value as StatusProjeto | '')}
              className="w-full md:w-48 px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Todos os status</option>
              <option value="A_INICIAR">A Iniciar</option>
              <option value="EM_ANDAMENTO">Em Andamento</option>
              <option value="ATRASADO">Atrasado</option>
              <option value="CONCLUIDO">Concluído</option>
            </select>
          </div>
        </div>

        {/* Contador e Limpar Filtros */}
        <div className="flex items-center gap-4">
          <div className="text-sm text-gray-600">
            {projectCount} projeto{projectCount !== 1 ? 's' : ''} encontrado{projectCount !== 1 ? 's' : ''}
          </div>
          
          {hasActiveFilters && (
            <button
              onClick={clearFilters}
              className="px-3 py-2 text-sm text-gray-600 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
            >
              Limpar Filtros
            </button>
          )}
        </div>
      </div>
    </div>
  );
};