package com.stockpulse.entity;

import com.stockpulse.enums.PricingDirection;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "pricing_suggestions")
public class PricingSuggestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal currentPrice;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal recommendedPrice;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PricingDirection direction;

	@Column(nullable = false)
	private double confidence;

	@Column(nullable = false, length = 2000)
	private String reasoning;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private SuggestionStatus status = SuggestionStatus.PENDING;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TriggerReason triggerReason;

	@Column(nullable = false)
	private Instant createdAt;

	protected PricingSuggestion() {
	}

	public PricingSuggestion(Product product, BigDecimal currentPrice, BigDecimal recommendedPrice,
							 PricingDirection direction, double confidence, String reasoning,
							 TriggerReason triggerReason) {
		this.product = product;
		this.currentPrice = currentPrice;
		this.recommendedPrice = recommendedPrice;
		this.direction = direction;
		this.confidence = confidence;
		this.reasoning = reasoning;
		this.triggerReason = triggerReason;
	}

	@PrePersist
	void setCreatedAt() { if (createdAt == null) createdAt = Instant.now(); }

	public void decide(SuggestionStatus decision) { this.status = decision; }
	public Long getId() { return id; }
	public Product getProduct() { return product; }
	public BigDecimal getCurrentPrice() { return currentPrice; }
	public BigDecimal getRecommendedPrice() { return recommendedPrice; }
	public PricingDirection getDirection() { return direction; }
	public double getConfidence() { return confidence; }
	public String getReasoning() { return reasoning; }
	public SuggestionStatus getStatus() { return status; }
	public TriggerReason getTriggerReason() { return triggerReason; }
	public Instant getCreatedAt() { return createdAt; }
}
