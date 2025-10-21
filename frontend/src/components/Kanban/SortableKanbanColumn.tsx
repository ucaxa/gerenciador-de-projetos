import React from 'react';
import { useDroppable } from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import type { KanbanColumn as KanbanColumnType } from '../../types/kanban/kanbanColumn';
import { SortableProjectCard } from './SortableProjectCard';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';

interface SortableKanbanColumnProps {
  column: KanbanColumnType;
  onStatusChange: (projetoId: number, novoStatus: StatusProjeto) => void;
  onEdit?: (projeto: ProjetoResponse) => void;
}

export const SortableKanbanColumn: React.FC<SortableKanbanColumnProps> = ({
  column,
  onStatusChange,
  onEdit
}) => {
  const { isOver, setNodeRef } = useDroppable({
    id: column.id,
  });

  const getStatusOptions = (currentStatus: StatusProjeto): StatusProjeto[] => {
    const allStatus: StatusProjeto[] = ['A_INICIAR', 'EM_ANDAMENTO', 'ATRASADO', 'CONCLUIDO'];
    return allStatus.filter(status => status !== currentStatus);
  };

  return (
    <div 
      ref={setNodeRef}
      className={`rounded-lg border-2 p-4 min-h-[600px] transition-all duration-200 ${
        isOver 
          ? 'border-blue-400 bg-blue-50 shadow-lg scale-[1.02]' 
          : column.color
      }`}
    >
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

      {/* Lista de Projetos - Agora Sortable */}
      <SortableContext 
        items={column.projetos.map(p => p.id.toString())} 
        strategy={verticalListSortingStrategy}
      >
        <div className="space-y-3">
          {column.projetos.length === 0 ? (
            <div className={`text-center py-8 rounded-lg border-2 border-dashed transition-all ${
              isOver 
                ? 'border-blue-300 bg-blue-25 text-blue-600' 
                : 'border-gray-300 text-gray-500'
            }`}>
              <div className="text-4xl mb-2">{isOver ? '🎯' : '📋'}</div>
              <p className="text-sm">{isOver ? 'Solte aqui!' : 'Arraste projetos aqui'}</p>
            </div>
          ) : (
            column.projetos.map(projeto => (
              <SortableProjectCard
                key={projeto.id}
                projeto={projeto}
                statusOptions={getStatusOptions(projeto.status)}
                onStatusChange={onStatusChange}
                onEdit={onEdit} // NOVO: passando callback de edição
              />
            ))
          )}
        </div>
      </SortableContext>
    </div>
  );
};