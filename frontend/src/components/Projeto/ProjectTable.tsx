import React, { useState } from 'react';
import type { ProjetoResponse } from '../../types/projeto/projetoResponse';
import type { StatusProjeto } from '../../types/projeto/statusprojeto';
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

  const getStatusColor = (status: StatusProjeto) => {
    switch (status) {
      case 'A_INICIAR': return 'bg-blue-100 text-blue-800';
      case 'EM_ANDAMENTO': return 'bg-yellow-100 text-yellow-800';
      case 'ATRASADO': return 'bg-red-100 text-red-800';
      case 'CONCLUIDO': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusText = (status: StatusProjeto) => {
    return status.replace('_', ' ');
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
                  Datas Previstas
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
                    <div className="flex flex-col items-center justify-center">
                      <div className="text-4xl mb-2">📋</div>
                      <p>Nenhum projeto encontrado</p>
                      <p className="text-sm text-gray-400">Tente ajustar os filtros</p>
                    </div>
                  </td>
                </tr>
              ) : (
                projetos.map((projeto) => (
                  <tr key={projeto.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="text-sm font-medium text-gray-900">{projeto.nome}</div>
                      <div className="text-sm text-gray-500">
                        Criado em {formatDate(projeto.createdAt)}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(projeto.status)}`}>
                        {getStatusText(projeto.status)}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex flex-wrap gap-1 max-w-xs">
                        {projeto.responsaveis.slice(0, 3).map(responsavel => (
                          <span 
                            key={responsavel.id}
                            className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-100 text-gray-800"
                            title={responsavel.email}
                          >
                            {responsavel.nome.split(' ')[0]}
                          </span>
                        ))}
                        {projeto.responsaveis.length > 3 && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-gray-200 text-gray-600">
                            +{projeto.responsaveis.length - 3}
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      <div className="space-y-1">
                        <div>
                          <span className="font-medium">Início:</span> {formatDate(projeto.inicioPrevisto)}
                        </div>
                        <div>
                          <span className="font-medium">Término:</span> {formatDate(projeto.terminoPrevisto)}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      <div className="space-y-1">
                        <div>
                          <span className="font-medium">Atraso:</span> {projeto.diasAtraso || 0}d
                        </div>
                        <div>
                          <span className="font-medium">Restante:</span> {projeto.percentualTempoRestante.toFixed(1)}%
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleEdit(projeto)}
                          className="text-blue-600 hover:text-blue-900 px-3 py-1 rounded border border-blue-200 hover:bg-blue-50 transition-colors"
                          title="Editar projeto"
                        >
                          Editar
                        </button>
                        <button
                          onClick={() => onDelete(projeto.id)}
                          className="text-red-600 hover:text-red-900 px-3 py-1 rounded border border-red-200 hover:bg-red-50 transition-colors"
                          title="Excluir projeto"
                        >
                          Excluir
                        </button>
                      </div>
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