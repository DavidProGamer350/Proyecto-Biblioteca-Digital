import { useState, useContext, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { UserService } from '../services/UserService';

export const ProfilePage = () => {
  const { user: currentUser, logout } = useContext(AuthContext);
  const navigate = useNavigate();
  
  const [userData, setUserData] = useState({
    name: '',
    email: '',
    password: ''
  });
  const [isPremium, setIsPremium] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [editing, setEditing] = useState(false);
  const [subscriptionDates, setSubscriptionDates] = useState({ inicio: null, expiracion: null });

  useEffect(() => {
    if (currentUser) {
      setUserData({
        name: currentUser.name || '',
        email: currentUser.email || ''
      });
      loadPremiumStatus();
    }
  }, [currentUser]);

  const loadPremiumStatus = async () => {
    try {
      const premium = await UserService.isPremium(currentUser?.id);
      setIsPremium(premium);
      
      // Obtener datos completos del usuario para las fechas
      const userComplete = await UserService.getById(currentUser?.id);
      if (userComplete && premium) {
        setSubscriptionDates({
          inicio: userComplete.fechaSuscripcion,
          expiracion: userComplete.fechaExpiracionSuscripcion
        });
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
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

  const handleUpdate = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    try {
      const updateData = {
        name: userData.name,
        email: userData.email
      };
      
      if (userData.password && userData.password.length > 0) {
        if (userData.password.length < 6) {
          setError('La contraseña debe tener al menos 6 caracteres');
          return;
        }
      }
      
      await UserService.update(currentUser.id, updateData);
      
      const updatedUser = {
        ...currentUser,
        name: userData.name,
        email: userData.email
      };
      localStorage.setItem('user', JSON.stringify(updatedUser));
      
      setSuccess('Perfil actualizado correctamente');
      setEditing(false);
      setUserData({ ...userData, password: '' });
    } catch (err) {
      setError(err.message);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (loading) {
    return <div className="container">Cargando...</div>;
  }

  return (
    <div>
      <nav className="navbar">
        <Link to="/dashboard" className="navbar-brand">Biblioteca Digital</Link>
        <div className="navbar-menu">
          <Link to="/dashboard" className="navbar-link">Panel</Link>
          <Link to="/books" className="navbar-link">Libros</Link>
          <span className="navbar-user">{currentUser?.name}</span>
          <button onClick={handleLogout} className="btn btn-secondary" style={{ padding: '5px 10px' }}>
            Cerrar Sesión
          </button>
        </div>
      </nav>

      <div className="profile-container">
        <div className="profile-card">
          <h1 className="profile-title">Mi Perfil</h1>
          
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}
          
          <div className="profile-info">
            <div className="info-row">
              <span className="info-label">Rol:</span>
              <span className="info-value">{currentUser?.rol}</span>
            </div>
            <div className="info-row">
              <span className="info-label">Suscripción:</span>
              <span className={`info-value ${isPremium ? 'premium' : 'gratis'}`}>
                {isPremium ? 'Premium' : 'Gratis'}
              </span>
            </div>
            {isPremium && (
              <>
                <div className="info-row">
                  <span className="info-label">Inicio:</span>
                  <span className="info-value">{formatDate(subscriptionDates.inicio)}</span>
                </div>
                <div className="info-row">
                  <span className="info-label">Expira:</span>
                  <span className="info-value">{formatDate(subscriptionDates.expiracion)}</span>
                </div>
              </>
            )}
          </div>
          
          <form onSubmit={handleUpdate} className="profile-form">
            <div className="form-group">
              <label>Nombre</label>
              <input
                type="text"
                value={userData.name}
                onChange={(e) => {
                  setUserData({ ...userData, name: e.target.value });
                  setEditing(true);
                }}
                disabled={false}
              />
            </div>
            
            <div className="form-group">
              <label>Email</label>
              <input
                type="email"
                value={userData.email}
                onChange={(e) => {
                  setUserData({ ...userData, email: e.target.value });
                  setEditing(true);
                }}
                disabled={false}
              />
            </div>
            
            <div className="form-group">
              <label>Nueva Contraseña (opcional)</label>
              <input
                type="password"
                placeholder="Nueva contraseña"
                value={userData.password}
                onChange={(e) => {
                  setUserData({ ...userData, password: e.target.value });
                  setEditing(true);
                }}
              />
              <small>Dejar vacío para mantener la contraseña actual</small>
            </div>
            
            {editing && (
              <div className="form-actions">
                <button type="submit" className="btn btn-primary">
                  Guardar Cambios
                </button>
                <button type="button" onClick={() => {
                  setEditing(false);
                  setUserData({ name: currentUser?.name, email: currentUser?.email, password: '' });
                }} className="btn btn-secondary">
                  Cancelar
                </button>
              </div>
            )}
          </form>
          
          <div className="profile-actions">
            <button onClick={handleLogout} className="btn btn-secondary btn-logout">
              Cerrar Sesión
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};