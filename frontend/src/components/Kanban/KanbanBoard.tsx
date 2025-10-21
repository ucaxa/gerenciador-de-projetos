// components/Kanban/KanbanBoard.tsx
import React, { useState, useEffect } from 'react';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';
import { projetoService } from '../../services/projetoService';
import { KanbanColumn } from './KanbanColumn';
import { ProjectForm } from '../Projeto/ProjectForm'; // NOVO: import do formulário

export const KanbanBoard: React.FC = () => {
  const [projetos, setProjetos] = useState<ProjetoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingProjeto, setEditingProjeto] = useState<ProjetoResponse | null>(null); // NOVO: estado para edição
  const [showForm, setShowForm] = useState(false); // NOVO: controle do modal

  // Colunas do Kanban
  const columns = [
    {
      id: 'A_INICIAR' as StatusProjeto,
      title: 'A Iniciar',
      color: 'bg-blue-100 border-blue-300',
      projetos: projetos.filter(p => p.status === 'A_INICIAR')
    },
    {
      id: 'EM_ANDAMENTO' as StatusProjeto,
      title: 'Em Andamento', 
      color: 'bg-yellow-100 border-yellow-300',
      projetos: projetos.filter(p => p.status === 'EM_ANDAMENTO')
    },
    {
      id: 'ATRASADO' as StatusProjeto,
      title: 'Atrasado',
      color: 'bg-red-100 border-red-300',
      projetos: projetos.filter(p => p.status === 'ATRASADO')
    },
    {
      id: 'CONCLUIDO' as StatusProjeto,
      title: 'Concluído',
      color: 'bg-green-100 border-green-300',
      projetos: projetos.filter(p => p.status === 'CONCLUIDO')
    }
  ];

  // Carregar projetos
  const carregarProjetos = async () => {
    try {
      setLoading(true);
      const data = await projetoService.listarTodos();
      setProjetos(data);
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

  // Atualizar projeto após transição
  const handleStatusChange = (projetoId: number, novoStatus: StatusProjeto) => {
    setProjetos(prev => 
      prev.map(p => 
        p.id === projetoId ? { ...p, status: novoStatus } : p
      )
    );
  };

  // NOVO: Handler para edição de projeto
  const handleEditProjeto = (projeto: ProjetoResponse) => {
    setEditingProjeto(projeto);
    setShowForm(true);
  };

  // NOVO: Handler para fechar o formulário
  const handleCloseForm = () => {
    setShowForm(false);
    setEditingProjeto(null);
  };

  // NOVO: Handler para sucesso na edição/criação
  const handleFormSuccess = () => {
    carregarProjetos(); // Recarrega os projetos
    handleCloseForm();
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
        <h1 className="text-3xl font-bold text-gray-800">Quadro Kanban</h1>
        <p className="text-gray-600">Gerencie seus projetos de forma visual</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {columns.map(column => (
          <KanbanColumn
            key={column.id}
            column={column}
            onStatusChange={handleStatusChange}
            onEdit={handleEditProjeto} // NOVO: passando callback de edição
          />
        ))}
      </div>

      {/* Estatísticas */}
      <div className="mt-8 grid grid-cols-2 md:grid-cols-4 gap-4">
        {columns.map(column => (
          <div key={column.id} className="bg-white p-4 rounded-lg shadow text-center">
            <div className="text-2xl font-bold text-gray-800">{column.projetos.length}</div>
            <div className="text-sm text-gray-600">{column.title}</div>
          </div>
        ))}
      </div>

      {/* NOVO: Modal do Formulário */}
      {showForm && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
            <ProjectForm
              projeto={editingProjeto || undefined}
              onClose={handleCloseForm}
              onSuccess={handleFormSuccess}
            />
          </div>
        </div>
      )}
    </div>
  );
};