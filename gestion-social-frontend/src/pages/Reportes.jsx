import { useState } from 'react';
import client from '../api/client';
import '../styles/reportes.css';

export default function Reportes() {
  const fecha = new Date();
  const [year, setYear] = useState(fecha.getFullYear());
  const [month, setMonth] = useState(fecha.getMonth() + 1);
  const [reporte, setReporte] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState('');

  const cargarReporte = async () => {
    try {
      setCargando(true);
      setError('');
      const { data } = await client.get('/reports/monthly', { params: { year, month } });
      setReporte(data || []);
    } catch {
      setError('Error al cargar el reporte.');
    } finally {
      setCargando(false);
    }
  };

  const exportarExcel = async (url, nombreArchivo) => {
    try {
      const token = localStorage.getItem('bg_token');
      const res = await fetch(`/api${url}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!res.ok) throw new Error();
      const blob = await res.blob();
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = nombreArchivo;
      link.click();
      URL.revokeObjectURL(link.href);
    } catch {
      alert('Error al exportar.');
    }
  };

  const total = reporte.reduce((sum, r) => sum + r.totalUnidades, 0);

  return (
    <div>
      <h1 className="page-title">Reportes</h1>

      <div className="card" style={{ padding: 20, marginBottom: 24 }}>
        <h2 style={{ fontFamily: 'var(--display)', fontSize: 18, fontWeight: 400, marginBottom: 12 }}>Reporte mensual</h2>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', alignItems: 'flex-end' }}>
          <div className="field" style={{ marginBottom: 0, flex: '0 0 110px' }}>
            <label className="label">Año</label>
            <input className="input" type="number" value={year} onChange={(e) => setYear(parseInt(e.target.value) || year)} />
          </div>
          <div className="field" style={{ marginBottom: 0, flex: '0 0 130px' }}>
            <label className="label">Mes</label>
            <select className="select" value={month} onChange={(e) => setMonth(parseInt(e.target.value))}>
              {['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'].map((m, i) => (
                <option key={i + 1} value={i + 1}>{m}</option>
              ))}
            </select>
          </div>
          <button className="btn btn-primary" onClick={cargarReporte} disabled={cargando}>
            {cargando ? 'Cargando…' : 'Cargar reporte'}
          </button>
        </div>
      </div>

      {error && <div className="alert-banner critical"><span>{error}</span></div>}

      {reporte.length > 0 && (
        <div className="card" style={{ padding: 20, marginBottom: 24 }}>
          <h3 style={{ fontFamily: 'var(--display)', fontSize: 16, fontWeight: 400, marginBottom: 16 }}>
            Unidades donadas por categoría — {month}/{year}
          </h3>

          {reporte.map((r) => {
            const pct = total > 0 ? Math.round((r.totalUnidades / total) * 100) : 0;
            return (
              <div key={r.nombreCategoria} style={{ marginBottom: 12 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 3 }}>
                  <span style={{ fontWeight: 500 }}>{r.nombreCategoria}</span>
                  <span className="num">{r.totalUnidades} uds.</span>
                </div>
                <div style={{ background: 'var(--line-soft)', borderRadius: 4, height: 8, overflow: 'hidden' }}>
                  <div style={{
                    width: `${pct}%`,
                    height: '100%',
                    background: pct > 30 ? 'var(--teal-600)' : pct > 15 ? 'var(--sand-500)' : 'var(--ok-600)',
                    borderRadius: 4,
                    transition: 'width 0.4s ease',
                  }} />
                </div>
              </div>
            );
          })}

          <div style={{ marginTop: 16, paddingTop: 12, borderTop: '1px solid var(--line)', display: 'flex', justifyContent: 'space-between', fontWeight: 600 }}>
            <span>Total</span>
            <span className="num">{total} unidades</span>
          </div>
        </div>
      )}

      <div className="card" style={{ padding: 20 }}>
        <h2 style={{ fontFamily: 'var(--display)', fontSize: 18, fontWeight: 400, marginBottom: 12 }}>Exportar datos</h2>
        <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>
          <button className="btn btn-secondary" onClick={() => exportarExcel('/exports/inventory', 'inventario.xlsx')}>
            📦 Exportar inventario
          </button>
          <button className="btn btn-secondary" onClick={() => exportarExcel(`/exports/monthly?year=${year}&month=${month}`, `reporte-${year}-${month}.xlsx`)}>
            📊 Exportar reporte mensual
          </button>
        </div>
      </div>
    </div>
  );
}
