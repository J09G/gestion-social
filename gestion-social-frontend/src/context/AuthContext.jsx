import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import client from '../api/client';

const ContextoAutenticacion = createContext(null);

// Backend responde: { token, nombre, rol }
// Backend recibe: { correo, contrasena }

export function ProveedorAutenticacion({ children }) {
  const [usuario, setUsuario] = useState(null);
  const [token, setToken] = useState(null);
  const [cargando, setCargando] = useState(true); // true mientras se restaura sesión

  // Al montar: restaurar sesión desde localStorage
  useEffect(() => {
    const tokenGuardado = localStorage.getItem('bg_token');
    const usuarioGuardado = localStorage.getItem('bg_user');
    if (tokenGuardado && usuarioGuardado) {
      try {
        setUsuario(JSON.parse(usuarioGuardado));
        setToken(tokenGuardado);
      } catch {
        localStorage.removeItem('bg_token');
        localStorage.removeItem('bg_user');
      }
    }
    setCargando(false);
  }, []);

  // iniciarSesion: envía { correo, contrasena } al backend
  const iniciarSesion = useCallback(async (correo, contrasena) => {
    const { data } = await client.post('/auth/iniciarSesion', { correo, contrasena });
    // data = { token, nombre, rol }
    const datosUsuario = { nombre: data.nombre, rol: data.rol };
    localStorage.setItem('bg_token', data.token);
    localStorage.setItem('bg_user', JSON.stringify(datosUsuario));
    setToken(data.token);
    setUsuario(datosUsuario);
  }, []);

  const cerrarSesion = useCallback(() => {
    localStorage.removeItem('bg_token');
    localStorage.removeItem('bg_user');
    setToken(null);
    setUsuario(null);
  }, []);

  const esAdmin = usuario?.rol === 'ADMIN';

  return (
    <ContextoAutenticacion.Provider value={{ usuario, token, cargando, iniciarSesion, cerrarSesion, esAdmin }}>
      {children}
    </ContextoAutenticacion.Provider>
  );
}

export function useAutenticacion() {
  const ctx = useContext(ContextoAutenticacion);
  if (!ctx) throw new Error('useAutenticacion debe usarse dentro de ProveedorAutenticacion');
  return ctx;
}
