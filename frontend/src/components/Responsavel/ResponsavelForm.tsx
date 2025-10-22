import React, { useState, useEffect } from 'react';
import type { ResponsavelRequest } from '../../types/responsavel/responsavelRequest';
import type { ResponsavelResponse } from '../../types/responsavel/responsavelResponse';
import { responsavelService } from '../../services/responsavelService';

interface ResponsavelFormProps {
  responsavel?: ResponsavelResponse;
  onClose: () => void;
  onSuccess: () => void;
}

export const ResponsavelForm: React.FC<ResponsavelFormProps> = ({
  responsavel,
  onClose,
  onSuccess
}) => {
  const [formData, setFormData] = useState<ResponsavelRequest>({
    nome: '',
    email: '',
    cargo: '',
  });

  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (responsavel) {
      setFormData({
        nome: responsavel.nome,
        email: responsavel.email,
        cargo: responsavel.cargo || '',
      });
    }
  }, [responsavel]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
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

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome é obrigatório';
    } else if (formData.nome.trim().length < 2) {
      newErrors.nome = 'Nome deve ter pelo menos 2 caracteres';
    }

    if (!formData.email.trim()) {
      newErrors.email = 'Email é obrigatório';
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      newErrors.email = 'Email inválido';
    }

    if (formData.cargo && formData.cargo.trim().length < 2) {
      newErrors.cargo = 'Cargo deve ter pelo menos 2 caracteres';
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
      if (responsavel) {
        // Editar responsável existente
        await responsavelService.atualizar(responsavel.id, formData);
      } else {
        // Criar novo responsável
        await responsavelService.criar(formData);
      }
      
      onSuccess();
    } catch (error: any) {
      console.error('Erro ao salvar responsável:', error);
      
      // Tratamento específico para email duplicado
      if (error.response?.data?.message?.includes('email') || error.message?.includes('email')) {
        setErrors({ email: 'Este email já está cadastrado no sistema' });
      } else if (error.response?.data?.message) {
        // Outras mensagens de erro do backend
        setErrors({ submit: error.response.data.message });
      } else {
        setErrors({ submit: 'Erro ao salvar responsável. Tente novamente.' });
      }
    } finally {
      setLoading(false);
    }
  };

  const getAvatarInitials = (name: string) => {
    return name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  const getAvatarColor = (name: string) => {
    const colors = ['bg-blue-500', 'bg-green-500', 'bg-purple-500', 'bg-orange-500'];
    const index = name.length % colors.length;
    return colors[index];
  };

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <div className="flex items-center space-x-4">
          {formData.nome && (
            <div className={`flex-shrink-0 h-12 w-12 rounded-full flex items-center justify-center ${getAvatarColor(formData.nome)}`}>
              <span className="text-white font-medium text-sm">
                {getAvatarInitials(formData.nome)}
              </span>
            </div>
          )}
          <div>
            <h2 className="text-2xl font-bold text-gray-800">
              {responsavel ? '✏️ Editar Responsável' : '👤 Novo Responsável'}
            </h2>
            <p className="text-sm text-gray-600 mt-1">
              {responsavel ? 'Atualize os dados do responsável' : 'Adicione um novo responsável ao sistema'}
            </p>
          </div>
        </div>
        <button
          onClick={onClose}
          className="text-gray-400 hover:text-gray-600 text-2xl transition-colors p-1 hover:bg-gray-100 rounded-full"
          disabled={loading}
        >
          ×
        </button>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Nome */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            👤 Nome Completo *
          </label>
          <input
            type="text"
            name="nome"
            value={formData.nome}
            onChange={handleChange}
            className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
              errors.nome ? 'border-red-500 bg-red-50' : 'border-gray-300'
            }`}
            placeholder="Digite o nome completo"
            disabled={loading}
          />
          {errors.nome && (
            <p className="mt-2 text-sm text-red-600 flex items-center">
              <span className="mr-1">⚠️</span>
              {errors.nome}
            </p>
          )}
        </div>

        {/* Email */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            📧 Email *
          </label>
          <input
            type="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
              errors.email ? 'border-red-500 bg-red-50' : 'border-gray-300'
            }`}
            placeholder="exemplo@empresa.com"
            disabled={loading}
          />
          {errors.email && (
            <p className="mt-2 text-sm text-red-600 flex items-center">
              <span className="mr-1">⚠️</span>
              {errors.email}
            </p>
          )}
        </div>

        {/* Cargo */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-2">
            💼 Cargo
          </label>
          <input
            type="text"
            name="cargo"
            value={formData.cargo}
            onChange={handleChange}
            className={`w-full px-4 py-3 border rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
              errors.cargo ? 'border-red-500 bg-red-50' : 'border-gray-300'
            }`}
            placeholder="Ex: Desenvolvedor Senior, Product Owner, Scrum Master..."
            disabled={loading}
          />
          {errors.cargo && (
            <p className="mt-2 text-sm text-red-600 flex items-center">
              <span className="mr-1">⚠️</span>
              {errors.cargo}
            </p>
          )}
          <p className="mt-1 text-xs text-gray-500">
            Campo opcional - descreva a função do responsável
          </p>
        </div>

        {/* Erro geral */}
        {errors.submit && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-red-700 text-sm flex items-center">
              <span className="mr-2">❌</span>
              {errors.submit}
            </p>
          </div>
        )}

        {/* Ações */}
        <div className="flex justify-end space-x-3 pt-6 border-t border-gray-200">
          <button
            type="button"
            onClick={onClose}
            className="px-6 py-3 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 hover:border-gray-400 transition-colors font-medium disabled:opacity-50 disabled:cursor-not-allowed"
            disabled={loading}
          >
            Cancelar
          </button>
          <button
            type="submit"
            disabled={loading}
            className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center font-medium shadow-sm"
          >
            {loading ? (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                {responsavel ? 'Atualizando...' : 'Criando...'}
              </>
            ) : (
              <>
                <span className="mr-2">{responsavel ? '💾' : '✨'}</span>
                {responsavel ? 'Atualizar Responsável' : 'Criar Responsável'}
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};