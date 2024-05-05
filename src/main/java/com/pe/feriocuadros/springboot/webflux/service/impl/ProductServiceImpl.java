package com.pe.feriocuadros.springboot.webflux.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pe.feriocuadros.springboot.webflux.documents.Categoria;
import com.pe.feriocuadros.springboot.webflux.documents.Producto;
import com.pe.feriocuadros.springboot.webflux.repository.CategoriaRepository;
import com.pe.feriocuadros.springboot.webflux.repository.ProductoRepository;
import com.pe.feriocuadros.springboot.webflux.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements ProductService{

	@Autowired
	private ProductoRepository repository;
	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Override
	public Flux<Producto> findAll() {		
		return repository.findAll();
	}
	
	@Override
	public Flux<Producto> findAllConNombreUpperCase(){
		return repository.findAll()
			.map(producto ->{
				producto.setNombre(producto.getNombre().toUpperCase());
				return producto;
			});
	}
	
	@Override
	public Flux<Producto> findAllConNombreUpperCaseRepeat(){
		return findAllConNombreUpperCase().repeat(5000);
	}

	@Override
	public Mono<Producto> findById(String id) {		
		return repository.findById(id);
	}

	@Override
	public Mono<Producto> save(Producto producto) {		
		return repository.save(producto);
	}

	@Override
	public Mono<Void> delete(Producto producto) {		
		return repository.delete(producto);
	}

	@Override
	public Flux<Categoria> findAllCategoria() {
		return categoriaRepository.findAll();
	}

	@Override
	public Mono<Categoria> findCategoriaById(String id) {
		return categoriaRepository.findById(id);
	}

	@Override
	public Mono<Categoria> saveCategoria(Categoria categoria) {
		return categoriaRepository.save(categoria);
	}

	@Override
	public Mono<Producto> findByNombre(String nombre) {
		return repository.findByNombre(nombre);
	}
	
	@Override
	public Mono<Categoria> findCategoriaByNombre(String nombre) {
		return categoriaRepository.findByNombre(nombre);
	}


}
