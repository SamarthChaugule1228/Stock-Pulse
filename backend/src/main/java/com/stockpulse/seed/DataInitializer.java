package com.stockpulse.seed;

import com.stockpulse.entity.InventorySnapshot;
import com.stockpulse.entity.Product;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import com.stockpulse.repository.InventorySnapshotRepository;
import com.stockpulse.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(ProductRepository productRepository, InventorySnapshotRepository snapshotRepository) {
	return args -> {
	    if (productRepository.count() > 0) {
		return;
	    }
	    List<Product> products = productRepository.saveAll(List.of(
		    new Product("SKU-ELEC-001", "Wireless Noise-Cancelling Headphones", Category.ELECTRONICS,
			    new BigDecimal("129.99"), 42, 10, 6, ProductLifecycle.ACTIVE),
		    new Product("SKU-ELEC-002", "Smart Home Hub", Category.ELECTRONICS,
			    new BigDecimal("89.99"), 25, 8, 4, ProductLifecycle.ACTIVE),
		    new Product("SKU-APP-001", "Organic Cotton T-Shirt", Category.APPAREL,
			    new BigDecimal("24.99"), 8, 15, 12, ProductLifecycle.PRICE_REVIEW_PENDING),
		    new Product("SKU-HOME-001", "Bamboo Storage Basket", Category.HOME,
			    new BigDecimal("32.50"), 31, 10, 3, ProductLifecycle.ACTIVE),
		    new Product("SKU-ELEC-003", "Portable Bluetooth Speaker", Category.ELECTRONICS,
			    new BigDecimal("59.99"), 18, 7, 5, ProductLifecycle.ACTIVE),
		    new Product("SKU-HOME-002", "Ceramic Pour-Over Set", Category.HOME,
			    new BigDecimal("41.00"), 14, 9, 4, ProductLifecycle.ACTIVE),
		    new Product("SKU-APP-002", "Merino Wool Beanie", Category.APPAREL,
			    new BigDecimal("28.99"), 22, 9, 5, ProductLifecycle.ACTIVE),
		    new Product("SKU-APP-003", "Hoodie - Heather Grey", Category.APPAREL,
			    new BigDecimal("54.99"), 11, 12, 15, ProductLifecycle.ACTIVE)
	    ));
	    snapshotRepository.saveAll(products.stream()
		    .map(product -> new InventorySnapshot(product, product.getStockLevel(), product.getDemandVelocity(), Instant.now()))
		    .toList());
	};
    }
}
