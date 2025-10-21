// components/Kanban/KanbanColumn.tsx
import React from 'react';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';
import { projetoService } from '../../services/projetoService';
import { ProjectCard } from './ProjectCard';

interface KanbanColumnProps {
  column: {
    id: StatusProjeto;
    title: string;
    color: string;
    projetos: ProjetoResponse[];
  };
  onStatusChange: (projetoId: number, novoStatus: StatusProjeto) => void;
  onEdit?: (projeto: ProjetoResponse) => void; // NOVO: callback para edição
}

export const KanbanColumn: React.FC<KanbanColumnProps> = ({ 
  column, 
  onStatusChange,
  onEdit // NOVO
}) => {
  const getStatusOptions = (currentStatus: StatusProjeto): StatusProjeto[] => {
    const allStatus: StatusProjeto[] = ['A_INICIAR', 'EM_ANDAMENTO', 'ATRASADO', 'CONCLUIDO'];
    return allStatus.filter(status => status !== currentStatus);
  };

  const handleStatusTransition = async (projetoId: number, novoStatus: StatusProjeto) => {
    try {
      await projetoService.transicionarStatus(projetoId, novoStatus);
      onStatusChange(projetoId, novoStatus);
    } catch (error) {
      console.error('Erro na transição:', error);
      alert('Erro ao mudar status: ' + (error as Error).message);
    }
  };

  return (
    <div className={`rounded-lg border-2 ${column.color} p-4 min-h-[600px]`}>
      {/* Cabeçalho da Coluna */}
      <div className="flex justify-between items-center mb-4">
        <div>
          <h3 className="font-semibold text-lg text-gray-800">{column.title}</h3>
          <span className="text-sm text-gray-600">
            {column.projetos.length} projeto{column.projetos.length !== 1 ? 's' : ''}
          </span>
        </div>
        <div className={`w-3 h-3 rounded-full ${
          column.id === 'A_INICIAR' ? 'bg-blue-500' :
          column.id === 'EM_ANDAMENTO' ? 'bg-yellow-500' :
          column.id === 'ATRASADO' ? 'bg-red-500' : 'bg-green-500'
        }`} />
      </div>

      {/* Lista de Projetos */}
      <div className="space-y-3">
        {column.projetos.length === 0 ? (
          <div className="text-center py-8 text-gray-500">
            <div className="text-4xl mb-2">📋</div>
            <p>Nenhum projeto</p>
          </div>
        ) : (
          column.projetos.map(projeto => (
            <ProjectCard
              key={projeto.id}
              projeto={projeto}
              statusOptions={getStatusOptions(projeto.status)}
              onStatusChange={handleStatusTransition}
              onEdit={onEdit} // NOVO: passando callback de edição
            />
          ))
        )}
      </div>
    </div>
  );
};