import React from 'react';
import { useDroppable } from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy } from '@dnd-kit/sortable';
import type { KanbanColumn as KanbanColumnType } from 'src/types';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto'; // 👈 IMPORTE O TIPO
import { ProjectCard } from 'src/components/Kanban/ProjectCard';

interface SortableKanbanColumnProps {
  column: KanbanColumnType;
  onStatusChange: (projetoId: number, novoStatus: StatusProjeto) => void; // 👈 MUDE string PARA StatusProjeto
}

export const SortableKanbanColumn: React.FC<SortableKanbanColumnProps> = ({
  column,
  onStatusChange
}) => {
  const { setNodeRef } = useDroppable({
    id: column.id,
  });

  const getStatusOptions = (currentStatus: StatusProjeto): StatusProjeto[] => {
    const allStatus: StatusProjeto[] = ['A_INICIAR', 'EM_ANDAMENTO', 'ATRASADO', 'CONCLUIDO'];
    return allStatus.filter(status => status !== currentStatus);
  };

  return (
    <div 
      ref={setNodeRef}
      className={`rounded-lg border-2 ${column.color} p-4 min-h-[600px]`}
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
            <div className="text-center py-8 text-gray-500">
              <div className="text-4xl mb-2">📋</div>
              <p>Arraste projetos aqui</p>
            </div>
          ) : (
            column.projetos.map(projeto => (
              <ProjectCard
                key={projeto.id}
                projeto={projeto}
                statusOptions={getStatusOptions(projeto.status as StatusProjeto)} // 👈 CONVERTA PARA StatusProjeto
                onStatusChange={onStatusChange}
              />
            ))
          )}
        </div>
      </SortableContext>
    </div>
  );
};