import { useState, useEffect } from 'react';
import client from '../api/client';

export default function Historial() {
  const [movimientos, setMovimientos] = useState([]);
  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  const TAMANIO_PAGINA = 20;

  // Filtros
  const [filtroDesde, setFiltroDesde] = useState('');
  const [filtroHasta, setFiltroHasta] = useState('');
  const [filtroTipo, setFiltroTipo] = useState('');
  const [filtroProducto, setFiltroProducto] = useState('');
  const [busqueda, setBusqueda] = useState(false);

  const cargarMovimientos = async () => {
    try {
      setCargando(true);
      setError('');
      const params = { page: pagina, size: TAMANIO_PAGINA };
      if (filtroDesde) params.desde = filtroDesde + 'T00:00:00';
      if (filtroHasta) params.hasta = filtroHasta + 'T23:59:59';
      if (filtroTipo) params.tipo = filtroTipo;
      if (filtroProducto) params.idProducto = filtroProducto;
      const { data } = await client.get('/movements', { params });
      const payload = data.data || {};
      setMovimientos(payload.items || []);
      setTotalPaginas(payload.totalPaginas || 0);
    } catch {
      setError('Error al cargar el historial.');
    } finally {
      setCargando(false);
    }
  };

  const cargarProductos = async () => {
    try {
      const { data } = await client.get('/products');
      setProductos(data.data || []);
    } catch { /* no crítico */ }
  };

  useEffect(() => { cargarProductos(); }, []);
  useEffect(() => { cargarMovimientos(); }, [filtroTipo, filtroProducto, pagina, busqueda]);

  const manejarBuscar = (e) => {
    e.preventDefault();
    setPagina(0);
    setBusqueda((prev) => !prev);
  };

  const limpiarFiltros = () => {
    setFiltroDesde('');
    setFiltroHasta('');
    setFiltroTipo('');
    setFiltroProducto('');
    setPagina(0);
    setBusqueda((prev) => !prev);
  };

  return (
    <div>
      <h1 className="page-title">Historial de movimientos</h1>

      <form onSubmit={manejarBuscar} style={{ display: 'flex', gap: 10, marginBottom: 20, flexWrap: 'wrap', alignItems: 'flex-end' }}>
        <div className="field" style={{ marginBottom: 0, flex: '0 0 145px' }}>
          <label className="label">Desde</label>
          <input className="input" type="date" value={filtroDesde} onChange={(e) => setFiltroDesde(e.target.value)} />
        </div>
        <div className="field" style={{ marginBottom: 0, flex: '0 0 145px' }}>
          <label className="label">Hasta</label>
          <input className="input" type="date" value={filtroHasta} onChange={(e) => setFiltroHasta(e.target.value)} />
        </div>
        <div className="field" style={{ marginBottom: 0, flex: '0 0 130px' }}>
          <label className="label">Tipo</label>
          <select className="select" value={filtroTipo} onChange={(e) => setFiltroTipo(e.target.value)}>
            <option value="">Todos</option>
            <option value="ENTRADA">Entradas</option>
            <option value="SALIDA">Salidas</option>
          </select>
        </div>
        <div className="field" style={{ marginBottom: 0, flex: '1 1 200px' }}>
          <label className="label">Producto</label>
          <select className="select" value={filtroProducto} onChange={(e) => setFiltroProducto(e.target.value)}>
            <option value="">Todos</option>
            {productos.map((p) => <option key={p.id} value={p.id}>{p.nombre}</option>)}
          </select>
        </div>
        <button className="btn btn-primary" type="submit">Buscar</button>
        <button className="btn btn-ghost" type="button" onClick={limpiarFiltros}>Limpiar</button>
      </form>

      {error && <div className="alert-banner critical"><span>{error}</span></div>}

      {cargando ? (
        <p style={{ color: 'var(--ink-500)' }}>Cargando historial…</p>
      ) : movimientos.length === 0 ? (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: 'var(--ink-500)' }}>
          No se encontraron movimientos con esos filtros.
        </div>
      ) : (
        <div className="card" style={{ overflow: 'auto' }}>
          <table className="data">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Producto</th>
                <th>Categoría</th>
                <th>Tipo</th>
                <th>Cantidad</th>
                <th>Usuario</th>
              </tr>
            </thead>
            <tbody>
              {movimientos.map((mov) => (
                <tr key={mov.id}>
                  <td style={{ whiteSpace: 'nowrap' }}>
                    {new Date(mov.fechaHora).toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' })}
                  </td>
                  <td className="name">{mov.nombreProducto}</td>
                  <td><span className="badge badge-cat">{mov.nombreCategoria || '—'}</span></td>
                  <td>
                    <span className={`badge ${mov.tipo === 'ENTRADA' ? 'badge-entrada' : 'badge-salida'}`}>
                      {mov.tipo}
                    </span>
                  </td>
                  <td className="num">{mov.cantidad}</td>
                  <td>{mov.nombreUsuario}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {totalPaginas > 1 && (
        <div style={{ display: 'flex', gap: 8, justifyContent: 'center', marginTop: 16, alignItems: 'center' }}>
          <button className="btn btn-ghost" onClick={() => setPagina(Math.max(0, pagina - 1))} disabled={pagina === 0 || cargando}>
            Anterior
          </button>
          <span style={{ color: 'var(--ink-500)' }}>Página {pagina + 1} de {totalPaginas}</span>
          <button className="btn btn-ghost" onClick={() => setPagina(Math.min(totalPaginas - 1, pagina + 1))} disabled={pagina >= totalPaginas - 1 || cargando}>
            Siguiente
          </button>
        </div>
      )}
    </div>
  );
}
