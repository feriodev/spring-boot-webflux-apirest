package com.pe.feriocuadros.springboot.webflux;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.pe.feriocuadros.springboot.webflux.documents.Categoria;
import com.pe.feriocuadros.springboot.webflux.documents.Producto;
import com.pe.feriocuadros.springboot.webflux.service.ProductService;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApirestApplication implements CommandLineRunner{

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApirestApplication.class);
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApirestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categoria").subscribe();
		
		Categoria elect = new Categoria("Electronico");
		Categoria compu = new Categoria("Computacion");
		Categoria perif = new Categoria("Periferico");
		
		Flux.just(elect, compu, perif)
			.flatMap(cat -> service.saveCategoria(cat))
			.doOnNext(c -> {
				log.info("Categoria creada: " + c.getNombre() + ", Id: "+ c.getId());
			})
			.thenMany(Flux.just(new Producto("Computadora", 1000.00, elect),
					new Producto("Monitor", 235.00, compu),
					new Producto("Mouse", 40.00, perif),
					new Producto("Estabilizador", 60.00, elect),
					new Producto("Teclado", 50.00, perif))
			.flatMap(producto -> {
				producto.setCreateAt(new Date());
				return service.save(producto);
			})
			).subscribe(producto -> log.info("insert: " + producto.getId().concat(" ").concat(producto.getNombre())));
		
	}

}
