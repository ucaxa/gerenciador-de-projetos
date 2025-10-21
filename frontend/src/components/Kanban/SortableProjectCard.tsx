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
      {...listeners}
      className={`cursor-grab active:cursor-grabbing transition-all duration-200 ${
        isDragging ? 'shadow-xl z-10' : 'hover:shadow-md'
      }`}
    >
      <ProjectCard
        projeto={projeto}
        statusOptions={statusOptions}
        onStatusChange={onStatusChange}
        onEdit={onEdit} // NOVO: propagando o callback
      />
    </div>
  );
};