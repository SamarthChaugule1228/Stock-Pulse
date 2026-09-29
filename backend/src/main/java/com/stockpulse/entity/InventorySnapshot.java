package com.stockpulse.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "inventory_snapshots")
public class InventorySnapshot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	private int stockLevel;
	private int demandVelocity;
	private Instant observedAt;

	protected InventorySnapshot() {
	}

	public InventorySnapshot(Product product, int stockLevel, int demandVelocity, Instant observedAt) {
		this.product = product;
		this.stockLevel = stockLevel;
		this.demandVelocity = demandVelocity;
		this.observedAt = observedAt;
	}

	public Long getId() { return id; }
	public Product getProduct() { return product; }
	public int getStockLevel() { return stockLevel; }
	public int getDemandVelocity() { return demandVelocity; }
	public Instant getObservedAt() { return observedAt; }
}
