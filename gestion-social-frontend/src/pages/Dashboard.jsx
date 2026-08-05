import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import client from '../api/client';
import '../styles/dashboard.css';

export default function Dashboard() {
  const [alertas, setAlertas] = useState([]);
  const [totales, setTotales] = useState({ productos: 0, categorias: 0, movimientos: 0 });
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const obtenerDatos = async () => {
      try {
        const [resAlertas, resProductos, resCategorias, resMovimientos] = await Promise.all([
          client.get('/products/alerts'),
          client.get('/products'),
          client.get('/categories'),
          client.get('/movements'),
        ]);

        setAlertas(resAlertas.data.data || []);
        setTotales({
          productos: (resProductos.data.data || []).length,
          categorias: (resCategorias.data.data || []).length,
          movimientos: (resMovimientos.data.data || []).length,
        });
      } catch {
        setError('No se pudieron cargar los datos del dashboard.');
      } finally {
        setCargando(false);
      }
    };
    obtenerDatos();
  }, []);

  return (
    <div>
      <h1 className="page-title">Panel general</h1>

      {cargando && <p style={{ color: 'var(--ink-500)' }}>Cargando datos…</p>}
      {error && (
        <div className="alert-banner critical">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10" />
            <line x1="12" y1="8" x2="12" y2="12" />
            <line x1="12" y1="16" x2="12.01" y2="16" />
          </svg>
          <span>{error}</span>
        </div>
      )}

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-label">Productos en inventario</div>
          <div className="stat-value">{cargando ? '…' : totales.productos}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Categorías registradas</div>
          <div className="stat-value">{cargando ? '…' : totales.categorias}</div>
        </div>
        <div className="stat-card">
          <div className="stat-label">Movimientos totales</div>
          <div className="stat-value">{cargando ? '…' : totales.movimientos}</div>
        </div>
      </div>

      {alertas.length > 0 && (
        <>
          <h2 style={{ fontFamily: 'var(--display)', fontSize: 20, marginBottom: 12, fontWeight: 400 }}>
            ⚠️ Productos bajo stock mínimo
          </h2>
          {alertas.map((prod) => (
            <div key={prod.id} className="alert-banner critical">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z" />
                <line x1="12" y1="9" x2="12" y2="13" />
                <line x1="12" y1="17" x2="12.01" y2="17" />
              </svg>
              <span>
                <strong>{prod.nombre}</strong> — Stock actual: {prod.stockActual}{' '}
                (mínimo: {prod.umbralCritico ?? 'N/A'})
              </span>
            </div>
          ))}
        </>
      )}

      <div className="quick-tiles" style={{ marginTop: 24 }}>
        <Link to="/inventario" className="quick-tile">
          <div className="tile-icon">📦</div>
          <div className="tile-label">Inventario</div>
        </Link>
        <Link to="/movimientos" className="quick-tile">
          <div className="tile-icon">↗️</div>
          <div className="tile-label">Movimientos</div>
        </Link>
        <Link to="/historial" className="quick-tile">
          <div className="tile-icon">📋</div>
          <div className="tile-label">Historial</div>
        </Link>
        <Link to="/reportes" className="quick-tile">
          <div className="tile-icon">📊</div>
          <div className="tile-label">Reportes</div>
        </Link>
      </div>
    </div>
  );
}
