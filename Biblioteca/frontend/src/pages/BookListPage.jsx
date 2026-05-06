import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BookService } from '../services/BookService';
import { Navbar } from '../components/Navbar';

export const BookListPage = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadBooks();
  }, []);

  const loadBooks = async () => {
    try {
      setLoading(true);
      const data = await BookService.getAll();
      setBooks(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Estás seguro de eliminar este libro?')) return;
    try {
      await BookService.delete(id);
      setBooks(books.filter(b => b.id !== id));
    } catch (err) {
      setError(err.message);
    }
  };

  const filteredBooks = books.filter(book =>
    book.titulo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    book.autor?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    book.isbn?.includes(searchTerm)
  );

  return (
    <div>
      <Navbar />

      <div className="users-container">
        <div className="users-header">
          <h1>Gestión de Libros</h1>
          <div className="header-actions">
            <input
              type="text"
              placeholder="Buscar libros..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <Link to="/libros/nuevo" className="btn btn-primary">
              + Agregar Libro
            </Link>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <div className="loading">Cargando...</div>
        ) : (
          <table className="users-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Título</th>
                <th>Autor</th>
                <th>ISBN</th>
                <th>Formato</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredBooks.map(book => (
                <tr key={book.id}>
                  <td>{book.id}</td>
                  <td>{book.titulo}</td>
                  <td>{book.autor}</td>
                  <td>{book.isbn}</td>
                  <td>
                    <span className={`rol-badge ${book.formato?.toLowerCase()}`}>
                      {book.formato}
                    </span>
                  </td>
                  <td className="actions">
                    <Link
                      to={`/libros/editar/${book.id}`}
                      className="btn-action"
                    >
                      Editar
                    </Link>
                    <button
                      onClick={() => handleDelete(book.id)}
                      className="btn-action btn-delete"
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};