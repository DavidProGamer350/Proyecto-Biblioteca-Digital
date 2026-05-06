const API_URL = '/prestamos';

const getToken = () => {
  return localStorage.getItem('token') || '';
};

export const LoanService = {
  getAll: async () => {
    const response = await fetch(`${API_URL}`, {
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });
    if (!response.ok) {
      throw new Error('Error al obtener préstamos');
    }
    return response.json();
  },

  getMyLoans: async () => {
    const response = await fetch(`${API_URL}/mis-prestamos`, {
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });
    if (!response.ok) {
      throw new Error('Error al obtener tus préstamos');
    }
    return response.json();
  },

  create: async (usuarioId, libroId) => {
    const response = await fetch(`${API_URL}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${getToken()}`
      },
      body: JSON.stringify({
        usuarioId: usuarioId,
        libroId: libroId
      })
    });

    if (!response.ok) {
      throw new Error('Error al crear préstamo');
    }
    return response.json();
  },

  returnLoan: async (id) => {
    const response = await fetch(`${API_URL}/${id}/devolver`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });

    if (!response.ok) {
      throw new Error('Error al devolver préstamo');
    }
    return response.json();
  },

  renew: async (id, dias = 7) => {
    const response = await fetch(`${API_URL}/${id}/renovar?dias=${dias}`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${getToken()}`
      }
    });

    if (!response.ok) {
      throw new Error('Error al renovar préstamo');
    }
    return response.json();
  }
};