package pe.ms.webflux.products.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import pe.ms.webflux.products.app.model.Product;
import pe.ms.webflux.products.app.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Mono<ResponseEntity<Flux<Product>>> findAll() {
        return Mono
                .defer(() -> Mono.just(ResponseEntity.ok(productService.findAll())));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> findById(@PathVariable Long id) {
        return productService
                .findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public Mono<ResponseEntity<Flux<Product>>> findByCategory(@PathVariable String category) {
        return Mono
                .defer(() -> Mono.just(ResponseEntity.ok(productService.findByCategory(category))));
    }

    @PostMapping
    public Mono<ResponseEntity<Product>> create(@RequestBody Product product, ServerHttpRequest request) {
        return productService
                .create(product)
                .map(saved -> ResponseEntity
                        .created(request.getURI().resolve("/products/" + saved.getProductId()))
                        .body(saved));
    }

    @PatchMapping("/{id}/price")
    public Mono<ResponseEntity<Product>> updateUnitPrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        return productService
                .updateUnitPrice(id, price)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable Long id) {
        return productService
                .deleteById(id)
                .then(Mono.fromSupplier(() -> ResponseEntity.noContent().build()));
    }
}