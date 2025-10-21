import React, { createContext, useContext } from 'react';
import type { ReactNode } from 'react'; // 👈 type-only import
import {
    DndContext as DndKitContext,
    closestCenter,
    KeyboardSensor,
    PointerSensor,
    useSensor,
    useSensors,
    type DragEndEvent,
} from '@dnd-kit/core';
import {
    SortableContext,
    sortableKeyboardCoordinates,
    verticalListSortingStrategy,
} from '@dnd-kit/sortable';

interface DndProviderProps {
    children: ReactNode;
    onDragEnd?: (event: DragEndEvent) => void;
}

// Contexto para compartilhar estado se necessário
interface DndContextType {
    isDragging: boolean;
}

const CustomDndContext = createContext<DndContextType | undefined>(undefined);

export const DndContextProvider: React.FC<DndProviderProps> = ({ 
    children, 
    onDragEnd 
}) => {
    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const [isDragging, setIsDragging] = React.useState(false);

    const handleDragStart = () => {
        setIsDragging(true);
    };

    const handleDragEnd = (event: DragEndEvent) => {
        setIsDragging(false);
        
        const { active, over } = event;

        if (over && active.id !== over.id) {
            console.log('Item movido:', active.id, 'para:', over.id);
        }

        onDragEnd?.(event);
    };

    const handleDragCancel = () => {
        setIsDragging(false);
    };

    const contextValue: DndContextType = {
        isDragging,
    };

    return (
        <CustomDndContext.Provider value={contextValue}>
            <DndKitContext
                sensors={sensors}
                collisionDetection={closestCenter}
                onDragStart={handleDragStart}
                onDragEnd={handleDragEnd}
                onDragCancel={handleDragCancel}
            >
                {children}
            </DndKitContext>
        </CustomDndContext.Provider>
    );
};

// Hook personalizado para usar o contexto
export const useDnd = (): DndContextType => {
    const context = useContext(CustomDndContext);
    if (context === undefined) {
        throw new Error('useDnd deve ser usado dentro de um DndContextProvider');
    }
    return context;
};

// Hook utilitário para criar um contexto sortable
export const useSortableContext = (items: string[]) => {
    return {
        items,
        strategy: verticalListSortingStrategy,
    };
};

// Componente auxiliar para containers sortable
interface SortableContainerProps {
    id: string;
    items: string[];
    children: ReactNode;
    className?: string;
}

export const SortableContainer: React.FC<SortableContainerProps> = ({
    id,
    items,
    children,
    className = '',
}) => {
    const sortableContext = useSortableContext(items);

    return (
        <SortableContext {...sortableContext}>
            <div id={id} className={className}>
                {children}
            </div>
        </SortableContext>
    );
};

export default DndContextProvider;