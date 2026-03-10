package pe.ms.webflux.products.app.service;

import pe.ms.webflux.products.app.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ProductService {

    Flux<Product> findAll();

    Flux<Product> findByCategory(String categoryName);

    Mono<Product> findById(Long id);

    Mono<Product> create(Product product);

    Mono<Product> updateUnitPrice(Long id, BigDecimal price);

    Mono<Void> deleteById(Long id);
}
