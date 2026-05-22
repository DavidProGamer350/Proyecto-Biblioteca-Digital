import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { BookService } from '../services/BookService';
import { LoanService } from '../services/LoanService';
import { AuthContext } from '../context/AuthContext';
import { useContext } from 'react';
import { Navbar } from '../components/Navbar';

export const CatalogPage = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [selectedBook, setSelectedBook] = useState(null);
  const { user } = useContext(AuthContext);

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

  const handleLoanRequest = (book) => {
    if (!user) {
      // Guardar libro pendiente en localStorage
      localStorage.setItem('pendingLoan', JSON.stringify({ libroId: book.id, titulo: book.titulo }));
      setSelectedBook(book);
      setShowLoginModal(true);
      return;
    }
    // Usuario logueado - solicitar préstamo
    requestLoan(book);
  };

  const requestLoan = async (book) => {
    try {
      await LoanService.create(user.id, book.id);
      alert(`Préstamo de "${book.titulo}" solicitado exitosamente. Tienes 14 días para leerlo.`);
    } catch (err) {
      setError('Error al solicitar préstamo: ' + err.message);
    }
  };

  const handleCloseModal = () => {
    setShowLoginModal(false);
    setSelectedBook(null);
  };

  const handleGoToLogin = () => {
    window.location.href = '/login';
  };

  const filteredBooks = books.filter(book =>
    book.titulo?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    book.autor?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    book.isbn?.includes(searchTerm)
  );

  return (
    <div>
      <Navbar />

      <div className="dashboard">
        <h1 className="dashboard-title">Catálogo de Libros</h1>
        
        <div className="users-header">
          <div className="header-actions">
            <input
              type="text"
              placeholder="Buscar por título, autor o ISBN..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}

        {loading ? (
          <div className="loading">Cargando libros...</div>
        ) : (
          <div className="dashboard-grid">
            {filteredBooks.map(book => (
              <div key={book.id} className="dashboard-card">
                <h3>{book.titulo}</h3>
                <p><strong>Autor:</strong> {book.autor}</p>
                <p><strong>ISBN:</strong> {book.isbn}</p>
                <p>
                  <span className={`rol-badge ${book.formato?.toLowerCase()}`}>
                    {book.formato}
                  </span>
                </p>
                {user ? (
                  <button
                    onClick={() => requestLoan(book)}
                    className="btn btn-primary"
                    style={{ marginTop: '10px', width: '100%' }}
                  >
                    Solicitar Préstamo
                  </button>
                ) : (
                  <button
                    onClick={() => handleLoanRequest(book)}
                    className="btn btn-primary"
                    style={{ marginTop: '10px', width: '100%' }}
                  >
                    Solicitar Préstamo
                  </button>
                )}
              </div>
            ))}
          </div>
        )}

        {!loading && filteredBooks.length === 0 && (
          <div className="proximamente">
            <p>No se encontraron libros que coincidan con tu búsqueda.</p>
          </div>
        )}
      </div>

      {/* Modal de Login */}
      {showLoginModal && (
        <div className="modal-overlay">
          <div className="modal" style={{ maxWidth: '400px', textAlign: 'center' }}>
            <h2>Acceso Requerido</h2>
            <p>Para solicitar el préstamo de "<strong>{selectedBook?.titulo}</strong>" debes iniciar sesión.</p>
            <div className="form-actions" style={{ justifyContent: 'center', marginTop: '20px' }}>
              <button onClick={handleGoToLogin} className="btn btn-primary">
                Iniciar Sesión
              </button>
              <button onClick={handleCloseModal} className="btn btn-secondary">
                Cancelar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};