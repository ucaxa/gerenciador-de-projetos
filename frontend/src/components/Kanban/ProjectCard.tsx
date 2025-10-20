// components/Kanban/ProjectCard.tsx
import React, { useState } from 'react';
//import { ProjetoResponse, StatusProjeto } from '../../types/projeto';
import  type {ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';

interface ProjectCardProps {
  projeto: ProjetoResponse;
  statusOptions: StatusProjeto[];
  onStatusChange: (projetoId: number, novoStatus: StatusProjeto) => void;
}

export const ProjectCard: React.FC<ProjectCardProps> = ({ 
  projeto, 
  statusOptions, 
  onStatusChange 
}) => {
  const [isChangingStatus, setIsChangingStatus] = useState(false);

  const getStatusColor = (status: StatusProjeto) => {
    switch (status) {
      case 'A_INICIAR': return 'bg-blue-100 text-blue-800';
      case 'EM_ANDAMENTO': return 'bg-yellow-100 text-yellow-800';
      case 'ATRASADO': return 'bg-red-100 text-red-800';
      case 'CONCLUIDO': return 'bg-green-100 text-green-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return 'Não definida';
    return new Date(dateString).toLocaleDateString('pt-BR');
  };

  const handleStatusSelect = (novoStatus: StatusProjeto) => {
    setIsChangingStatus(false);
    onStatusChange(projeto.id, novoStatus);
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-4 border border-gray-200 hover:shadow-lg transition-shadow">
      {/* Cabeçalho do Card */}
      <div className="flex justify-between items-start mb-3">
        <h4 className="font-semibold text-gray-800 text-sm line-clamp-2 flex-1">
          {projeto.nome}
        </h4>
        
        {/* Dropdown de Status */}
        <div className="relative">
          <button
            onClick={() => setIsChangingStatus(!isChangingStatus)}
            className={`px-2 py-1 rounded text-xs font-medium ${getStatusColor(projeto.status)}`}
          >
            {projeto.status.replace('_', ' ')}
          </button>
          
          {isChangingStatus && (
            <div className="absolute right-0 mt-1 w-40 bg-white border border-gray-200 rounded-lg shadow-lg z-10">
              {statusOptions.map(status => (
                <button
                  key={status}
                  onClick={() => handleStatusSelect(status)}
                  className="block w-full text-left px-3 py-2 text-sm hover:bg-gray-100 first:rounded-t-lg last:rounded-b-lg"
                >
                  {status.replace('_', ' ')}
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Responsáveis */}
      {projeto.responsaveis && projeto.responsaveis.length > 0 && (
        <div className="mb-2">
          <div className="text-xs text-gray-600 mb-1">Responsáveis:</div>
          <div className="flex flex-wrap gap-1">
            {projeto.responsaveis.slice(0, 2).map(responsavel => (
              <span 
                key={responsavel.id}
                className="bg-gray-100 text-gray-700 text-xs px-2 py-1 rounded"
              >
                {responsavel.nome.split(' ')[0]}
              </span>
            ))}
            {projeto.responsaveis.length > 2 && (
              <span className="bg-gray-100 text-gray-700 text-xs px-2 py-1 rounded">
                +{projeto.responsaveis.length - 2}
              </span>
            )}
          </div>
        </div>
      )}

      {/* Datas */}
      <div className="space-y-1 text-xs text-gray-600">
        {projeto.inicioPrevisto && (
          <div>
            <span className="font-medium">Início Previsto:</span> {formatDate(projeto.inicioPrevisto)}
          </div>
        )}
        {projeto.terminoPrevisto && (
          <div>
            <span className="font-medium">Término Previsto:</span> {formatDate(projeto.terminoPrevisto)}
          </div>
        )}
        {projeto.inicioRealizado && (
          <div>
            <span className="font-medium">Início Real:</span> {formatDate(projeto.inicioRealizado)}
          </div>
        )}
      </div>

      {/* Métricas */}
      <div className="mt-3 pt-2 border-t border-gray-200">
        <div className="flex justify-between text-xs">
          <div>
            <span className="font-medium">Atraso:</span> {projeto.diasAtraso || 0}d
          </div>
          <div>
            <span className="font-medium">Tempo Restante:</span> {projeto.percentualTempoRestante.toFixed(1)}%
          </div>
        </div>
        
        {/* Barra de Progresso */}
        <div className="mt-1 w-full bg-gray-200 rounded-full h-2">
          <div 
            className="bg-blue-600 h-2 rounded-full transition-all duration-300"
            style={{ width: `${Math.max(0, Math.min(100, projeto.percentualTempoRestante))}%` }}
          />
        </div>
      </div>
    </div>
  );
};