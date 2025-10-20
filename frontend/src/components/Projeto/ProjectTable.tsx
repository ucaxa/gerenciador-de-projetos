import React, { useState } from 'react';
import type { ProjetoResponse } from '../../types/projeto/projetoResponse';
import { ProjectForm } from './ProjectForm';

interface ProjectTableProps {
  projetos: ProjetoResponse[];
  onDelete: (id: number) => void;
  onRefresh: () => void;
}

export const ProjectTable: React.FC<ProjectTableProps> = ({ 
  projetos, 
  onDelete, 
  onRefresh 
}) => {
  const [editingProject, setEditingProject] = useState<ProjetoResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'A_INICIAR': return 'bg-blue-100 text-blue-800';
      case 'EM_ANDAMENTO': return 'bg-yellow-100 text-yellow-800';
      case 'ATRASADO': return 'bg-red-100 text-red-800';
      case 'CONCLUIDO': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('pt-BR');
  };

  const handleEdit = (projeto: ProjetoResponse) => {
    setEditingProject(projeto);
    setIsFormOpen(true);
  };

  const handleFormSuccess = () => {
    setIsFormOpen(false);
    setEditingProject(null);
    onRefresh();
  };

  const handleFormClose = () => {
    setIsFormOpen(false);
    setEditingProject(null);
  };

  return (
    <>
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Projeto
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Responsáveis
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Datas
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Métricas
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Ações
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {projetos.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                    Nenhum projeto encontrado
                  </td>
                </tr>
              ) : (
                projetos.map((projeto) => (
                  <tr key={projeto.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900">{projeto.nome}</div>
                      <div className="text-sm text-gray-500">
                        Criado em {formatDate(projeto.createdAt)}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(projeto.status)}`}>
                        {projeto.status.replace('_', ' ')}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="text-sm text-gray-900">
                        {projeto.responsaveis.slice(0, 2).map(r => r.nome).join(', ')}
                        {projeto.responsaveis.length > 2 && ` +${projeto.responsaveis.length - 2}`}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <div>Início: {formatDate(projeto.inicioPrevisto)}</div>
                      <div>Término: {formatDate(projeto.terminoPrevisto)}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <div>Atraso: {projeto.diasAtraso || 0}d</div>
                      <div>Restante: {projeto.percentualTempoRestante.toFixed(1)}%</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => handleEdit(projeto)}
                        className="text-blue-600 hover:text-blue-900 mr-4"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => onDelete(projeto.id)}
                        className="text-red-600 hover:text-red-900"
                      >
                        Excluir
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Modal de Edição */}
      {isFormOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto">
            <ProjectForm 
              projeto={editingProject || undefined}
              onClose={handleFormClose}
              onSuccess={handleFormSuccess}
            />
          </div>
        </div>
      )}
    </>
  );
};