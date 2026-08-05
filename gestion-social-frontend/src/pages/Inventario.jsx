import { useState, useEffect } from 'react';
import { useAutenticacion } from '../context/AuthContext';
import client from '../api/client';
 
export default function Inventario() {
  const { esAdmin } = useAutenticacion();
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const [filtroNombre, setFiltroNombre] = useState('');
  const [filtroCategoria, setFiltroCategoria] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');

  const cargarProductos = async () => {
    try {
      setCargando(true);
      const params = {};
      if (filtroNombre) params.nombre = filtroNombre;
      if (filtroCategoria) params.idCategoria = filtroCategoria;
      if (filtroEstado) params.estado = filtroEstado;
      const { data } = await client.get('/products', { params });
      setProductos(data.data || []);
    } catch {
      setError('Error al cargar productos.');
    } finally {
      setCargando(false);
    }
  };

  const cargarCategorias = async () => {
    try {
      const { data } = await client.get('/categories');
      setCategorias(data.data || []);
    } catch { /* no crítico */ }
  };

  useEffect(() => { cargarCategorias(); }, []);
  useEffect(() => { cargarProductos(); }, [filtroCategoria, filtroEstado]);

  const manejarBusqueda = (e) => {
    e.preventDefault();
    cargarProductos();
  };

  const manejarDesactivar = async (id) => {
    if (!confirm('¿Desactivar este producto?')) return;
    try {
      await client.patch(`/products/${id}/deactivate`);
      cargarProductos();
    } catch {
      alert('Error al desactivar.');
    }
  };

  const estiloEstado = (estado) => {
    if (estado === 'CRITICO') return { background: 'var(--critical-100)', color: 'var(--critical-700)' };
    if (estado === 'BAJO') return { background: 'var(--warn-100)', color: 'var(--warn-600)' };
    return { background: 'var(--ok-100)', color: 'var(--ok-700)' };
  };

  return (
    <div>
      <h1 className="page-title">Inventario</h1>

      <form onSubmit={manejarBusqueda} style={{ display: 'flex', gap: 10, marginBottom: 20, flexWrap: 'wrap', alignItems: 'flex-end' }}>
        <div className="field" style={{ marginBottom: 0, flex: '1 1 200px' }}>
          <label className="label">Buscar por nombre</label>
          <input className="input" type="text" placeholder="Ej: Arroz" value={filtroNombre} onChange={(e) => setFiltroNombre(e.target.value)} />
        </div>
        <div className="field" style={{ marginBottom: 0, flex: '1 1 160px' }}>
          <label className="label">Categoría</label>
          <select className="select" value={filtroCategoria} onChange={(e) => setFiltroCategoria(e.target.value)}>
            <option value="">Todas</option>
            {categorias.map((cat) => <option key={cat.id} value={cat.id}>{cat.nombre}</option>)}
          </select>
        </div>
        <div className="field" style={{ marginBottom: 0, flex: '1 1 140px' }}>
          <label className="label">Estado</label>
          <select className="select" value={filtroEstado} onChange={(e) => setFiltroEstado(e.target.value)}>
            <option value="">Todos</option>
            <option value="OK">OK</option>
            <option value="BAJO">Bajo</option>
            <option value="CRITICO">Crítico</option>
          </select>
        </div>
        <button className="btn btn-primary" type="submit">Buscar</button>
      </form>

      {error && <div className="alert-banner critical"><span>{error}</span></div>}

      {cargando ? (
        <p style={{ color: 'var(--ink-500)' }}>Cargando productos…</p>
      ) : productos.length === 0 ? (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: 'var(--ink-500)' }}>
          No se encontraron productos.
        </div>
      ) : (
        <div className="card" style={{ overflow: 'auto' }}>
          <table className="data">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Categoría</th>
                <th>Talla</th>
                <th>Stock</th>
                <th>Mínimo</th>
                <th>Estado</th>
                {esAdmin && <th style={{ width: 80 }}></th>}
              </tr>
            </thead>
            <tbody>
              {productos.map((prod) => (
                <tr key={prod.id}>
                  <td className="name">{prod.nombre}</td>
                  <td><span className="badge badge-cat">{prod.nombreCategoria || '—'}</span></td>
                  <td>{prod.tieneTalla ? prod.talla : 'N/A'}</td>
                  <td className="num">{prod.stockActual}</td>
                  <td className="num">{prod.umbralCritico}</td>
                  <td><span className="badge" style={estiloEstado(prod.estado)}>{prod.estado}</span></td>
                  {esAdmin && (
                    <td>
                      {prod.activo ? (
                        <button className="btn btn-sm btn-ghost" onClick={() => manejarDesactivar(prod.id)} title="Desactivar">
                          ❌
                        </button>
                      ) : (
                        <span className="badge" style={{ background: 'var(--ink-300)', color: '#fff' }}>Inactivo</span>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
