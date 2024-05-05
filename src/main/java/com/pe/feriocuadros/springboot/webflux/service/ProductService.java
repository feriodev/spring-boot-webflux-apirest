package com.pe.feriocuadros.springboot.webflux.service;

import com.pe.feriocuadros.springboot.webflux.documents.Categoria;
import com.pe.feriocuadros.springboot.webflux.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

	public Flux<Producto> findAll();
	
	public Flux<Producto> findAllConNombreUpperCase();
	
	public Flux<Producto> findAllConNombreUpperCaseRepeat();
	
	public Mono<Producto> findById(String id);
	
	public Mono<Producto> save(Producto producto);
	
	public Mono<Void> delete(Producto producto);
	
	public Flux<Categoria> findAllCategoria();
	
	public Mono<Categoria> findCategoriaById(String id);
	
	public Mono<Categoria> saveCategoria(Categoria categoria);
	
	public Mono<Producto> findByNombre(String nombre);
	
	public Mono<Categoria> findCategoriaByNombre(String nombre);
}
