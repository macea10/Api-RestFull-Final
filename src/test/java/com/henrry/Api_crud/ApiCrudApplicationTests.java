package com.henrry.Api_crud;

import com.henrry.Api_crud.Models.Producto;
import com.henrry.Api_crud.Service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Locale;

@SpringBootTest
class ApiCrudApplicationTests {

	@Autowired
	private ProductoService productoService;


	@Test
	void testListarTotalProductos() {
		Flux<Producto> flux = productoService.listarProductos(Locale.getDefault());
		StepVerifier.create(flux)
				.expectNextCount(3) // Esperamos que haya 3 productos iniciales
				.verifyComplete();
	}

	@Test
	void testListarProductos() {
		Flux<Producto> productos = productoService.listarProductos(Locale.getDefault());
		StepVerifier.create(productos)
				.expectNextMatches(p -> p.getNombre().equals("iPhone 14"))
				.expectNextMatches(p -> p.getNombre().equals("Samsung TV"))
				.expectNextMatches(p -> p.getNombre().equals("Nike Air Max"))
				.verifyComplete();
	}

}
