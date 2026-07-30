import '../styles/layout.css'
import { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAutenticacion } from '../context/AuthContext'

const nav = [
  { to: '/',              label: 'Inicio',        end: true },
  { to: '/inventario',   label: 'Inventario' },
  { to: '/movimientos',  label: 'Movimientos' },
  { to: '/historial',    label: 'Historial' },
  { to: '/reportes',     label: 'Reportes' },
  { to: '/categorias',   label: 'CategorÃ­as' },
  { to: '/productos',    label: 'Productos',      admin: true },
  { to: '/usuarios',     label: 'Usuarios',       admin: true },
]

export default function Layout() {
  const { usuario, cerrarSesion, esAdmin } = useAutenticacion()
  const navigate = useNavigate()
  const [menuAbierto, setMenuAbierto] = useState(false)

  const manejarCierre = () => {
    cerrarSesion()
    navigate('/login')
  }

  const iniciales = usuario?.nombre
    ?.split(' ')
    .map((n) => n[0])
    .slice(0, 2)
    .join('')
    .toUpperCase() || '??'

  return (
    <div className="app-shell">
      {/* ---- Navbar superior ---- */}
      <header className="appbar">
        <div className="appbar-inner">
          {/* Brand */}
          <Link to="/" className="brand" onClick={() => setMenuAbierto(false)}>
            <div className="brand-mark">B</div>
            <span>Bodega Social</span>
            <span className="brand-sub">AlcaldÃ­a Municipal</span>
          </Link>

          {/* Hamburger (mobile) */}
          <button
            className={`hamburger ${menuAbierto ? 'is-open' : ''}`}
            onClick={() => setMenuAbierto((o) => !o)}
            aria-label="MenÃº"
          >
            <span />
            <span />
            <span />
          </button>

          {/* Nav links (desktop) */}
          <nav className="nav" id="mainNav">
            {nav.map((item) => {
              // Si el item requiere admin y no lo es, no renderizar
              if (item.admin && !esAdmin) return null
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.end}
                  className={({ isActive }) => isActive ? 'active' : ''}
                >
                  {item.label}
                </NavLink>
              )
            })}
          </nav>

          {/* usuario chip (desktop) */}
          <div className="user-chip desktop-only">
            <div className="avatar">{iniciales}</div>
            <span className="user-name">{usuario?.nombre}</span>
            <span className={`role badge ${esAdmin ? 'badge-role-admin' : 'badge-role-bodeguero'}`}>
              {usuario?.rol}
            </span>
            <button className="logout-btn" onClick={manejarCierre} title="Cerrar sesiÃ³n">
              Salir
            </button>
          </div>
        </div>

        {/* Mobile menu (overlay) */}
        <div className={`mobile-menu ${menuAbierto ? 'is-open' : ''}`}>
          <nav className="mobile-nav">
            {nav.map((item) => {
              if (item.admin && !esAdmin) return null
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.end}
                  onClick={() => setMenuAbierto(false)}
                  className={({ isActive }) => isActive ? 'active' : ''}
                >
                  {item.label}
                </NavLink>
              )
            })}
          </nav>
          <div className="mobile-user">
            <div className="avatar">{iniciales}</div>
            <span>{usuario?.nombre}</span>
            <span className={`badge ${esAdmin ? 'badge-role-admin' : 'badge-role-bodeguero'}`}>
              {usuario?.rol}
            </span>
            <button className="logout-btn full-width" onClick={manejarCierre}>
              Cerrar sesiÃ³n
            </button>
          </div>
        </div>
      </header>

      {/* ---- Contenido principal ---- */}
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  )
}