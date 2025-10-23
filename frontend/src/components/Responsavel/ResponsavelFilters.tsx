// components/Responsavel/ResponsavelFilters.tsx
import React from 'react';

interface ResponsavelFiltersProps {
  filters: {
    cargo: string;
    search: string;
  };
  onFilterChange: (filters: { cargo: string; search: string }) => void;
  responsavelCount: number;
  totalCount?: number;
  availableCargos?: string[]; // Lista de cargos únicos para o dropdown
}

export const ResponsavelFilters: React.FC<ResponsavelFiltersProps> = ({
  filters,
  onFilterChange,
  responsavelCount,
  totalCount,
  availableCargos = [] // Valor padrão vazio
}) => {
  const handleCargoChange = (cargo: string) => {
    onFilterChange({ ...filters, cargo });
  };

  const handleSearchChange = (search: string) => {
    onFilterChange({ ...filters, search });
  };

  const clearFilters = () => {
    onFilterChange({ cargo: '', search: '' });
  };

  const hasActiveFilters = filters.cargo !== '' || filters.search !== '';

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200 mb-6">
      <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
        {/* Título e Contadores */}
        <div className="flex flex-col sm:flex-row sm:items-center gap-4">
          <h2 className="text-lg font-semibold text-gray-800">Filtros</h2>
          
          <div className="flex flex-wrap gap-4 text-sm">
            <div className="text-gray-600">
              <span className="font-medium">{responsavelCount}</span> responsável(eis) encontrado(s)
            </div>
            {totalCount && totalCount !== responsavelCount && (
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
        {/* Filtro de Cargo */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Cargo
          </label>
          <select
            value={filters.cargo}
            onChange={(e) => handleCargoChange(e.target.value)}
            className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="">Todos os cargos</option>
            {availableCargos.map((cargo) => (
              <option key={cargo} value={cargo}>
                {cargo}
              </option>
            ))}
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
              placeholder="Buscar por nome, email ou cargo..."
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
          {filters.cargo && (
            <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              Cargo: {filters.cargo}
              <button
                onClick={() => handleCargoChange('')}
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