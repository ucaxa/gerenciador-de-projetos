import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { StatusProjeto } from 'src/types/projeto/statusprojeto'; // 👈 IMPORTE O TIPO
import { ProjectCard } from 'src/components/Kanban/ProjectCard';

interface SortableProjectCardProps {
  projeto: ProjetoResponse;
  statusOptions: StatusProjeto[]; 
  onStatusChange: (projetoId: number, novoStatus: StatusProjeto) => void; // 👈 MUDE string PARA StatusProjeto
}

export const SortableProjectCard: React.FC<SortableProjectCardProps> = ({
  projeto,
  statusOptions,
  onStatusChange
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
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      className="cursor-grab active:cursor-grabbing"
    >
      <ProjectCard
        projeto={projeto}
        statusOptions={statusOptions}
        onStatusChange={onStatusChange}
      />
    </div>
  );
};