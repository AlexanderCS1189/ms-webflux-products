package pe.ms.webflux.products.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pe.ms.webflux.products.app.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public List<Product> seedProducts() {
        List<Product> products = new ArrayList<>(List.of(
                new Product(1L, "Azucar", "Alimentación", new BigDecimal("1.10"), 20L),
                new Product(2L, "Leche", "Alimentación", new BigDecimal("1.20"), 15L),
                new Product(3L, "Jabón", "Limpieza", new BigDecimal("0.89"), 30L),
                new Product(4L, "Mesa", "Hogar", new BigDecimal("125.00"), 4L),
                new Product(5L, "Televisión", "Hogar", new BigDecimal("650.00"), 10L),
                new Product(6L, "Huevos", "Alimentación", new BigDecimal("2.20"), 30L),
                new Product(7L, "Fregona", "Limpieza", new BigDecimal("3.40"), 6L),
                new Product(8L, "Detergente", "Limpieza", new BigDecimal("8.70"), 12L)
        ));
        return Collections.unmodifiableList(products);
    }
}
