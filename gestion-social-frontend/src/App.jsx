import { Routes, Route, Navigate } from 'react-router-dom'
import Plantilla from './components/Layout'
import RutaPrivada from './components/PrivateRoute'

// Páginas
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Inventario from './pages/Inventario'
import Movimientos from './pages/Movimientos'
import Historial from './pages/Historial'
import Reportes from './pages/Reportes'
import Categorias from './pages/Categorias'
import Productos from './pages/Productos'
import Usuarios from './pages/Usuarios'

export default function App() {
  return (
    <Routes>
      {/* Ruta pública */}
      <Route path="/login" element={<Login />} />

      {/* Rutas protegidas con Plantilla */}
      <Route
        element={
          <RutaPrivada>
            <Plantilla />
          </RutaPrivada>
        }
      >
        <Route index element={<Dashboard />} />
        <Route path="inventario" element={<Inventario />} />
        <Route path="movimientos" element={<Movimientos />} />
        <Route path="historial" element={<Historial />} />
        <Route path="reportes" element={<Reportes />} />
        <Route path="categorias" element={<Categorias />} />
        <Route path="productos" element={<RutaPrivada role="ADMIN"><Productos /></RutaPrivada>} />
        <Route path="usuarios" element={<RutaPrivada role="ADMIN"><Usuarios /></RutaPrivada>} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}