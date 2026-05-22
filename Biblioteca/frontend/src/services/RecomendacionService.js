const API_URL = '/recomendaciones';

const getToken = () => {
  return localStorage.getItem('token') || '';
};

export const RecomendacionService = {
  getAll: async () => {
    const response = await fetch(API_URL, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!response.ok) throw new Error('Error al obtener recomendaciones');
    return response.json();
  },

  getByUsuarioId: async (usuarioId) => {
    const response = await fetch(`${API_URL}/usuario/${usuarioId}`, {
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!response.ok) throw new Error('Error al obtener recomendaciones del usuario');
    return response.json();
  },

  generar: async (usuarioId) => {
    const response = await fetch(`${API_URL}/generar/${usuarioId}`, {
      method: 'POST',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!response.ok) throw new Error('Error al generar recomendaciones');
    return response.json();
  },

  delete: async (id) => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE',
      headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!response.ok) throw new Error('Error al eliminar recomendación');
  }
};
