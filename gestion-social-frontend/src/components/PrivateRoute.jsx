import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function PrivateRoute({ children, role }) {
  const { user, token, loading } = useAuth()
  const location = useLocation()

  // Mientras restaura sesión desde localStorage
  if (loading) {
    return (
      <div className="page-loader">
        <div className="spinner" />
        <p>Restaurando sesión…</p>
      </div>
    )
  }

  // No autenticado → redirigir al login
  if (!token || !user) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  // Rol requerido y no lo tiene → redirigir al dashboard
  if (role && user.rol !== role) {
    return <Navigate to="/" replace />
  }

  return children
}