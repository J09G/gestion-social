import '../styles/login.css'
import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAutenticacion } from '../context/AuthContext'

export default function Login() {
  const { iniciarSesion } = useAutenticacion()
  const navigate = useNavigate()
  const location = useLocation()

  const [correo, setCorreo] = useState('')
  const [contrasena, setContrasena] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  const manejarEnvio = async (e) => {
    e.preventDefault()
    setError('')
    setCargando(true)
    try {
      await iniciarSesion(correo, contrasena)
      const from = location.state?.from?.pathname || '/'
      navigate(from, { replace: true })
    } catch (err) {
      const msg =
        err.response?.data?.mensaje ||
        err.response?.data?.message ||
        'Credenciales incorrectas. Verifica e intenta de nuevo.'
      setError(msg)
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="login-shell" data-screen-label="login">
      <aside className="login-aside">
        <div className="login-aside-brand">
          <div className="brand-mark">B</div>
          <div className="login-aside-brand-name">
            Bodega <em style={{ fontStyle: 'italic', color: 'var(--sand-500)' }}>Social</em>
          </div>
        </div>

        <div>
          <h1 className="login-headline">
            Donaciones que <em>encuentran</em> su destino.
          </h1>
          <p className="login-tagline">
            Sistema de gestión de inventario para la bodega social de la alcaldía.
            Trazabilidad de cada producto, desde su llegada hasta la familia que lo recibe.
          </p>
        </div>

        <div className="login-meta">
          <div>
            <div>Versión</div>
            <b>v1.0.4 · 2026</b>
          </div>
          <div>
            <div>Entorno</div>
            <b>Producción</b>
          </div>
          <div>
            <div>Soporte</div>
            <b>ext. 2204</b>
          </div>
        </div>
      </aside>

      <main className="login-form-wrap">
        <form className="login-form" onSubmit={manejarEnvio} autoComplete="on">
          <p className="page-eyebrow" style={{ marginBottom: 6 }}>
            Acceso restringido
          </p>
          <h2>Bienvenido de vuelta</h2>
          <p className="login-sub">
            Inicia sesión con tu cuenta institucional para gestionar el inventario de la bodega.
          </p>

          {error && (
            <div className="login-error">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="10" />
                <line x1="12" y1="8" x2="12" y2="12" />
                <line x1="12" y1="16" x2="12.01" y2="16" />
              </svg>
              <span>{error}</span>
            </div>
          )}

          <div className="field">
            <label className="label" htmlFor="email">
              Correo institucional <span className="req">*</span>
            </label>
            <input
              className="input"
              id="email"
              type="email"
              placeholder="usuario@alcaldia.gov"
              value={correo}
              onChange={(e) => setCorreo(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className="field">
            <label className="label" htmlFor="password">
              Contraseña <span className="req">*</span>
            </label>
            <input
              className="input"
              id="password"
              type="password"
              placeholder="••••••••"
              value={contrasena}
              onChange={(e) => setContrasena(e.target.value)}
              required
            />
          </div>

          <button className="btn btn-primary" type="submit" disabled={cargando}>
            {cargando ? 'Ingresando…' : 'Iniciar sesión'}
            <svg className="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="5" y1="12" x2="19" y2="12" />
              <polyline points="12 5 19 12 12 19" />
            </svg>
          </button>

          <div className="login-foot">
            Sesión segura · token JWT · expira a las 8 h
            <br />
            ¿Problemas para acceder? Contacta al administrador.
          </div>
        </form>
      </main>
    </div>
  )
}