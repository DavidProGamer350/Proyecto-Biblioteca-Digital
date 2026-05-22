import { createContext, useState, useEffect } from 'react';
import { AuthService } from '../services/AuthService';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const storedUser = AuthService.getUser();
    if (storedUser && AuthService.isAuthenticated()) {
      setUser(storedUser);
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await AuthService.login({ email, password });
    AuthService.saveToken(response.token);
    AuthService.saveUser({
      id: response.id,
      name: response.name,
      email: response.email,
      rol: response.rol,
    });
    setUser({
      id: response.id,
      name: response.name,
      email: response.email,
      rol: response.rol,
    });
    
    // Verificar si hay un préstamo pendiente después del login
    const pendingLoan = localStorage.getItem('pendingLoan');
    if (pendingLoan) {
      localStorage.removeItem('pendingLoan');
      const loanData = JSON.parse(pendingLoan);
      // Crear el préstamo
      try {
        await LoanService.create(response.id, loanData.libroId);
        alert(`Préstamo de "${loanData.titulo}" creado exitosamente. Tienes 14 días.`);
        window.location.href = '/mis-prestamos';
      } catch (err) {
        alert('Error al crear préstamo: ' + err.message);
        window.location.href = '/catalogo';
      }
    } else {
      window.location.href = '/catalogo';
    }
    
    return response;
  };

  const register = async (name, email, password) => {
    const response = await AuthService.register({ name, email, password });
    AuthService.saveToken(response.token);
    AuthService.saveUser({
      id: response.id,
      name: response.name,
      email: response.email,
      rol: response.rol,
    });
    setUser({
      id: response.id,
      name: response.name,
      email: response.email,
      rol: response.rol,
    });
    return response;
  };

  const logout = () => {
    AuthService.removeToken();
    AuthService.removeUser();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};