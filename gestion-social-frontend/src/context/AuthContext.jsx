import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import client from '../api/client';

const AuthContext = createContext(null);

// Backend responde: { token, nombre, rol }
// Backend recibe: { correo, contrasena }

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setTokenState] = useState(null);
  const [loading, setLoading] = useState(true); // true mientras se restaura sesión

  // Al montar: restaurar sesión desde localStorage
  useEffect(() => {
    const savedToken = localStorage.getItem('bg_token');
    const savedUser = localStorage.getItem('bg_user');
    if (savedToken && savedUser) {
      try {
        setUser(JSON.parse(savedUser));
        setTokenState(savedToken);
      } catch {
        localStorage.removeItem('bg_token');
        localStorage.removeItem('bg_user');
      }
    }
    setLoading(false);
  }, []);

  // Login: envía { correo, contrasena } al backend
  const login = useCallback(async (correo, contrasena) => {
    const { data } = await client.post('/auth/login', { correo, contrasena });
    // data = { token, nombre, rol }
    const userData = { nombre: data.nombre, rol: data.rol };
    localStorage.setItem('bg_token', data.token);
    localStorage.setItem('bg_user', JSON.stringify(userData));
    setTokenState(data.token);
    setUser(userData);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('bg_token');
    localStorage.removeItem('bg_user');
    setTokenState(null);
    setUser(null);
  }, []);

  const isAdmin = user?.rol === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, token, loading, login, logout, isAdmin }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider');
  return ctx;
}
