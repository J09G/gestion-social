import '../styles/layout.css'
import { useState } from 'react'
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

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
  const { user, logout, isAdmin } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const initials = user?.nombre
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
          <Link to="/" className="brand" onClick={() => setMenuOpen(false)}>
            <div className="brand-mark">B</div>
            <span>Bodega Social</span>
            <span className="brand-sub">AlcaldÃ­a Municipal</span>
          </Link>

          {/* Hamburger (mobile) */}
          <button
            className={`hamburger ${menuOpen ? 'is-open' : ''}`}
            onClick={() => setMenuOpen((o) => !o)}
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
              if (item.admin && !isAdmin) return null
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

          {/* User chip (desktop) */}
          <div className="user-chip desktop-only">
            <div className="avatar">{initials}</div>
            <span className="user-name">{user?.nombre}</span>
            <span className={`role badge ${isAdmin ? 'badge-role-admin' : 'badge-role-bodeguero'}`}>
              {user?.rol}
            </span>
            <button className="logout-btn" onClick={handleLogout} title="Cerrar sesiÃ³n">
              Salir
            </button>
          </div>
        </div>

        {/* Mobile menu (overlay) */}
        <div className={`mobile-menu ${menuOpen ? 'is-open' : ''}`}>
          <nav className="mobile-nav">
            {nav.map((item) => {
              if (item.admin && !isAdmin) return null
              return (
                <NavLink
                  key={item.to}
                  to={item.to}
                  end={item.end}
                  onClick={() => setMenuOpen(false)}
                  className={({ isActive }) => isActive ? 'active' : ''}
                >
                  {item.label}
                </NavLink>
              )
            })}
          </nav>
          <div className="mobile-user">
            <div className="avatar">{initials}</div>
            <span>{user?.nombre}</span>
            <span className={`badge ${isAdmin ? 'badge-role-admin' : 'badge-role-bodeguero'}`}>
              {user?.rol}
            </span>
            <button className="logout-btn full-width" onClick={handleLogout}>
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