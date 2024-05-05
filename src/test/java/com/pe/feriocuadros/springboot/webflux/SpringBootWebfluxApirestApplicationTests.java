package com.pe.feriocuadros.springboot.webflux;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pe.feriocuadros.springboot.webflux.documents.Categoria;
import com.pe.feriocuadros.springboot.webflux.documents.Producto;
import com.pe.feriocuadros.springboot.webflux.service.ProductService;

import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApirestApplicationTests {

	@Autowired
	private WebTestClient client;
	
	@Autowired
	private ProductService service;
	
	@Value("${config.base.endpoint}")
	String path;
	
	@Test
	void listarTest() {
		client.get()
			.uri(path)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Producto.class)
			.consumeWith(response -> {
				List<Producto> lista = response.getResponseBody();
				assertFalse(lista.isEmpty());
				assertEquals(6, lista.size());
				assertThat(lista.size() > 0).isTrue();
			});
	}
	
	@Test
	void testDetalle(){		
		Producto producto = service.findByNombre("Monitor").block();		
		client.get()
		.uri(path.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith(response -> {
			Producto prod = response.getResponseBody();
			assertEquals("Monitor", prod.getNombre());
			assertThat(prod.getId()).isNotEmpty();
		});
	}
	
	@Test
	void testCrear(){
		
		Categoria categoria = service.findCategoriaByNombre("Computacion").block();
		Producto producto = new Producto("CPU", 5000.00, categoria);
		
		client.post()
			.uri(path)
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(new ParameterizedTypeReference<LinkedHashMap<String, Object>>() {})
			.consumeWith(response -> {
				Object o = response.getResponseBody().get("producto");
				Producto prod = new ObjectMapper().convertValue(o, Producto.class);
				assertEquals("CPU", prod.getNombre());
				assertEquals(5000.00, prod.getPrecio());
				assertThat(prod.getId()).isNotEmpty();
			});
	}
	
	@Test
	void testEditar() throws Exception {
		Producto producto = service.findByNombre("Computadora").block();
		Categoria categoria = service.findCategoriaByNombre("Periferico").block();
		
		Producto editado = new Producto("Laptop", 1200.00, categoria);
		
		client.put()
		.uri(path.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
		.accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)
		.body(Mono.just(editado), Producto.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody(Producto.class)
		.consumeWith(response -> {
			Producto prod = response.getResponseBody();
			assertEquals("Laptop", prod.getNombre());
			assertEquals(1200.00, prod.getPrecio());
			assertThat(prod.getId()).isNotEmpty();
		});
	}
	
	@Test
	void testEliminar() throws Exception {
		Producto producto = service.findByNombre("Computadora").block();
		
		client.delete()
		.uri(path.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNoContent()
		.expectBody().isEmpty();
		
		client.get()
		.uri(path.concat("/{id}"), Collections.singletonMap("id", producto.getId()))
		.exchange()
		.expectStatus().isNotFound()
		.expectBody().isEmpty();
	}

}
