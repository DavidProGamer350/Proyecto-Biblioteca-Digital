import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { BookService } from '../services/BookService';

export const BookCreatePage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    titulo: '',
    autor: '',
    isbn: '',
    formato: 'PDF'
  });
  const [file, setFile] = useState(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      
      // Auto-detectar formato basado en extensión
      const extension = selectedFile.name.split('.').pop().toLowerCase();
      if (['pdf', 'epub', 'mobi'].includes(extension)) {
        setFormData(prev => ({ ...prev, formato: extension.toUpperCase() }));
      }
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSaving(true);

    try {
      const bookData = {
        ...formData,
        file: file
      };
      
      await BookService.create(bookData);
      alert('Libro creado exitosamente');
      navigate('/libros');
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <nav className="navbar">
        <Link to="/dashboard" className="navbar-brand">Biblioteca Digital</Link>
        <div className="navbar-menu">
          <Link to="/libros" className="navbar-link">← Volver a Libros</Link>
        </div>
      </nav>

      <div className="container">
        <h1>Crear Nuevo Libro</h1>
        
        {error && <div className="error-message">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Título</label>
            <input
              type="text"
              name="titulo"
              value={formData.titulo}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Autor</label>
            <input
              type="text"
              name="autor"
              value={formData.autor}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>ISBN</label>
            <input
              type="text"
              name="isbn"
              value={formData.isbn}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Formato</label>
            <select
              name="formato"
              value={formData.formato}
              onChange={handleChange}
              required
            >
              <option value="PDF">PDF</option>
              <option value="EPUB">EPUB</option>
              <option value="MOBI">MOBI</option>
            </select>
          </div>

          <div className="form-group">
            <label>Archivo</label>
            <input
              type="file"
              onChange={handleFileChange}
              accept=".pdf,.epub,.mobi"
              required
            />
            {file && (
              <small>Archivo seleccionado: {file.name} ({(file.size / 1024).toFixed(2)} KB)</small>
            )}
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Guardando...' : 'Crear Libro'}
            </button>
            <Link to="/libros" className="btn btn-secondary">
              Cancelar
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
};