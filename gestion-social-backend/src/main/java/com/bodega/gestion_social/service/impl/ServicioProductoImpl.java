package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.request.SolicitudProducto;
import com.bodega.gestion_social.dto.response.RespuestaProducto;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.mapper.ProductoMapper;
import com.bodega.gestion_social.model.Categoria;
import com.bodega.gestion_social.model.Producto;
import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.repository.ProductoRepository;
import com.bodega.gestion_social.service.ServicioProducto;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicioProductoImpl implements ServicioProducto {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<RespuestaProducto> listarConFiltros(String idCategoria, String estado, String nombre) {
        Sort sort = Sort.by(Sort.Direction.DESC, "fechaCreacion");
        List<Producto> productos;

        if (nombre != null && !nombre.isBlank()) {
            productos = productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre, sort);
        } else {
            productos = productoRepository.findByActivoTrue(sort);
        }

        if (idCategoria != null && !idCategoria.isBlank()) {
            productos = productos.stream()
                    .filter(p -> idCategoria.equals(p.getIdCategoria()))
                    .toList();
        }

        if ("CRITICO".equalsIgnoreCase(estado)) {
            productos = productos.stream()
                    .filter(p -> p.getStockActual() < p.getUmbralCritico())
                    .toList();
        } else if ("OK".equalsIgnoreCase(estado)) {
            productos = productos.stream()
                    .filter(p -> p.getStockActual() >= p.getUmbralCritico())
                    .toList();
        }

        return mapearARespuesta(productos);
    }

    @Override
    public RespuestaProducto crear(SolicitudProducto solicitud) {
        Producto producto = new Producto();
        producto.setNombre(solicitud.getNombre());
        producto.setIdCategoria(solicitud.getIdCategoria());
        producto.setTieneTalla(solicitud.isTieneTalla());
        producto.setTalla(solicitud.getTalla());
        producto.setStockActual(solicitud.getStockActual());
        producto.setUmbralCritico(solicitud.getUmbralCritico());
        producto.setActivo(true);
        producto.setFechaCreacion(LocalDateTime.now());

        return mapearARespuesta(productoRepository.save(producto));
    }

    @Override
    public RespuestaProducto actualizar(String id, SolicitudProducto solicitud) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));

        producto.setNombre(solicitud.getNombre());
        producto.setIdCategoria(solicitud.getIdCategoria());
        producto.setTieneTalla(solicitud.isTieneTalla());
        producto.setTalla(solicitud.getTalla());
        producto.setStockActual(solicitud.getStockActual());
        producto.setUmbralCritico(solicitud.getUmbralCritico());

        return mapearARespuesta(productoRepository.save(producto));
    }

    @Override
    public void desactivar(String id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public List<RespuestaProducto> obtenerAlertas() {
        return mapearARespuesta(
                productoRepository.findProductosCriticos().stream()
                        .sorted(Comparator.comparing(Producto::getFechaCreacion).reversed())
                        .toList()
        );
    }

    @Override
    public int importarDesdeExcel(MultipartFile archivo) {
        int importados = 0;

        try (Workbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet hoja = workbook.getSheetAt(0);

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) continue;

                String nombre = obtenerValorCelda(fila.getCell(0));
                String nombreCategoria = obtenerValorCelda(fila.getCell(1));
                String talla = obtenerValorCelda(fila.getCell(2));
                int stockActual = (int) obtenerValorNumerico(fila.getCell(3));
                int umbralCritico = (int) obtenerValorNumerico(fila.getCell(4));

                if (nombre == null || nombre.isBlank()) continue;

                // Normalizar y buscar/crear categoría (ignorando mayúsculas/minúsculas)
                String nombreCatNormalizado = nombreCategoria == null ? "" : nombreCategoria.trim().replaceAll("\\s+", " ");
                if (nombreCatNormalizado.isBlank()) {
                    nombreCatNormalizado = "Sin categoría";
                } else {
                    nombreCatNormalizado = Character.toUpperCase(nombreCatNormalizado.charAt(0)) + nombreCatNormalizado.substring(1);
                }

                String finalNombreCat = nombreCatNormalizado;
                String idCategoria = categoriaRepository.findByNombreIgnoreCase(finalNombreCat)
                        .map(Categoria::getId)
                        .orElseGet(() -> {
                            Categoria nuevaCat = new Categoria();
                            nuevaCat.setNombre(finalNombreCat);
                            nuevaCat.setFechaCreacion(LocalDateTime.now());
                            return categoriaRepository.save(nuevaCat).getId();
                        });

                // Buscar producto por nombre y talla
                Optional<Producto> productoExistente;
                if (talla != null && !talla.isBlank()) {
                    productoExistente = productoRepository.findByNombreAndTallaAndActivoTrue(nombre, talla);
                } else {
                    productoExistente = productoRepository.findByNombreAndTallaIsNullAndActivoTrue(nombre);
                }

                if (productoExistente.isPresent()) {
                    Producto prod = productoExistente.get();
                    prod.setStockActual(prod.getStockActual() + stockActual);
                    productoRepository.save(prod);
                } else {
                    Producto nuevo = new Producto();
                    nuevo.setNombre(nombre);
                    nuevo.setIdCategoria(idCategoria);
                    nuevo.setTieneTalla(talla != null && !talla.isBlank());
                    nuevo.setTalla(talla);
                    nuevo.setStockActual(stockActual);
                    nuevo.setUmbralCritico(umbralCritico);
                    nuevo.setActivo(true);
                    nuevo.setFechaCreacion(LocalDateTime.now());
                    productoRepository.save(nuevo);
                }

                importados++;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo Excel: " + e.getMessage(), e);
        }

        return importados;
    }

    private String obtenerValorCelda(Cell celda) {
        if (celda == null) return null;
        return switch (celda.getCellType()) {
            case STRING -> celda.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) celda.getNumericCellValue());
            default -> null;
        };
    }

    private double obtenerValorNumerico(Cell celda) {
        if (celda == null) return 0;
        if (celda.getCellType() == CellType.NUMERIC) return celda.getNumericCellValue();
        if (celda.getCellType() == CellType.STRING) {
            try {
                return Double.parseDouble(celda.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private List<RespuestaProducto> mapearARespuesta(List<Producto> productos) {
        Map<String, String> nombresCategorias = categoriaRepository.findAllById(
                        productos.stream()
                                .map(Producto::getIdCategoria)
                                .filter(java.util.Objects::nonNull)
                                .collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNombre));

        return productos.stream()
                .map(producto -> {
                    String nombreCategoria = nombresCategorias.getOrDefault(producto.getIdCategoria(), "Sin categoría");
                    String estado = producto.getStockActual() < producto.getUmbralCritico() ? "CRITICO" : "OK";

                    RespuestaProducto respuesta = productoMapper.toResponse(producto);
                    respuesta.setNombreCategoria(nombreCategoria);
                    respuesta.setEstado(estado);
                    return respuesta;
                })
                .toList();
    }

    private RespuestaProducto mapearARespuesta(Producto producto) {
        return mapearARespuesta(List.of(producto)).get(0);
    }
}