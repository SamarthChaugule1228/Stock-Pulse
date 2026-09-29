package com.stockpulse.entity;

import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 80)
	private String sku;

	@Column(nullable = false, length = 200)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private Category category;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal currentPrice;

	@Column(nullable = false)
	private int stockLevel;

	@Column(nullable = false)
	private int reorderThreshold;

	@Column(nullable = false)
	private int demandVelocity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ProductLifecycle lifecycle = ProductLifecycle.ACTIVE;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<InventorySnapshot> inventorySnapshots = new ArrayList<>();

	protected Product() {
	}

	public Product(String sku, String name, Category category, BigDecimal currentPrice,
				   int stockLevel, int reorderThreshold, int demandVelocity,
				   ProductLifecycle lifecycle) {
		this.sku = sku;
		this.name = name;
		this.category = category;
		this.currentPrice = currentPrice;
		this.stockLevel = stockLevel;
		this.reorderThreshold = reorderThreshold;
		this.demandVelocity = demandVelocity;
		this.lifecycle = lifecycle;
	}

	public void updateStock(int stockLevel) {
		this.stockLevel = stockLevel;
		this.lifecycle = stockLevel == 0 ? ProductLifecycle.OUT_OF_STOCK : this.lifecycle;
		if (stockLevel > 0 && lifecycle == ProductLifecycle.OUT_OF_STOCK) {
			lifecycle = ProductLifecycle.ACTIVE;
		}
	}

	public void recordSale() {
		if (stockLevel > 0) {
			stockLevel--;
		}
		demandVelocity++;
		if (stockLevel == 0) {
			lifecycle = ProductLifecycle.OUT_OF_STOCK;
		}
	}

	public void applyPrice(BigDecimal price) {
		this.currentPrice = price;
		if (lifecycle == ProductLifecycle.PRICE_REVIEW_PENDING) {
			lifecycle = stockLevel == 0 ? ProductLifecycle.OUT_OF_STOCK : ProductLifecycle.ACTIVE;
		}
	}

	public void receiveStock(int quantity) {
		stockLevel += quantity;
		if (stockLevel > 0 && lifecycle == ProductLifecycle.OUT_OF_STOCK) {
			lifecycle = ProductLifecycle.ACTIVE;
		}
	}

	public void markPriceReviewPending() {
		if (stockLevel > 0) {
			lifecycle = ProductLifecycle.PRICE_REVIEW_PENDING;
		}
	}

	public Long getId() { return id; }
	public String getSku() { return sku; }
	public String getName() { return name; }
	public Category getCategory() { return category; }
	public BigDecimal getCurrentPrice() { return currentPrice; }
	public int getStockLevel() { return stockLevel; }
	public int getReorderThreshold() { return reorderThreshold; }
	public int getDemandVelocity() { return demandVelocity; }
	public ProductLifecycle getLifecycle() { return lifecycle; }
	public List<InventorySnapshot> getInventorySnapshots() { return inventorySnapshots; }
}
