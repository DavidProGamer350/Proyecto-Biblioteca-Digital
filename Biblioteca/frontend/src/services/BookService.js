const API_URL = '/books';

export const BookService = {
  getAll: async () => {
    const response = await fetch(`${API_URL}`);
    if (!response.ok) {
      throw new Error('Error al obtener libros');
    }
    return response.json();
  },

  getById: async (id) => {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) {
      throw new Error('Libro no encontrado');
    }
    return response.json();
  },

  getByIsbn: async (isbn) => {
    const response = await fetch(`${API_URL}/isbn/${isbn}`);
    if (!response.ok) {
      throw new Error('Libro no encontrado');
    }
    return response.json();
  },

  create: async (bookData) => {
    const formData = new FormData();
    formData.append('book', JSON.stringify({
      titulo: bookData.titulo,
      autor: bookData.autor,
      isbn: bookData.isbn,
      formato: bookData.formato
    }));
    
    if (bookData.file) {
      formData.append('file', bookData.file);
    }

    const response = await fetch(`${API_URL}/upload`, {
      method: 'POST',
      body: formData
    });

    if (!response.ok) {
      throw new Error('Error al crear libro');
    }
    return response.json();
  },

  update: async (id, bookData) => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(bookData)
    });

    if (!response.ok) {
      throw new Error('Error al actualizar libro');
    }
    return response.json();
  },

  delete: async (id) => {
    const response = await fetch(`${API_URL}/${id}`, {
      method: 'DELETE'
    });

    if (!response.ok) {
      throw new Error('Error al eliminar libro');
    }
  },

  download: async (id) => {
    const response = await fetch(`${API_URL}/${id}/download`);
    if (!response.ok) {
      throw new Error('Error al descargar');
    }
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `libro-${id}`;
    a.click();
    window.URL.revokeObjectURL(url);
  }
};