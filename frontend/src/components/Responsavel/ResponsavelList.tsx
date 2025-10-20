import React, { useState, useEffect } from 'react';
import type { ResponsavelResponse } from 'src/types';
import { responsavelService } from '../../services/responsavelService';
import { ResponsavelTable } from './ResponsavelTable';
import { ResponsavelForm } from './ResponsavelForm';

export const ResponsavelList: React.FC = () => {
  const [responsaveis, setResponsaveis] = useState<ResponsavelResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingResponsavel, setEditingResponsavel] = useState<ResponsavelResponse | null>(null);

  const carregarResponsaveis = async () => {
    try {
      setLoading(true);
      const data = await responsavelService.listarTodos();
      setResponsaveis(data);
      setError(null);
    } catch (err) {
      setError('Erro ao carregar responsáveis');
      console.error('Erro:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarResponsaveis();
  }, []);

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
        await carregarResponsaveis();
      } catch (error) {
        alert('Erro ao excluir responsável: ' + (error as Error).message);
      }
    }
  };

  const handleFormSuccess = () => {
    setIsFormOpen(false);
    setEditingResponsavel(null);
    carregarResponsaveis();
  };

  const handleFormClose = () => {
    setIsFormOpen(false);
    setEditingResponsavel(null);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg">Carregando responsáveis...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-red-600 text-lg">{error}</div>
        <button 
          onClick={carregarResponsaveis}
          className="ml-4 bg-blue-500 text-white px-4 py-2 rounded"
        >
          Tentar Novamente
        </button>
      </div>
    );
  }

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-gray-800">Gestão de Responsáveis</h1>
          <p className="text-gray-600">Gerencie os responsáveis dos projetos</p>
        </div>
        
        <button
          onClick={handleCreate}
          className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center"
        >
          <span className="mr-2">+</span>
          Novo Responsável
        </button>
      </div>

      <ResponsavelTable 
        responsaveis={responsaveis}
        onEdit={handleEdit}
        onDelete={handleDelete}
      />

      {/* Modal do Formulário */}
      {isFormOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-md w-full max-h-[90vh] overflow-y-auto">
            <ResponsavelForm 
              responsavel={editingResponsavel || undefined}
              onClose={handleFormClose}
              onSuccess={handleFormSuccess}
            />
          </div>
        </div>
      )}
    </div>
  );
};