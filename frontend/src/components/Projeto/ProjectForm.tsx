// components/Projeto/ProjectForm.tsx
import React, { useState, useEffect } from 'react';
//import { ProjetoRequest, ProjetoResponse, ResponsavelResponse } from '../../types';
import type { ProjetoRequest } from 'src/types/projeto/projetoRequest';
import type { ProjetoResponse } from 'src/types/projeto/projetoResponse';
import type { ResponsavelResponse } from 'src/types/responsavel/responsavelResponse';

import { projetoService } from '../../services/projetoService';
import { responsavelService } from '../../services/responsavelService';

interface ProjectFormProps {
  projeto?: ProjetoResponse; // Para edição
  onClose: () => void;
  onSuccess: () => void;
}

export const ProjectForm: React.FC<ProjectFormProps> = ({ 
  projeto, 
  onClose, 
  onSuccess 
}) => {
  const [formData, setFormData] = useState<ProjetoRequest>({
    nome: '',
    responsavelIds: [],
    inicioPrevisto: '',
    terminoPrevisto: '',
    inicioRealizado: '',
    terminoRealizado: '',
  });

  const [responsaveis, setResponsaveis] = useState<ResponsavelResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Carregar responsáveis e dados do projeto (se edição)
  useEffect(() => {
    const carregarDados = async () => {
      try {
        const responsaveisData = await responsavelService.listarTodos();
        setResponsaveis(responsaveisData);

        if (projeto) {
          // Preencher formulário para edição
          setFormData({
            nome: projeto.nome,
            responsavelIds: projeto.responsaveis.map(r => r.id),
            inicioPrevisto: projeto.inicioPrevisto || '',
            terminoPrevisto: projeto.terminoPrevisto || '',
            inicioRealizado: projeto.inicioRealizado || '',
            terminoRealizado: projeto.terminoRealizado || '',
          });
        }
      } catch (error) {
        console.error('Erro ao carregar dados:', error);
        alert('Erro ao carregar dados do formulário');
      }
    };

    carregarDados();
  }, [projeto]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // Limpar erro do campo
    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  const handleResponsavelChange = (responsavelId: number, checked: boolean) => {
    setFormData(prev => ({
      ...prev,
      responsavelIds: checked
        ? [...(prev.responsavelIds || []), responsavelId]
        : (prev.responsavelIds || []).filter(id => id !== responsavelId)
    }));
  };

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome do projeto é obrigatório';
    }

    // Validação de datas
    if (formData.inicioPrevisto && formData.terminoPrevisto) {
      const inicio = new Date(formData.inicioPrevisto);
      const termino = new Date(formData.terminoPrevisto);
      
      if (termino < inicio) {
        newErrors.terminoPrevisto = 'Término não pode ser antes do início';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
  e.preventDefault();

  if (!validateForm()) {
    return;
  }

  setLoading(true);
  try {
    if (projeto) {
      await projetoService.atualizar(projeto.id, formData);
    } else {
      await projetoService.criar(formData); // ✅ Aguarda criação
    }

    onSuccess(); // ✅ Chama onSuccess após sucesso
  } catch (error) {
    console.error('Erro ao salvar projeto:', error);
    alert('Erro ao salvar projeto: ' + (error as Error).message);
  } finally {
    setLoading(false);
  }
};

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-bold text-gray-800">
          {projeto ? 'Editar Projeto' : 'Novo Projeto'}
        </h2>
        <button
          onClick={onClose}
          className="text-gray-500 hover:text-gray-700 text-2xl"
        >
          ×
        </button>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Nome do Projeto */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Nome do Projeto *
          </label>
          <input
            type="text"
            name="nome"
            value={formData.nome}
            onChange={handleChange}
            className={`w-full px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
              errors.nome ? 'border-red-500' : 'border-gray-300'
            }`}
            placeholder="Digite o nome do projeto"
          />
          {errors.nome && (
            <p className="mt-1 text-sm text-red-600">{errors.nome}</p>
          )}
        </div>

        {/* Responsáveis */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            Responsáveis
          </label>
          <div className="max-h-40 overflow-y-auto border border-gray-300 rounded-lg p-3">
            {responsaveis.length === 0 ? (
              <p className="text-gray-500 text-sm">Nenhum responsável cadastrado</p>
            ) : (
              responsaveis.map(responsavel => (
                <label key={responsavel.id} className="flex items-center space-x-3 py-1">
                  <input
                    type="checkbox"
                    checked={formData.responsavelIds?.includes(responsavel.id) || false}
                    onChange={(e) => handleResponsavelChange(responsavel.id, e.target.checked)}
                    className="rounded text-blue-600 focus:ring-blue-500"
                  />
                  <span className="text-sm text-gray-700">
                    {responsavel.nome} ({responsavel.email})
                    {responsavel.cargo && ` - ${responsavel.cargo}`}
                  </span>
                </label>
              ))
            )}
          </div>
        </div>

        {/* Datas Previstas */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Início Previsto
            </label>
            <input
              type="date"
              name="inicioPrevisto"
              value={formData.inicioPrevisto}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Término Previsto
            </label>
            <input
              type="date"
              name="terminoPrevisto"
              value={formData.terminoPrevisto}
              onChange={handleChange}
              className={`w-full px-3 py-2 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                errors.terminoPrevisto ? 'border-red-500' : 'border-gray-300'
              }`}
            />
            {errors.terminoPrevisto && (
              <p className="mt-1 text-sm text-red-600">{errors.terminoPrevisto}</p>
            )}
          </div>
        </div>

        {/* Datas Realizadas */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Início Realizado
            </label>
            <input
              type="date"
              name="inicioRealizado"
              value={formData.inicioRealizado}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Término Realizado
            </label>
            <input
              type="date"
              name="terminoRealizado"
              value={formData.terminoRealizado}
              onChange={handleChange}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        {/* Ações */}
        <div className="flex justify-end space-x-3 pt-4 border-t">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300 transition-colors"
            disabled={loading}
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center"
          >
            {loading ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                Salvando...
              </>
            ) : (
              projeto ? 'Atualizar Projeto' : 'Criar Projeto'
            )}
          </button>
        </div>
      </form>
    </div>
  );
};