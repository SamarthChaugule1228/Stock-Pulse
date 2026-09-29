package com.stockpulse.repository;

import com.stockpulse.entity.Product;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findBySku(String sku);
	List<Product> findAllByOrderByIdAsc();
	List<Product> findByLifecycleOrderByIdAsc(ProductLifecycle lifecycle);
	List<Product> findByCategoryOrderByIdAsc(Category category);
	List<Product> findByLifecycleAndCategoryOrderByIdAsc(ProductLifecycle lifecycle, Category category);

	@Query("select coalesce(avg(p.demandVelocity), 0) from Product p where p.category = :category")
	double averageDemandVelocityByCategory(@Param("category") Category category);

	@Query("select coalesce(avg(p.demandVelocity), 0) from Product p where p.category = :category and p.id <> :productId")
	double averageDemandVelocityByCategoryExcludingProduct(@Param("category") Category category,
																				 @Param("productId") Long productId);
}
