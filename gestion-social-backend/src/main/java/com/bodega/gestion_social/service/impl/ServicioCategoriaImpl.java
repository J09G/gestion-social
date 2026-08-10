package com.bodega.gestion_social.service.impl;

import com.bodega.gestion_social.dto.request.SolicitudCategoria;
import com.bodega.gestion_social.dto.response.RespuestaCategoria;
import com.bodega.gestion_social.exception.RecursoNoEncontradoException;
import com.bodega.gestion_social.mapper.CategoriaMapper;
import com.bodega.gestion_social.model.Categoria;
import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.service.ServicioCategoria;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ServicioCategoriaImpl implements ServicioCategoria {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<RespuestaCategoria> listarTodas() {
        return categoriaMapper.toResponseList(categoriaRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion")));
    }

    @Override
    public RespuestaCategoria crear(SolicitudCategoria solicitud) {
        Categoria categoria = new Categoria();
        categoria.setNombre(solicitud.getNombre());
        categoria.setFechaCreacion(LocalDateTime.now());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public RespuestaCategoria actualizar(String id, SolicitudCategoria solicitud) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", "id", id));
        categoria.setNombre(solicitud.getNombre());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }
}