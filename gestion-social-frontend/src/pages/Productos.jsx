import { useState, useEffect, useRef } from 'react';
import client from '../api/client';

export default function Productos() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [editando, setEditando] = useState(null);
  const [mostrarForm, setMostrarForm] = useState(false);
  const [importando, setImportando] = useState(false);
  const fileInputRef = useRef(null);

  const formInicial = { nombre: '', idCategoria: '', tieneTalla: false, talla: '', stockActual: 0, umbralCritico: 0 };
  const [form, setForm] = useState(formInicial);

  const cargarProductos = async () => {
    try {
      setCargando(true);
      const { data } = await client.get('/products');
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

  useEffect(() => { cargarProductos(); cargarCategorias(); }, []);

  const abrirCrear = () => {
    setEditando(null);
    setForm(formInicial);
    setMostrarForm(true);
  };

  const abrirEditar = (prod) => {
    setEditando(prod.id);
    setForm({
      nombre: prod.nombre,
      idCategoria: prod.idCategoria || '',
      tieneTalla: prod.tieneTalla || false,
      talla: prod.talla || '',
      stockActual: prod.stockActual,
      umbralCritico: prod.umbralCritico,
    });
    setMostrarForm(true);
  };

  const cerrarForm = () => {
    setMostrarForm(false);
    setEditando(null);
    setForm(formInicial);
  };

  const manejarEnvio = async (e) => {
    e.preventDefault();
    if (!form.nombre.trim() || !form.idCategoria) return;
    try {
      setEnviando(true);
      const payload = {
        nombre: form.nombre.trim(),
        idCategoria: form.idCategoria,
        tieneTalla: form.tieneTalla,
        talla: form.tieneTalla ? form.talla : null,
        stockActual: form.stockActual,
        umbralCritico: form.umbralCritico,
      };
      if (editando) {
        await client.put(`/products/${editando}`, payload);
      } else {
        await client.post('/products', payload);
      }
      cerrarForm();
      cargarProductos();
    } catch {
      alert('Error al guardar el producto.');
    } finally {
      setEnviando(false);
    }
  };

  const manejarImportar = async (e) => {
    const archivo = e.target.files[0];
    if (!archivo) return;
    if (!archivo.name.endsWith('.xlsx')) {
      alert('Solo se permiten archivos .xlsx');
      return;
    }
    try {
      setImportando(true);
      const formData = new FormData();
      formData.append('file', archivo);
      const { data } = await client.post('/products/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      alert(data.message || 'Importación exitosa.');
      cargarProductos();
    } catch {
      alert('Error al importar el archivo.');
    } finally {
      setImportando(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
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
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 className="page-title" style={{ marginBottom: 0 }}>Productos</h1>
        <button className="btn btn-primary" onClick={abrirCrear}>+ Nuevo producto</button>
        <div style={{ display: 'flex', gap: 10 }}>
          <input
            type="file"
            accept=".xlsx"
            ref={fileInputRef}
            style={{ display: 'none' }}
            onChange={manejarImportar}
          />
          <button
            className="btn btn-ghost"
            onClick={() => fileInputRef.current?.click()}
            disabled={importando}
            title="Importar productos desde Excel (.xlsx)"
          >
            {importando ? 'Importando…' : '📥 Importar Excel'}
          </button>
        </div>
      </div>

      {mostrarForm && (
        <div className="card" style={{ padding: 20, marginBottom: 24, maxWidth: 600 }}>
          <h2 style={{ fontFamily: 'var(--display)', fontSize: 18, fontWeight: 400, marginBottom: 16 }}>
            {editando ? 'Editar producto' : 'Nuevo producto'}
          </h2>
          <form onSubmit={manejarEnvio} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              <div className="field" style={{ flex: '1 1 250px' }}>
                <label className="label">Nombre <span className="req">*</span></label>
                <input className="input" type="text" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
              </div>
              <div className="field" style={{ flex: '1 1 200px' }}>
                <label className="label">Categoría <span className="req">*</span></label>
                <select className="select" value={form.idCategoria} onChange={(e) => setForm({ ...form, idCategoria: e.target.value })} required>
                  <option value="">Seleccionar…</option>
                  {categorias.map((cat) => <option key={cat.id} value={cat.id}>{cat.nombre}</option>)}
                </select>
              </div>
            </div>
            <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
              <label className="label" style={{ display: 'flex', alignItems: 'center', gap: 6, cursor: 'pointer' }}>
                <input type="checkbox" checked={form.tieneTalla} onChange={(e) => setForm({ ...form, tieneTalla: e.target.checked })} />
                ¿Tiene talla?
              </label>
              {form.tieneTalla && (
                <div className="field" style={{ flex: '0 0 120px' }}>
                  <label className="label">Talla</label>
                  <input className="input" type="text" placeholder="Ej: M, 38, Única" value={form.talla} onChange={(e) => setForm({ ...form, talla: e.target.value })} />
                </div>
              )}
            </div>
            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
              <div className="field" style={{ flex: '0 0 120px' }}>
                <label className="label">Stock inicial</label>
                <input className="input" type="number" min="0" value={form.stockActual} onChange={(e) => setForm({ ...form, stockActual: parseInt(e.target.value) || 0 })} />
              </div>
              <div className="field" style={{ flex: '0 0 130px' }}>
                <label className="label">Umbral crítico</label>
                <input className="input" type="number" min="0" value={form.umbralCritico} onChange={(e) => setForm({ ...form, umbralCritico: parseInt(e.target.value) || 0 })} />
              </div>
            </div>
            <div style={{ display: 'flex', gap: 10 }}>
              <button className="btn btn-primary" type="submit" disabled={enviando}>
                {enviando ? 'Guardando…' : (editando ? 'Actualizar' : 'Crear')}
              </button>
              <button className="btn btn-ghost" type="button" onClick={cerrarForm}>Cancelar</button>
            </div>
          </form>
        </div>
      )}

      {error && <div className="alert-banner critical"><span>{error}</span></div>}

      {cargando ? (
        <p style={{ color: 'var(--ink-500)' }}>Cargando productos…</p>
      ) : productos.length === 0 ? (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: 'var(--ink-500)' }}>
          No hay productos registrados.
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
                <th>Crítico</th>
                <th>Estado</th>
                <th style={{ width: 100 }}></th>
              </tr>
            </thead>
            <tbody>
              {productos.map((prod) => (
                <tr key={prod.id}>
                  <td className="name">
                    {prod.nombre}
                    {!prod.activo && <span className="badge" style={{ marginLeft: 8, background: 'var(--ink-300)', color: '#fff', fontSize: 10 }}>Inactivo</span>}
                  </td>
                  <td><span className="badge badge-cat">{prod.nombreCategoria || '—'}</span></td>
                  <td>{prod.tieneTalla ? prod.talla : 'N/A'}</td>
                  <td className="num">{prod.stockActual}</td>
                  <td className="num">{prod.umbralCritico}</td>
                  <td><span className="badge" style={estiloEstado(prod.estado)}>{prod.estado}</span></td>
                  <td>
                    <button className="btn btn-sm btn-ghost" onClick={() => abrirEditar(prod)} title="Editar">✏️</button>
                    {prod.activo && (
                      <button className="btn btn-sm btn-ghost" onClick={() => manejarDesactivar(prod.id)} title="Desactivar">❌</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
