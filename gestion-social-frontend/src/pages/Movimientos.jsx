import { useState, useEffect } from 'react';
import client from '../api/client';
import '../styles/movimientos.css';

export default function Movimientos() {
  const [productos, setProductos] = useState([]);
  const [movimientos, setMovimientos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);

  const TAMANIO_PAGINA = 20;

  // Filtros tabla
  const [filtroTipo, setFiltroTipo] = useState('');
  const [filtroProducto, setFiltroProducto] = useState('');

  // Formulario
  const [form, setForm] = useState({ idProducto: '', tipo: 'ENTRADA', cantidad: '' });
  const [enviando, setEnviando] = useState(false);
  const [mensajeExito, setMensajeExito] = useState('');

  const cargarProductos = async () => {
    try {
      const { data } = await client.get('/products');
      setProductos(data.data || []);
    } catch { /* no crítico */ }
  };

  const cargarMovimientos = async () => {
    try {
      setCargando(true);
      setError('');
      const params = { page: pagina, size: TAMANIO_PAGINA };
      if (filtroTipo) params.tipo = filtroTipo;
      if (filtroProducto) params.idProducto = filtroProducto;
      const { data } = await client.get('/movements', { params });
      const payload = data.data || {};
      setMovimientos(payload.items || []);
      setTotalPaginas(payload.totalPaginas || 0);
    } catch {
      setError('Error al cargar movimientos.');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => { cargarProductos(); }, []);
  useEffect(() => { cargarMovimientos(); }, [filtroTipo, filtroProducto, pagina]);

  const manejarEnvio = async (e) => {
    e.preventDefault();
    if (!form.idProducto || !form.cantidad || form.cantidad < 1) {
      alert('Selecciona un producto y una cantidad válida.');
      return;
    }
    try {
      setEnviando(true);
      await client.post('/movements', {
        idProducto: form.idProducto,
        tipo: form.tipo,
        cantidad: parseInt(form.cantidad),
      });
      setForm({ idProducto: '', tipo: 'ENTRADA', cantidad: '' });
      setMensajeExito('Movimiento registrado exitosamente.');
      setTimeout(() => setMensajeExito(''), 3000);
      cargarMovimientos();
    } catch {
      alert('Error al registrar el movimiento.');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div>
      <h1 className="page-title">Movimientos</h1>

      {/* Formulario */}
      <div className="card" style={{ padding: 20, marginBottom: 24 }}>
        <h2 style={{ fontFamily: 'var(--display)', fontSize: 18, fontWeight: 400, marginBottom: 16 }}>Registrar entrada o salida</h2>

        {mensajeExito && <div className="alert-banner ok"><span>{mensajeExito}</span></div>}

        <form onSubmit={manejarEnvio} style={{ display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div className="field" style={{ marginBottom: 0, flex: '1 1 200px' }}>
            <label className="label">Producto <span className="req">*</span></label>
            <select className="select" value={form.idProducto} onChange={(e) => setForm({ ...form, idProducto: e.target.value })} required>
              <option value="">Seleccionar producto…</option>
              {productos.filter(p => p.activo).map((p) => (
                <option key={p.id} value={p.id}>{p.nombre} (Stock: {p.stockActual})</option>
              ))}
            </select>
          </div>
          <div className="field" style={{ marginBottom: 0, flex: '0 0 130px' }}>
            <label className="label">Tipo <span className="req">*</span></label>
            <select className="select" value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })}>
              <option value="ENTRADA">Entrada</option>
              <option value="SALIDA">Salida</option>
            </select>
          </div>
          <div className="field" style={{ marginBottom: 0, flex: '0 0 110px' }}>
            <label className="label">Cantidad <span className="req">*</span></label>
            <input className="input" type="number" min="1" value={form.cantidad} onChange={(e) => setForm({ ...form, cantidad: e.target.value })} required />
          </div>
          <button className="btn btn-primary" type="submit" disabled={enviando}>
            {enviando ? 'Registrando…' : 'Registrar'}
          </button>
        </form>
      </div>

      {/* Filtros tabla */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 16, flexWrap: 'wrap' }}>
        <div className="field" style={{ marginBottom: 0, flex: '0 0 150px' }}>
          <label className="label">Filtrar tipo</label>
          <select className="select" value={filtroTipo} onChange={(e) => setFiltroTipo(e.target.value)}>
            <option value="">Todos</option>
            <option value="ENTRADA">Entradas</option>
            <option value="SALIDA">Salidas</option>
          </select>
        </div>
        <div className="field" style={{ marginBottom: 0, flex: '0 0 220px' }}>
          <label className="label">Filtrar producto</label>
          <select className="select" value={filtroProducto} onChange={(e) => setFiltroProducto(e.target.value)}>
            <option value="">Todos</option>
            {productos.map((p) => <option key={p.id} value={p.id}>{p.nombre}</option>)}
          </select>
        </div>
      </div>

      {error && <div className="alert-banner critical"><span>{error}</span></div>}

      {/* Tabla */}
      {cargando ? (
        <p style={{ color: 'var(--ink-500)' }}>Cargando movimientos…</p>
      ) : movimientos.length === 0 ? (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: 'var(--ink-500)' }}>
          No hay movimientos registrados.
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
