import React, { useState, createContext } from 'react';
import { KanbanBoard } from './components/Kanban/KanbanBoard';
import { ProjectList } from './components/Projeto/ProjectList';
import { ResponsavelList } from './components/Responsavel/ResponsavelList';
import { ProjectForm } from './components/Projeto/ProjectForm';

type Page = 'kanban' | 'projects' | 'responsibles';

// Contexto para refresh
export const RefreshContext = createContext({
  refreshKey: 0,
  triggerRefresh: () => {}
});

export const App: React.FC = () => {
  const [currentPage, setCurrentPage] = useState<Page>('kanban');
  const [isCreatingProject, setIsCreatingProject] = useState(false);
  const [refreshKey, setRefreshKey] = useState(0);
  
  const triggerRefresh = () => {
    setRefreshKey(prev => prev + 1);
  };

  const navigation = [
    { id: 'kanban' as Page, name: '📋 Kanban', current: currentPage === 'kanban' },
    { id: 'projects' as Page, name: '🚀 Projetos', current: currentPage === 'projects' },
    { id: 'responsibles' as Page, name: '👥 Responsáveis', current: currentPage === 'responsibles' },
  ];

  const renderPage = () => {
    switch (currentPage) {
      case 'kanban':
        return <KanbanBoard />;
      case 'projects':
        return <ProjectList />;
      case 'responsibles':
        return <ResponsavelList />;
      default:
        return <KanbanBoard />;
    }
  };

  return (
    <RefreshContext.Provider value={{ refreshKey, triggerRefresh }}>
      <div className="min-h-screen bg-gray-50">
        {/* Header */}
        <header className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between items-center h-16">
              {/* Logo e Navegação */}
              <div className="flex items-center">
                <h1 className="text-xl font-bold text-gray-800">
                  🎯 Sistema Kanban
                </h1>
                <nav className="ml-8 flex space-x-4">
                  {navigation.map((item) => (
                    <button
                      key={item.id}
                      onClick={() => setCurrentPage(item.id)}
                      className={`px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                        item.current
                          ? 'bg-blue-100 text-blue-700'
                          : 'text-gray-500 hover:text-gray-700 hover:bg-gray-100'
                      }`}
                    >
                      {item.name}
                    </button>
                  ))}
                </nav>
              </div>

              {/* Botão de Novo Projeto (só mostra no Kanban) */}
              {currentPage === 'kanban' && (
                <button
                  onClick={() => setIsCreatingProject(true)}
                  className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center"
                >
                  <span className="mr-2">+</span>
                  Novo Projeto
                </button>
              )}
            </div>
          </div>
        </header>

        {/* Main Content */}
        <main>
          {renderPage()}
        </main>

        {/* Modal de Criar Projeto */}
        {isCreatingProject && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
            <div className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto">
              <ProjectForm 
                onClose={() => setIsCreatingProject(false)}
                onSuccess={() => {
                  setIsCreatingProject(false);
                  triggerRefresh(); // Usa o contexto de refresh
                }}
              />
            </div>
          </div>
        )}

        {/* Footer */}
        <footer className="bg-white border-t mt-8">
          <div className="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8">
            <p className="text-center text-gray-500 text-sm">
              Sistema Kanban - Desafio Técnico
            </p>
          </div>
        </footer>
      </div>
    </RefreshContext.Provider>
  );
};

export default App;