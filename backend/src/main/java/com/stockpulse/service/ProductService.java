package com.stockpulse.service;

import com.stockpulse.dto.order.OrderRequest;
import com.stockpulse.dto.product.CreateProductRequest;
import com.stockpulse.dto.product.ProductResponse;
import com.stockpulse.dto.product.UpdateStockRequest;
import com.stockpulse.entity.InventorySnapshot;
import com.stockpulse.entity.Product;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import com.stockpulse.enums.TriggerReason;
import com.stockpulse.event.DemandSpikeEvent;
import com.stockpulse.event.InventoryChangedEvent;
import com.stockpulse.exception.ResourceNotFoundException;
import com.stockpulse.repository.InventorySnapshotRepository;
import com.stockpulse.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final InventorySnapshotRepository snapshotRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final double demandSpikeMultiplier;

	public ProductService(ProductRepository productRepository, InventorySnapshotRepository snapshotRepository,
						  ApplicationEventPublisher eventPublisher,
						  @Value("${commerce.demand-spike-multiplier:3.0}") double demandSpikeMultiplier) {
		this.productRepository = productRepository;
		this.snapshotRepository = snapshotRepository;
		this.eventPublisher = eventPublisher;
		this.demandSpikeMultiplier = demandSpikeMultiplier;
	}

	@Transactional
	public ProductResponse create(CreateProductRequest request) {
		if (productRepository.findBySku(request.sku()).isPresent()) {
			throw new DataIntegrityViolationException("SKU already exists: " + request.sku());
		}
		Product product = new Product(request.sku(), request.name(), request.category(), request.currentPrice(),
				request.stockLevel(), request.reorderThreshold(), request.demandVelocity(),
				request.lifecycle() == null ? ProductLifecycle.ACTIVE : request.lifecycle());
		Product saved = productRepository.save(product);
		snapshotRepository.save(new InventorySnapshot(saved, saved.getStockLevel(), saved.getDemandVelocity(), java.time.Instant.now()));
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> find(Category category, ProductLifecycle lifecycle) {
		List<Product> products;
		if (category != null && lifecycle != null) {
			products = productRepository.findByLifecycleAndCategoryOrderByIdAsc(lifecycle, category);
		} else if (category != null) {
			products = productRepository.findByCategoryOrderByIdAsc(category);
		} else if (lifecycle != null) {
			products = productRepository.findByLifecycleOrderByIdAsc(lifecycle);
		} else {
			products = productRepository.findAllByOrderByIdAsc();
		}
		return products.stream().map(ProductService::toResponse).toList();
	}

	@Transactional
	public ProductResponse updateStock(Long id, UpdateStockRequest request) {
		Product product = get(id);
		product.updateStock(request.stockLevel());
		if (product.getStockLevel() < product.getReorderThreshold()) {
			product.markPriceReviewPending();
		}
		Product saved = productRepository.save(product);
		snapshotRepository.save(new InventorySnapshot(saved, saved.getStockLevel(), saved.getDemandVelocity(), java.time.Instant.now()));
		if (saved.getStockLevel() < saved.getReorderThreshold()) {
			eventPublisher.publishEvent(new InventoryChangedEvent(saved.getId(), TriggerReason.INVENTORY_LOW));
		}
		return toResponse(saved);
	}

	@Transactional
	public ProductResponse order(Long id, OrderRequest request) {
		Product product = get(id);
		if (product.getStockLevel() < request.quantity()) {
			throw new IllegalArgumentException("Insufficient stock for order");
		}
		int previousDemand = product.getDemandVelocity();
		for (int index = 0; index < request.quantity(); index++) {
			product.recordSale();
		}
		Product saved = productRepository.save(product);
		if (saved.getStockLevel() < saved.getReorderThreshold()) {
			saved.markPriceReviewPending();
			saved = productRepository.save(saved);
		}
		snapshotRepository.save(new InventorySnapshot(saved, saved.getStockLevel(), saved.getDemandVelocity(), java.time.Instant.now()));
		if (saved.getStockLevel() < saved.getReorderThreshold()) {
			eventPublisher.publishEvent(new InventoryChangedEvent(saved.getId(), TriggerReason.INVENTORY_LOW));
		}
		double average = productRepository.averageDemandVelocityByCategory(saved.getCategory());
		if (previousDemand < demandSpikeMultiplier * average
				&& saved.getDemandVelocity() >= demandSpikeMultiplier * average) {
			eventPublisher.publishEvent(new DemandSpikeEvent(saved.getId()));
		}
		return toResponse(saved);
	}

	public Product get(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
	}

	public static ProductResponse toResponse(Product product) {
		return new ProductResponse(product.getId(), product.getSku(), product.getName(), product.getCategory(),
				product.getCurrentPrice(), product.getStockLevel(), product.getReorderThreshold(),
				product.getDemandVelocity(), product.getLifecycle());
	}
}
