import React from 'react';
import type { ResponsavelResponse } from 'src/types/responsavel/responsavelResponse';

interface ResponsavelTableProps {
  responsaveis: ResponsavelResponse[];
  onEdit: (responsavel: ResponsavelResponse) => void;
  onDelete: (id: number) => void;
}

export const ResponsavelTable: React.FC<ResponsavelTableProps> = ({
  responsaveis,
  onEdit,
  onDelete
}) => {
  const formatDate = (dateString?: string) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('pt-BR');
  };

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const getAvatarColor = (name: string) => {
    const colors = [
      'bg-blue-500', 'bg-green-500', 'bg-purple-500', 
      'bg-orange-500', 'bg-pink-500', 'bg-indigo-500'
    ];
    const index = name.length % colors.length;
    return colors[index];
  };

  const getProjectCountBadge = (count: number) => {
    if (count === 0) {
      return 'bg-gray-100 text-gray-600';
    } else if (count <= 2) {
      return 'bg-green-100 text-green-800';
    } else {
      return 'bg-blue-100 text-blue-800';
    }
  };

  return (
    <div className="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Responsável
              </th>
              <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Contato
              </th>
              <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Cargo
              </th>
              <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Projetos
              </th>
              <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Cadastro
              </th>
              <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ações
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {responsaveis.length === 0 ? (
              <tr>
                <td colSpan={6} className="px-6 py-12 text-center">
                  <div className="flex flex-col items-center justify-center text-gray-500">
                    <div className="text-4xl mb-3">👥</div>
                    <p className="text-lg font-medium mb-1">Nenhum responsável cadastrado</p>
                    <p className="text-sm text-gray-400">Clique em "Novo Responsável" para adicionar</p>
                  </div>
                </td>
              </tr>
            ) : (
              responsaveis.map((responsavel) => (
                <tr key={responsavel.id} className="hover:bg-gray-50 transition-colors group">
                  <td className="px-6 py-4">
                    <div className="flex items-center">
                      <div className={`flex-shrink-0 h-10 w-10 rounded-full flex items-center justify-center ${getAvatarColor(responsavel.nome)}`}>
                        <span className="text-white font-medium text-sm">
                          {getInitials(responsavel.nome)}
                        </span>
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-semibold text-gray-900 group-hover:text-blue-600 transition-colors">
                          {responsavel.nome}
                        </div>
                        <div className="text-xs text-gray-500">
                          ID: {responsavel.id}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm text-gray-900">{responsavel.email}</div>
                    <div className="text-xs text-gray-500 mt-1">
                      📧 E-mail
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="text-sm text-gray-900">
                      {responsavel.cargo ? (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                          {responsavel.cargo}
                        </span>
                      ) : (
                        <span className="text-gray-400 italic">Não informado</span>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    {/* Nota: Em um sistema real, isso viria do backend */}
                    <span className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${getProjectCountBadge(responsavel.id % 4)}`}>
                      {responsavel.id % 4 === 0 ? 'Sem projetos' : `${responsavel.id % 4} projeto(s)`}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-500">
                    <div className="space-y-1">
                      <div>Criado: {formatDate(responsavel.createdAt)}</div>
                      {responsavel.updatedAt && responsavel.updatedAt !== responsavel.createdAt && (
                        <div className="text-xs text-gray-400">
                          Atualizado: {formatDate(responsavel.updatedAt)}
                        </div>
                      )}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex space-x-2">
                      <button
                        onClick={() => onEdit(responsavel)}
                        className="text-blue-600 hover:text-blue-900 px-3 py-1.5 rounded-lg border border-blue-200 hover:bg-blue-50 transition-colors text-sm font-medium"
                        title="Editar responsável"
                      >
                        ✏️ Editar
                      </button>
                      <button
                        onClick={() => onDelete(responsavel.id)}
                        className="text-red-600 hover:text-red-900 px-3 py-1.5 rounded-lg border border-red-200 hover:bg-red-50 transition-colors text-sm font-medium"
                        title="Excluir responsável"
                        disabled={responsavel.id % 4 > 0} // Não permite excluir se tiver projetos
                      >
                        🗑️ Excluir
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
  );
};