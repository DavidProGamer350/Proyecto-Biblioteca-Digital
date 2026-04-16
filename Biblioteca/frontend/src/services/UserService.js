const API_URL = '/users';
const BACKEND_URL = 'http://localhost:8080';

const getAuthHeaders = () => {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
};

export const UserService = {
  getAll: async () => {
    const response = await fetch(`${BACKEND_URL}${API_URL}`, {
      headers: getAuthHeaders()
    });
    if (!response.ok) throw new Error('Error al obtener usuarios');
    return response.json();
  },

  getById: async (id) => {
    const response = await fetch(`${BACKEND_URL}${API_URL}/${id}`, {
      headers: getAuthHeaders()
    });
    if (!response.ok) throw new Error('Error al obtener usuario');
    return response.json();
  },

  createUser: async (userData) => {
    const response = await fetch(`${BACKEND_URL}${API_URL}`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(userData)
    });
    if (!response.ok) throw new Error('Error al crear usuario');
    return response.json();
  },

  update: async (id, userData) => {
    const response = await fetch(`${BACKEND_URL}${API_URL}/${id}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(userData)
    });
    if (!response.ok) throw new Error('Error al actualizar usuario');
    return response.json();
  },

  togglePremium: async (id) => {
    const response = await fetch(`${BACKEND_URL}${API_URL}/${id}/premium`, {
      method: 'PUT',
      headers: getAuthHeaders()
    });
    if (!response.ok) throw new Error('Error al cambiar premium');
    return response.json();
  },

  delete: async (id) => {
    const response = await fetch(`${BACKEND_URL}${API_URL}/${id}`, {
      method: 'DELETE',
      headers: getAuthHeaders()
    });
    if (!response.ok) throw new Error('Error al eliminar usuario');
  },

  isPremium: async (id) => {
    const response = await fetch(`${BACKEND_URL}${API_URL}/${id}/premium`, {
      headers: getAuthHeaders()
    });
    if (!response.ok) return false;
    return response.json();
  },

  getCurrentUser: async () => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) throw new Error('No hay usuario');
    return user;
  }
};