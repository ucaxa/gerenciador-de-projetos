// components/Kanban/SortableProjectCard.tsx
import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto';
import { ProjectCard } from './ProjectCard';

interface SortableProjectCardProps {
  projeto: ProjetoResponse;
  statusOptions: StatusProjeto[];
  onStatusChange: (projetoId: number, novoStatus: StatusProjeto) => void;
  onEdit?: (projeto: ProjetoResponse) => void;
}

export const SortableProjectCard: React.FC<SortableProjectCardProps> = ({
  projeto,
  statusOptions,
  onStatusChange,
  onEdit
}) => {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: projeto.id.toString() });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.6 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      // REMOVIDO: {...listeners} da div principal para não bloquear cliques
      className={`transition-all duration-200 ${
        isDragging ? 'shadow-xl z-10 rotate-2' : 'hover:shadow-md'
      }`}
    >
      {/* Área de drag separada */}
      <div 
        {...listeners} // Aplica listeners apenas nesta área
        className="cursor-grab active:cursor-grabbing"
      >
        <ProjectCard
          projeto={projeto}
          statusOptions={statusOptions}
          onStatusChange={onStatusChange}
          onEdit={onEdit}
        />
      </div>
    </div>
  );
};