import { useState, useEffect } from 'react';
import client from '../api/client';

export default function Categorias() {
  const [categorias, setCategorias] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [editando, setEditando] = useState(null); // id de la categoría en edición
  const [nombre, setNombre] = useState('');
  const [enviando, setEnviando] = useState(false);

  const cargarCategorias = async () => {
    try {
      setCargando(true);
      const { data } = await client.get('/categories');
      setCategorias(data.data || []);
    } catch {
      setError('Error al cargar categorías.');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => { cargarCategorias(); }, []);

  const manejarCrear = async (e) => {
    e.preventDefault();
    if (!nombre.trim()) return;
    try {
      setEnviando(true);
      await client.post('/categories', { nombre: nombre.trim() });
      setNombre('');
      cargarCategorias();
    } catch {
      alert('Error al crear la categoría.');
    } finally {
      setEnviando(false);
    }
  };

  const iniciarEdicion = (cat) => {
    setEditando(cat.id);
    setNombre(cat.nombre);
  };

  const cancelarEdicion = () => {
    setEditando(null);
    setNombre('');
  };

  const manejarActualizar = async (id) => {
    if (!nombre.trim()) return;
    try {
      setEnviando(true);
      await client.put(`/categories/${id}`, { nombre: nombre.trim() });
      setEditando(null);
      setNombre('');
      cargarCategorias();
    } catch {
      alert('Error al actualizar la categoría.');
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div>
      <h1 className="page-title">Categorías</h1>

      <div className="card" style={{ padding: 20, marginBottom: 24, maxWidth: 480 }}>
        <h2 style={{ fontFamily: 'var(--display)', fontSize: 18, fontWeight: 400, marginBottom: 12 }}>
          {editando ? 'Editar categoría' : 'Nueva categoría'}
        </h2>
        <form onSubmit={editando ? (e) => { e.preventDefault(); manejarActualizar(editando); } : manejarCrear} style={{ display: 'flex', gap: 10, alignItems: 'flex-end' }}>
          <div className="field" style={{ marginBottom: 0, flex: 1 }}>
            <label className="label">Nombre <span className="req">*</span></label>
            <input className="input" type="text" placeholder="Ej: Alimentos no perecederos" value={nombre} onChange={(e) => setNombre(e.target.value)} required />
          </div>
          <button className="btn btn-primary" type="submit" disabled={enviando}>
            {editando ? 'Guardar' : 'Crear'}
          </button>
          {editando && <button className="btn btn-ghost" type="button" onClick={cancelarEdicion}>Cancelar</button>}
        </form>
      </div>

      {error && <div className="alert-banner critical"><span>{error}</span></div>}

      {cargando ? (
        <p style={{ color: 'var(--ink-500)' }}>Cargando categorías…</p>
      ) : categorias.length === 0 ? (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: 'var(--ink-500)' }}>
          No hay categorías registradas.
        </div>
      ) : (
        <div className="card" style={{ overflow: 'auto', maxWidth: 480 }}>
          <table className="data">
            <thead>
              <tr>
                <th>Nombre</th>
                <th style={{ width: 80 }}></th>
              </tr>
            </thead>
            <tbody>
              {categorias.map((cat) => (
                <tr key={cat.id}>
                  <td className="name">{cat.nombre}</td>
                  <td>
                    <button className="btn btn-sm btn-ghost" onClick={() => iniciarEdicion(cat)} title="Editar">
                      ✏️
                    </button>
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
