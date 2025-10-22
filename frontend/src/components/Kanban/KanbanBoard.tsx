// components/Kanban/KanbanBoard.tsx
import React, { useState, useEffect } from 'react';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';
import { projetoService } from '../../services/projetoService';
import { ProjectForm } from '../Projeto/ProjectForm';
import { KanbanColumn } from './KanbanColumn';

export const KanbanBoard: React.FC = () => {
  const [projetos, setProjetos] = useState<ProjetoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editingProjeto, setEditingProjeto] = useState<ProjetoResponse | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [notification, setNotification] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

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

  // Mostrar notificação
  const showNotification = (message: string, type: 'success' | 'error') => {
    setNotification({ message, type });
    setTimeout(() => setNotification(null), 5000);
  };

  // Carregar projetos
  const carregarProjetos = async () => {
    try {
      setLoading(true);
      const data = await projetoService.listarTodos();
      setProjetos(data);
      setError(null);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Erro ao carregar projetos';
      setError(errorMessage);
      showNotification(errorMessage, 'error');
      console.error('Erro:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarProjetos();
  }, []);

  // Handler para mudança de status via dropdown
  const handleStatusChange = async (projetoId: number, novoStatus: StatusProjeto) => {
    try {
      console.log('Mudando status:', projetoId, 'para:', novoStatus);
      
      // Atualiza otimisticamente a UI
      setProjetos(prev => 
        prev.map(p => 
          p.id === projetoId ? { ...p, status: novoStatus } : p
        )
      );

      await projetoService.transicionarStatus(projetoId, novoStatus);
      
      // Recarrega para garantir que as métricas estão atualizadas
      await carregarProjetos();
      
      showNotification(`Status alterado para ${novoStatus.replace('_', ' ').toLowerCase()} com sucesso!`, 'success');
      
    } catch (error: any) {
      console.error('Erro na transição:', error);
      
      // ✅ CORREÇÃO: Pegar a mensagem personalizada do backend
      const errorMessage = error.response?.data?.message 
        || error.message 
        || 'Erro ao mudar status';
      
      showNotification(errorMessage, 'error');
      
      // Reverte em caso de erro
      await carregarProjetos();
    }
  };

  // Handlers para edição
  const handleEditProjeto = (projeto: ProjetoResponse) => {
    setEditingProjeto(projeto);
    setShowForm(true);
  };

  const handleCloseForm = () => {
    setShowForm(false);
    setEditingProjeto(null);
  };

  const handleFormSuccess = async () => {
  await carregarProjetos(); // Atualiza os projetos primeiro
  handleCloseForm();        // Depois fecha o modal
  showNotification(
    editingProjeto ? 'Projeto atualizado com sucesso!' : 'Projeto criado com sucesso!',
    'success'
  );
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
          className="ml-4 bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 transition-colors"
        >
          Tentar Novamente
        </button>
      </div>
    );
  }

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      {/* Notificação */}
      {notification && (
        <div className={`fixed top-4 right-4 z-50 p-4 rounded-lg shadow-lg max-w-md ${
          notification.type === 'success' 
            ? 'bg-green-500 text-white' 
            : 'bg-red-500 text-white'
        }`}>
          <div className="flex justify-between items-center">
            <span>{notification.message}</span>
            <button 
              onClick={() => setNotification(null)}
              className="ml-4 text-white hover:text-gray-200"
            >
              ✕
            </button>
          </div>
        </div>
      )}

      <div className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Quadro Kanban</h1>
        <p className="text-gray-600">Use o dropdown em cada card para mudar o status</p>
      </div>

      {/* Grid de colunas SEM drag-and-drop por enquanto */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {columns.map(column => (
          <KanbanColumn
            key={column.id}
            column={column}
            onStatusChange={handleStatusChange}
            onEdit={handleEditProjeto}
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

      {/* Modal do Formulário */}
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