import { useContext } from 'react';
import { RefreshContext } from '../App';

export const useRefresh = () => {
  const context = useContext(RefreshContext);
  
  if (!context) {
    throw new Error('useRefresh deve ser usado dentro de um RefreshContext.Provider');
  }
  
  return context;
};