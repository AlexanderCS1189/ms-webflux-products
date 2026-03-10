package pe.ms.webflux.products.app.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pe.ms.webflux.products.app.model.Product;
import pe.ms.webflux.products.app.service.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CopyOnWriteArrayList<Product> products;

    @Override
    public Flux<Product> findAll() {
        return Flux
                .defer(() -> Flux.fromIterable(products));
    }


    @Override
    public Flux<Product> findByCategory(String categoryName) {
        return Mono
                .justOrEmpty(categoryName)
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .flatMapMany(cat -> Flux
                        .defer(() -> Flux.fromIterable(products))
                        .filter(p -> {
                                    String pc = p.getCategoryName();
                                    return pc != null && pc.trim().toLowerCase().equals(cat);
                                }
                        )
                );
    }

    @Override
    public Mono<Product> findById(Long id) {
        return Mono
                .justOrEmpty(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("id is required")))
                .flatMap(validId -> Flux
                        .defer(() -> Flux.fromIterable(products))
                        .filter(p -> Objects.equals(p.getProductId(), validId))
                        .next()
                );
    }

    @Override
    public Mono<Product> create(Product product) {
        return Mono
                .defer(() -> Mono.just(product))
                .flatMap(p -> {
                    if (p.getProductId() != null) {
                        return Mono
                                .error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "El id debe ser nulo al crear el producto"));
                    }
                    return Mono.just(p);
                })
                .flatMap(p -> {
                    long nextId = products
                            .stream()
                            .map(Product::getProductId)
                            .filter(Objects::nonNull)
                            .mapToLong(Long::longValue)
                            .max()
                            .orElse(0L) + 1L;
                    p.setProductId(nextId);

                    boolean duplicate = products
                            .stream()
                            .anyMatch(existing -> existing.getProductId() != null
                                    && existing.getProductId().equals(p.getProductId()));
                    if (duplicate) {
                        return Mono
                                .error(new ResponseStatusException(HttpStatus.CONFLICT, "El producto ya existe"));
                    }
                    return Mono.just(p);
                })
                .doOnNext(products::add)
                .onErrorMap(IllegalArgumentException.class, ex ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex));
    }

    @Override
    public Mono<Product> updateUnitPrice(Long id, BigDecimal unitPrice) {
        return findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Product " + id + " not found")))
                .map(p -> {
                    p.setUnitPrice(unitPrice);
                    return p;
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono
                .fromRunnable(() -> products.removeIf(p -> Objects.equals(p.getProductId(), id)))
                .then();
    }
}
