package com.bodega.gestion_social;

import com.bodega.gestion_social.repository.CategoriaRepository;
import com.bodega.gestion_social.repository.MovimientoRepository;
import com.bodega.gestion_social.repository.ProductoRepository;
import com.bodega.gestion_social.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class GestionSocialApplicationTests {

	@MockitoBean
	private UsuarioRepository usuarioRepository;

	@MockitoBean
	private ProductoRepository productoRepository;

	@MockitoBean
	private CategoriaRepository categoriaRepository;

	@MockitoBean
	private MovimientoRepository movimientoRepository;

	@Test
	void contextLoads() {
	}

}
