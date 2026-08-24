import { useState, useEffect } from 'react';
import client from '../api/client';
import { IconoEditar, IconoDesactivar } from '../components/Iconos';

export default function Usuarios() {
  const [usuarios, setUsuarios] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [enviando, setEnviando] = useState(false);
  const [editando, setEditando] = useState(null);
  const [mostrarForm, setMostrarForm] = useState(false);

  const formInicial = { nombre: '', correo: '', contrasena: '', rol: 'BODEGUERO' };
  const [form, setForm] = useState(formInicial);

  const cargarUsuarios = async () => {
    try {
      setCargando(true);
      const { data } = await client.get('/users');
      setUsuarios(data.data || []);
    } catch {
      setError('Error al cargar usuarios.');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => { cargarUsuarios(); }, []);

  const abrirCrear = () => {
    setEditando(null);
    setForm(formInicial);
    setMostrarForm(true);
  };

  const abrirEditar = (usr) => {
    setEditando(usr.id);
    setForm({ nombre: usr.nombre, correo: usr.correo, contrasena: '', rol: usr.rol });
    setMostrarForm(true);
  };

  const cerrarForm = () => {
    setMostrarForm(false);
    setEditando(null);
    setForm(formInicial);
  };

  const manejarEnvio = async (e) => {
    e.preventDefault();
    if (!form.nombre.trim() || !form.correo.trim()) return;
    try {
      setEnviando(true);
      const payload = {
        nombre: form.nombre.trim(),
        correo: form.correo.trim(),
        contrasena: form.contrasena,
        rol: form.rol,
      };
      if (editando) {
        if (!payload.contrasena) delete payload.contrasena;
        await client.put(`/users/${editando}`, payload);
      } else {
        await client.post('/users', payload);
      }
      cerrarForm();
      cargarUsuarios();
    } catch (err) {
      const msg = err.response?.data?.message || 'Error al guardar el usuario.';
      alert(msg);
    } finally {
      setEnviando(false);
    }
  };

  const manejarDesactivar = async (id) => {
    if (!confirm('¿Desactivar este usuario?')) return;
    try {
      await client.patch(`/users/${id}/deactivate`);
      cargarUsuarios();
    } catch {
      alert('Error al desactivar.');
    }
  };

  const badgeRol = (rol) => {
    if (rol === 'ADMIN') return { background: 'var(--teal-900)', color: '#fff' };
    return { background: 'var(--sand-200)', color: 'var(--sand-700)' };
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 className="page-title" style={{ marginBottom: 0 }}>Usuarios</h1>
        <button className="btn btn-primary" onClick={abrirCrear}>+ Nuevo usuario</button>
      </div>

      {mostrarForm && (
        <div className="card" style={{ padding: 20, marginBottom: 24, maxWidth: 520 }}>
          <h2 style={{ fontFamily: 'var(--display)', fontSize: 18, fontWeight: 400, marginBottom: 16 }}>
            {editando ? 'Editar usuario' : 'Nuevo usuario'}
          </h2>
          <form onSubmit={manejarEnvio} style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            <div className="field">
              <label className="label">Nombre <span className="req">*</span></label>
              <input className="input" type="text" value={form.nombre} onChange={(e) => setForm({ ...form, nombre: e.target.value })} required />
            </div>
            <div className="field">
              <label className="label">Correo <span className="req">*</span></label>
              <input className="input" type="email" value={form.correo} onChange={(e) => setForm({ ...form, correo: e.target.value })} required />
            </div>
            <div className="field">
              <label className="label">Contraseña{editando ? ' (dejar vacía para no cambiar)' : ''} {editando ? '' : <span className="req">*</span>}</label>
              <input className="input" type="password" value={form.contrasena} onChange={(e) => setForm({ ...form, contrasena: e.target.value })} required={!editando} />
            </div>
            <div className="field">
              <label className="label">Rol <span className="req">*</span></label>
              <select className="select" value={form.rol} onChange={(e) => setForm({ ...form, rol: e.target.value })}>
                <option value="ADMIN">ADMIN</option>
                <option value="BODEGUERO">BODEGUERO</option>
              </select>
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
        <p style={{ color: 'var(--ink-500)' }}>Cargando usuarios…</p>
      ) : usuarios.length === 0 ? (
        <div className="card" style={{ padding: 32, textAlign: 'center', color: 'var(--ink-500)' }}>
          No hay usuarios registrados.
        </div>
      ) : (
        <div className="card" style={{ overflow: 'auto' }}>
          <table className="data">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Correo</th>
                <th>Rol</th>
                <th>Estado</th>
                <th style={{ width: 100 }}></th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usr) => (
                <tr key={usr.id}>
                  <td className="name">
                    {usr.nombre}
                    {!usr.activo && <span className="badge" style={{ marginLeft: 8, background: 'var(--ink-300)', color: '#fff', fontSize: 10 }}>Inactivo</span>}
                  </td>
                  <td style={{ fontFamily: 'var(--mono)', fontSize: 12 }}>{usr.correo}</td>
                  <td><span className="badge" style={badgeRol(usr.rol)}>{usr.rol}</span></td>
                  <td>
                    <span className={`badge ${usr.activo ? 'badge-ok' : 'badge-critical'}`}>
                      {usr.activo ? 'Activo' : 'Inactivo'}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-sm btn-ghost" onClick={() => abrirEditar(usr)} title="Editar" aria-label="Editar usuario">
                      <IconoEditar />
                    </button>
                    {usr.activo && (
                      <button className="btn btn-sm btn-ghost" onClick={() => manejarDesactivar(usr.id)} title="Desactivar" aria-label="Desactivar usuario">
                        <IconoDesactivar />
                      </button>
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
