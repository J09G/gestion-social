import { Navigate, useLocation } from 'react-router-dom'
import { useAutenticacion } from '../context/AuthContext'

export default function PrivateRoute({ children, role }) {
  const { usuario, token, cargando } = useAutenticacion()
  const location = useLocation()

  // Mientras restaura sesión desde localStorage
  if (cargando) {
    return (
      <div className="page-loader">
        <div className="spinner" />
        <p>Restaurando sesión…</p>
      </div>
    )
  }

  // No autenticado → redirigir al login
  if (!token || !usuario) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  // Rol requerido y no lo tiene → redirigir al dashboard
  if (role && usuario.rol !== role) {
    return <Navigate to="/" replace />
  }

  return children
}