// components/Responsavel/ResponsavelList.tsx
import React, { useState, useEffect } from 'react';
import type { ResponsavelResponse } from 'src/types/responsavel/responsavelResponse';
import { responsavelService } from '../../services/responsavelService';
import { ResponsavelTable } from './ResponsavelTable';
import { ResponsavelForm } from './ResponsavelForm';
import { ResponsavelFilters } from './ResponsavelFilters';
import { Pagination } from '../Pagination/Pagination';

interface Filters {
  cargo: string;
  search: string;
}

export const ResponsavelList: React.FC = () => {
  const [responsaveis, setResponsaveis] = useState<ResponsavelResponse[]>([]);
  const [allResponsaveis, setAllResponsaveis] = useState<ResponsavelResponse[]>([]); // Para filtros
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingResponsavel, setEditingResponsavel] = useState<ResponsavelResponse | null>(null);

  // Estados de paginação
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalElements, setTotalElements] = useState(0);

  // Estados de filtros
  const [filters, setFilters] = useState<Filters>({
    cargo: '',
    search: ''
  });

  // Carregar todos os responsáveis para filtros (sem paginação)
  const carregarTodosResponsaveis = async () => {
    try {
      const response = await responsavelService.listarTodos();
      setAllResponsaveis(response);
    } catch (err) {
      console.error('Erro ao carregar responsáveis para filtros:', err);
    }
  };

  // Carregar responsáveis paginados
  const carregarResponsaveis = async (pageNum: number = page) => {
    try {
      setLoading(true);
      const response = await responsavelService.listarPaginado(pageNum, pageSize);
      
      setResponsaveis(response.content);
      setTotalElements(response.totalElements);
      setError(null);
    } catch (err) {
      setError('Erro ao carregar responsáveis');
      console.error('Erro ao carregar responsáveis:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarResponsaveis();
    carregarTodosResponsaveis(); // Carrega todos para os filtros
  }, [page, pageSize]);

  // Aplicar filtros localmente na lista completa
  const aplicarFiltros = () => {
    let filtered = [...allResponsaveis];

    // Filtro por cargo
    if (filters.cargo) {
      filtered = filtered.filter(responsavel => 
        responsavel.cargo?.toLowerCase() === filters.cargo.toLowerCase()
      );
    }

    // Filtro de busca (nome, email, cargo)
    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      filtered = filtered.filter(responsavel =>
        responsavel.nome?.toLowerCase().includes(searchLower) ||
        responsavel.email?.toLowerCase().includes(searchLower) ||
        responsavel.cargo?.toLowerCase().includes(searchLower)
      );
    }

    return filtered;
  };

  // Responsáveis filtrados
  const responsaveisFiltrados = aplicarFiltros();
  const hasActiveFilters = filters.cargo !== '' || filters.search !== '';

  // Obter lista de cargos únicos para o dropdown
  const cargosUnicos = [...new Set(allResponsaveis
    .map(r => r.cargo)
    .filter(Boolean) // Remove valores nulos/vazios
    .sort() // Ordena alfabeticamente
  )] as string[];

  const handleFilterChange = (newFilters: Filters) => {
    setFilters(newFilters);
    setPage(0); // Reset para primeira página ao filtrar
  };

  const handleCreate = () => {
    setEditingResponsavel(null);
    setIsFormOpen(true);
  };

  const handleEdit = (responsavel: ResponsavelResponse) => {
    setEditingResponsavel(responsavel);
    setIsFormOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (window.confirm('Tem certeza que deseja excluir este responsável?')) {
      try {
        await responsavelService.excluir(id);
        // Recarrega ambas as listas
        await carregarResponsaveis();
        await carregarTodosResponsaveis();
      } catch (error) {
        alert('Erro ao excluir responsável: ' + (error as Error).message);
      }
    }
  };

  const handleFormSuccess = () => {
    setIsFormOpen(false);
    setEditingResponsavel(null);
    // Recarrega ambas as listas
    carregarResponsaveis();
    carregarTodosResponsaveis();
  };

  const handleFormClose = () => {
    setIsFormOpen(false);
    setEditingResponsavel(null);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  const handlePageSizeChange = (newSize: number) => {
    setPageSize(newSize);
    setPage(0); // Reset para primeira página ao mudar tamanho
  };

  const handleRefresh = () => {
    carregarResponsaveis();
    carregarTodosResponsaveis();
  };

  if (loading && responsaveis.length === 0) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="flex flex-col items-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mb-4"></div>
          <div className="text-lg text-gray-600">Carregando responsáveis...</div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-center">
          <div className="text-red-600 text-lg mb-4">{error}</div>
          <button 
            onClick={handleRefresh}
            className="bg-blue-500 text-white px-6 py-2 rounded-lg hover:bg-blue-600 transition-colors font-medium"
          >
            Tentar Novamente
          </button>
        </div>
      </div>
    );
  }

  const totalPages = Math.ceil(totalElements / pageSize);

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Gestão de Responsáveis</h1>
          <p className="text-gray-600">
            {hasActiveFilters 
              ? `${responsaveisFiltrados.length} responsável(eis) encontrado(s)` 
              : `${totalElements} responsável(eis) cadastrado(s)`
            }
          </p>
        </div>
        
        <button
          onClick={handleCreate}
          className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors flex items-center font-medium shadow-sm"
        >
          <span className="mr-2 text-lg">+</span>
          Novo Responsável
        </button>
      </div>

      {/* Componente de Filtros */}
      <ResponsavelFilters
        filters={filters}
        onFilterChange={handleFilterChange}
        responsavelCount={hasActiveFilters ? responsaveisFiltrados.length : totalElements}
        totalCount={hasActiveFilters ? totalElements : undefined}
        availableCargos={cargosUnicos}
      />

      <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        {/* Tabela com responsáveis paginados ou filtrados */}
        <ResponsavelTable 
          responsaveis={hasActiveFilters ? responsaveisFiltrados : responsaveis}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
        
        {/* Paginação - só mostra se não estiver filtrando e tiver elementos */}
        {!hasActiveFilters && totalElements > 0 && (
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={pageSize}
            onPageChange={handlePageChange}
            onPageSizeChange={handlePageSizeChange}
            itemsName="responsáveis"
          />
        )}

        {/* Mensagem quando filtros não retornam resultados */}
        {hasActiveFilters && responsaveisFiltrados.length === 0 && (
          <div className="text-center py-8 text-gray-500">
            Nenhum responsável encontrado com os filtros aplicados.
          </div>
        )}
      </div>

      {/* Modal do Formulário */}
      {isFormOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <h2 className="text-xl font-bold text-gray-800 mb-4">
                {editingResponsavel ? 'Editar Responsável' : 'Novo Responsável'}
              </h2>
              <ResponsavelForm 
                responsavel={editingResponsavel || undefined}
                onClose={handleFormClose}
                onSuccess={handleFormSuccess}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};