import { useState, useEffect, useContext } from 'react';
import { Link } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { UserService } from '../services/UserService';

export const AdminUsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editingUser, setEditingUser] = useState(null);
  const [creatingUser, setCreatingUser] = useState(false);
  const [newUser, setNewUser] = useState({
    name: '',
    email: '',
    passwordHash: '',
    rol: 'USER',
    suscripcionActiva: false,
    fechaSuscripcion: '',
    fechaExpiracionSuscripcion: ''
  });
  const [searchTerm, setSearchTerm] = useState('');
  const { user: currentUser } = useContext(AuthContext);

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const data = await UserService.getAll();
      setUsers(data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('¿Estás seguro de eliminar este usuario?')) return;
    try {
      await UserService.delete(id);
      setUsers(users.filter(u => u.id !== id));
    } catch (err) {
      setError(err.message);
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    try {
      // Preparar datos para actualizar incluyendo suscripcionActiva
      const updateData = {
        name: editingUser.name,
        email: editingUser.email,
        rol: editingUser.rol,
        suscripcionActiva: editingUser.suscripcionActiva
      };
      
      // Solo agregar contraseña si se proporciona una nueva
      if (editingUser.newPassword && editingUser.newPassword.length > 0) {
        updateData.passwordHash = editingUser.newPassword;
      }
      
      // Fechas de suscripción (admin puede editar)
      if (editingUser.fechaSuscripcion) {
        updateData.fechaSuscripcion = editingUser.fechaSuscripcion;
      }
      if (editingUser.fechaExpiracionSuscripcion) {
        updateData.fechaExpiracionSuscripcion = editingUser.fechaExpiracionSuscripcion;
      }
      
      await UserService.update(editingUser.id, updateData);
      setEditingUser(null);
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return '-';
    const date = new Date(dateString);
    // Ajustar por zona horaria
    const userTimezoneOffset = date.getTimezoneOffset() * 60000;
    const adjustedDate = new Date(date.getTime() + userTimezoneOffset);
    return adjustedDate.toLocaleDateString('es-ES');
  };

  const togglePremium = async (user) => {
    try {
      await UserService.togglePremium(user.id);
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    try {
      // Solo incluir fechas si es premium
      const userData = { ...newUser };
      if (!newUser.suscripcionActiva) {
        delete userData.fechaSuscripcion;
        delete userData.fechaExpiracionSuscripcion;
      }
      await UserService.createUser(userData);
      setCreatingUser(false);
      setNewUser({
        name: '',
        email: '',
        passwordHash: '',
        rol: 'USER',
        suscripcionActiva: false,
        fechaSuscripcion: '',
        fechaExpiracionSuscripcion: ''
      });
      loadUsers();
    } catch (err) {
      setError(err.message);
    }
  };

  const filteredUsers = users.filter(user =>
    user.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div>
      <nav className="navbar">
        <Link to="/dashboard" className="navbar-brand">Biblioteca Digital</Link>
        <div className="navbar-menu">
          <Link to="/dashboard" className="navbar-link">Panel</Link>
          <Link to="/books" className="navbar-link">Libros</Link>
          <span className="navbar-user">{currentUser?.name} (Admin)</span>
        </div>
      </nav>

      <div className="users-container">
        <div className="users-header">
          <h1>Gestión de Usuarios</h1>
          <div className="header-actions">
            <input
              type="text"
              placeholder="Buscar usuarios..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="search-input"
            />
            <button 
              onClick={() => setCreatingUser(true)} 
              className="btn btn-primary"
            >
              + Agregar Usuario
            </button>
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
                <th>Nombre</th>
                <th>Email</th>
                <th>Rol</th>
                <th>Estado</th>
                <th>Inicio</th>
                <th>Expiración</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map(user => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>
                    <span className={`rol-badge ${user.rol?.toLowerCase()}`}>
                      {user.rol}
                    </span>
                  </td>
                  <td>
                    <button
                      onClick={() => togglePremium(user)}
                      className={`btn-status ${user.suscripcionActiva ? 'active' : 'inactive'}`}
                    >
                      {user.suscripcionActiva ? 'Premium' : 'Gratis'}
                    </button>
                  </td>
                  <td>{formatDate(user.fechaSuscripcion)}</td>
                  <td>{formatDate(user.fechaExpiracionSuscripcion)}</td>
                  <td className="actions">
                    <button
                      onClick={() => setEditingUser({...user, newPassword: ''})}
                      className="btn-action"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(user.id)}
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

        {editingUser && (
          <div className="modal-overlay">
            <div className="modal">
              <h2>Editar Usuario</h2>
              <form onSubmit={handleUpdate} className="auth-form">
                <div className="form-group">
                  <label>Nombre</label>
                  <input
                    type="text"
                    value={editingUser.name}
                    onChange={(e) => setEditingUser({ ...editingUser, name: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    value={editingUser.email}
                    onChange={(e) => setEditingUser({ ...editingUser, email: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Rol</label>
                  <select
                    value={editingUser.rol}
                    onChange={(e) => setEditingUser({ ...editingUser, rol: e.target.value })}
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>Fecha Inicio Suscripción</label>
                  <input
                    type="date"
                    value={editingUser.fechaSuscripcion ? editingUser.fechaSuscripcion.substring(0, 10) : ''}
                    onChange={(e) => setEditingUser({ ...editingUser, fechaSuscripcion: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Fecha Expiración Suscripción</label>
                  <input
                    type="date"
                    value={editingUser.fechaExpiracionSuscripcion ? editingUser.fechaExpiracionSuscripcion.substring(0, 10) : ''}
                    onChange={(e) => setEditingUser({ ...editingUser, fechaExpiracionSuscripcion: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Nueva Contraseña (para resetear)</label>
                  <input
                    type="password"
                    placeholder="Nueva contraseña"
                    value={editingUser.newPassword || ''}
                    onChange={(e) => setEditingUser({ ...editingUser, newPassword: e.target.value })}
                  />
                  <small>Dejar vacío para mantener la contraseña actual</small>
                </div>
                <div className="form-actions">
                  <button type="submit" className="btn btn-primary">Guardar</button>
                  <button type="button" onClick={() => setEditingUser(null)} className="btn btn-secondary">
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {creatingUser && (
          <div className="modal-overlay">
            <div className="modal">
              <h2>Crear Nuevo Usuario</h2>
              <form onSubmit={handleCreate} className="auth-form">
                <div className="form-group">
                  <label>Nombre</label>
                  <input
                    type="text"
                    value={newUser.name}
                    onChange={(e) => setNewUser({ ...newUser, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    value={newUser.email}
                    onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Contraseña</label>
                  <input
                    type="password"
                    value={newUser.passwordHash}
                    onChange={(e) => setNewUser({ ...newUser, passwordHash: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Rol</label>
                  <select
                    value={newUser.rol}
                    onChange={(e) => setNewUser({ ...newUser, rol: e.target.value })}
                  >
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                </div>
                <div className="form-group">
                  <label>
                    <input
                      type="checkbox"
                      checked={newUser.suscripcionActiva}
                      onChange={(e) => setNewUser({ ...newUser, suscripcionActiva: e.target.checked })}
                    />
                    Premium
                  </label>
                </div>
                {newUser.suscripcionActiva && (
                  <>
                    <div className="form-group">
                      <label>Fecha Inicio Suscripción</label>
                      <input
                        type="date"
                        value={newUser.fechaSuscripcion}
                        onChange={(e) => setNewUser({ ...newUser, fechaSuscripcion: e.target.value })}
                      />
                    </div>
                    <div className="form-group">
                      <label>Fecha Expiración Suscripción</label>
                      <input
                        type="date"
                        value={newUser.fechaExpiracionSuscripcion}
                        onChange={(e) => setNewUser({ ...newUser, fechaExpiracionSuscripcion: e.target.value })}
                      />
                    </div>
                  </>
                )}
                <div className="form-actions">
                  <button type="submit" className="btn btn-primary">Crear</button>
                  <button type="button" onClick={() => setCreatingUser(false)} className="btn btn-secondary">
                    Cancelar
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};