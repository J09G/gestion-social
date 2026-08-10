package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.response.RespuestaProducto;
import com.bodega.gestion_social.dto.response.RespuestaReporteMensual;
import com.bodega.gestion_social.model.Movimiento;
import com.bodega.gestion_social.model.Producto;
import com.bodega.gestion_social.model.TipoMovimiento;
import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.repository.MovimientoRepository;
import com.bodega.gestion_social.repository.ProductoRepository;
import com.bodega.gestion_social.service.ServicioReporte;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioReporteImpl implements ServicioReporte {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public List<RespuestaReporteMensual> obtenerReporteMensual(int anio, int mes) {
        LocalDateTime inicio = LocalDateTime.of(anio, mes, 1, 0, 0);
        LocalDateTime fin = LocalDateTime.of(anio, mes, Month.of(mes).length(inicio.toLocalDate().isLeapYear()), 23, 59, 59);

        List<Movimiento> movimientos = movimientoRepository.findByTipoAndFechaHoraBetween(TipoMovimiento.SALIDA, inicio, fin, Sort.by(Sort.Direction.DESC, "fechaHora"));

        Map<String, Integer> totalesPorCategoria = movimientos.stream()
                .collect(Collectors.groupingBy(
                        Movimiento::getNombreCategoria,
                        Collectors.summingInt(Movimiento::getCantidad)
                ));

        return totalesPorCategoria.entrySet().stream()
                .map(entry -> new RespuestaReporteMensual(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    public byte[] exportarInventarioExcel() {
        List<Producto> productos = productoRepository.findByActivoTrue(Sort.by(Sort.Direction.DESC, "fechaCreacion"));

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet("Inventario");

            Row cabecera = hoja.createRow(0);
            String[] columnas = {"Nombre", "Categoría", "Talla", "Stock Actual", "Estado", "Umbral Crítico"};
            for (int i = 0; i < columnas.length; i++) {
                Cell celda = cabecera.createCell(i);
                celda.setCellValue(columnas[i]);
                CellStyle estilo = libro.createCellStyle();
                Font fuente = libro.createFont();
                fuente.setBold(true);
                estilo.setFont(fuente);
                celda.setCellStyle(estilo);
            }

            int numFila = 1;
            for (Producto p : productos) {
                Row fila = hoja.createRow(numFila++);
                String nombreCategoria = categoriaRepository.findById(p.getIdCategoria())
                        .map(c -> c.getNombre())
                        .orElse("Sin categoría");
                String estado = p.getStockActual() < p.getUmbralCritico() ? "CRÍTICO" : "OK";

                fila.createCell(0).setCellValue(p.getNombre());
                fila.createCell(1).setCellValue(nombreCategoria);
                fila.createCell(2).setCellValue(p.getTalla() != null ? p.getTalla() : "N/A");
                fila.createCell(3).setCellValue(p.getStockActual());
                fila.createCell(4).setCellValue(estado);
                fila.createCell(5).setCellValue(p.getUmbralCritico());
            }

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            libro.write(salida);
            return salida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }

    @Override
    public byte[] exportarReporteMensualExcel(int anio, int mes) {
        List<RespuestaReporteMensual> reporte = obtenerReporteMensual(anio, mes);

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = libro.createSheet("Reporte Mensual");

            Row cabecera = hoja.createRow(0);
            String[] columnas = {"Categoría", "Total Unidades Donadas"};
            for (int i = 0; i < columnas.length; i++) {
                Cell celda = cabecera.createCell(i);
                celda.setCellValue(columnas[i]);
                CellStyle estilo = libro.createCellStyle();
                Font fuente = libro.createFont();
                fuente.setBold(true);
                estilo.setFont(fuente);
                celda.setCellStyle(estilo);
            }

            int numFila = 1;
            for (RespuestaReporteMensual filaReporte : reporte) {
                Row fila = hoja.createRow(numFila++);
                fila.createCell(0).setCellValue(filaReporte.getNombreCategoria());
                fila.createCell(1).setCellValue(filaReporte.getTotalUnidades());
            }

            for (int i = 0; i < columnas.length; i++) {
                hoja.autoSizeColumn(i);
            }

            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            libro.write(salida);
            return salida.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }
}