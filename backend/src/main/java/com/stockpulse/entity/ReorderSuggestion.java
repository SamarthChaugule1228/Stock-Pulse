package com.stockpulse.entity;

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

import java.time.Instant;

@Entity
@Table(name = "reorder_suggestions")
public class ReorderSuggestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(nullable = false)
	private int currentStock;

	@Column(nullable = false)
	private int recommendedQuantity;

	@Column(nullable = false)
	private int suggestedLeadTimeDays;

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

	protected ReorderSuggestion() {
	}

	public ReorderSuggestion(Product product, int currentStock, int recommendedQuantity,
							  int suggestedLeadTimeDays, double confidence, String reasoning,
							  TriggerReason triggerReason) {
		this.product = product;
		this.currentStock = currentStock;
		this.recommendedQuantity = recommendedQuantity;
		this.suggestedLeadTimeDays = suggestedLeadTimeDays;
		this.confidence = confidence;
		this.reasoning = reasoning;
		this.triggerReason = triggerReason;
	}

	@PrePersist
	void setCreatedAt() { if (createdAt == null) createdAt = Instant.now(); }

	public void decide(SuggestionStatus decision) { this.status = decision; }
	public Long getId() { return id; }
	public Product getProduct() { return product; }
	public int getCurrentStock() { return currentStock; }
	public int getRecommendedQuantity() { return recommendedQuantity; }
	public int getSuggestedLeadTimeDays() { return suggestedLeadTimeDays; }
	public double getConfidence() { return confidence; }
	public String getReasoning() { return reasoning; }
	public SuggestionStatus getStatus() { return status; }
	public TriggerReason getTriggerReason() { return triggerReason; }
	public Instant getCreatedAt() { return createdAt; }
}
