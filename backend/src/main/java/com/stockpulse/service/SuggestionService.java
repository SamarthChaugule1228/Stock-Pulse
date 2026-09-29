package com.stockpulse.service;

import com.stockpulse.commerce.CommerceAdvisor;
import com.stockpulse.commerce.CommerceContext;
import com.stockpulse.commerce.CommerceRecommendation;
import com.stockpulse.dto.suggestion.PricingSuggestionResponse;
import com.stockpulse.dto.suggestion.ReorderSuggestionResponse;
import com.stockpulse.entity.PricingSuggestion;
import com.stockpulse.entity.Product;
import com.stockpulse.entity.ReorderSuggestion;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;
import com.stockpulse.exception.AiServiceException;
import com.stockpulse.repository.PricingSuggestionRepository;
import com.stockpulse.repository.ProductRepository;
import com.stockpulse.repository.ReorderSuggestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuggestionService {

	private static final Logger log = LoggerFactory.getLogger(SuggestionService.class);
	private final ProductRepository productRepository;
	private final PricingSuggestionRepository pricingRepository;
	private final ReorderSuggestionRepository reorderRepository;
	private final CommerceAdvisor commerceAdvisor;

	public SuggestionService(ProductRepository productRepository, PricingSuggestionRepository pricingRepository,
							 ReorderSuggestionRepository reorderRepository, CommerceAdvisor commerceAdvisor) {
		this.productRepository = productRepository;
		this.pricingRepository = pricingRepository;
		this.reorderRepository = reorderRepository;
		this.commerceAdvisor = commerceAdvisor;
	}

	@Transactional
	public synchronized void process(Long productId, TriggerReason triggerReason) {
		Product product = productRepository.findById(productId).orElse(null);
		if (product == null) {
			log.warn("Ignoring recommendation event for missing product {}", productId);
			return;
		}
		if (pricingRepository.existsByProductIdAndTriggerReasonAndStatus(productId, triggerReason, SuggestionStatus.PENDING)
				&& reorderRepository.existsByProductIdAndTriggerReasonAndStatus(productId, triggerReason, SuggestionStatus.PENDING)) {
			return;
		}
		CommerceRecommendation recommendation = recommendation(product, triggerReason);
		savePricingIfNeeded(product, triggerReason, recommendation);
		saveReorderIfNeeded(product, triggerReason, recommendation);
	}

	@Transactional
	public PricingSuggestion createPricing(Long productId, TriggerReason triggerReason) {
		Product product = getProduct(productId);
		if (pricingRepository.existsByProductIdAndTriggerReasonAndStatus(productId, triggerReason, SuggestionStatus.PENDING)) {
			return pricingRepository.findAllByOrderByCreatedAtDesc().stream()
					.filter(item -> item.getProduct().getId().equals(productId) && item.getTriggerReason() == triggerReason
							&& item.getStatus() == SuggestionStatus.PENDING).findFirst().orElseThrow();
		}
		CommerceRecommendation recommendation = recommendation(product, triggerReason);
		return pricingRepository.save(toPricing(product, triggerReason, recommendation));
	}

	@Transactional
	public ReorderSuggestion createReorder(Long productId, TriggerReason triggerReason) {
		Product product = getProduct(productId);
		if (reorderRepository.existsByProductIdAndTriggerReasonAndStatus(productId, triggerReason, SuggestionStatus.PENDING)) {
			return reorderRepository.findAllByOrderByCreatedAtDesc().stream()
					.filter(item -> item.getProduct().getId().equals(productId) && item.getTriggerReason() == triggerReason
							&& item.getStatus() == SuggestionStatus.PENDING).findFirst().orElseThrow();
		}
		CommerceRecommendation recommendation = recommendation(product, triggerReason);
		return reorderRepository.save(toReorder(product, triggerReason, recommendation));
	}

	@Transactional(readOnly = true)
	public List<PricingSuggestionResponse> pricingSuggestions() {
		return pricingRepository.findAllByOrderByCreatedAtDesc().stream().map(SuggestionService::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public List<ReorderSuggestionResponse> reorderSuggestions() {
		return reorderRepository.findAllByOrderByCreatedAtDesc().stream().map(SuggestionService::toResponse).toList();
	}

	private CommerceRecommendation recommendation(Product product, TriggerReason triggerReason) {
		CommerceContext context = new CommerceContext(product.getId(), product.getName(), product.getCategory(),
				product.getCurrentPrice(), product.getStockLevel(), product.getReorderThreshold(), product.getDemandVelocity(),
				productRepository.averageDemandVelocityByCategory(product.getCategory()), triggerReason);
		String selectedStrategy = commerceAdvisor.getActiveStrategy();
		try {
			CommerceRecommendation recommendation = commerceAdvisor.recommend(context);
			log.info("Recommendation generated source={} productId={} trigger={}", selectedStrategy, product.getId(), triggerReason);
			return recommendation;
		} catch (AiServiceException exception) {
			if (!"AI".equals(selectedStrategy)) {
				throw exception;
			}
			log.warn("Recommendation source=RULE_FALLBACK selectedStrategy=AI productId={} trigger={} reason={}",
					product.getId(), triggerReason, exception.getMessage());
			return commerceAdvisor.ruleRecommendation(context);
		}
	}

	private void savePricingIfNeeded(Product product, TriggerReason reason, CommerceRecommendation recommendation) {
		if (!pricingRepository.existsByProductIdAndTriggerReasonAndStatus(product.getId(), reason, SuggestionStatus.PENDING)) {
			pricingRepository.save(toPricing(product, reason, recommendation));
		}
	}

	private void saveReorderIfNeeded(Product product, TriggerReason reason, CommerceRecommendation recommendation) {
		if (!reorderRepository.existsByProductIdAndTriggerReasonAndStatus(product.getId(), reason, SuggestionStatus.PENDING)) {
			reorderRepository.save(toReorder(product, reason, recommendation));
		}
	}

	private Product getProduct(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new com.stockpulse.exception.ResourceNotFoundException("Product not found: " + productId));
	}

	private PricingSuggestion toPricing(Product product, TriggerReason reason, CommerceRecommendation recommendation) {
		return new PricingSuggestion(product, product.getCurrentPrice(), recommendation.recommendedPrice(),
				recommendation.pricingDirection(), recommendation.pricingConfidence(), recommendation.pricingReasoning(), reason);
	}

	private ReorderSuggestion toReorder(Product product, TriggerReason reason, CommerceRecommendation recommendation) {
		return new ReorderSuggestion(product, product.getStockLevel(), recommendation.recommendedQuantity(),
				recommendation.suggestedLeadTimeDays(), recommendation.reorderConfidence(), recommendation.reorderReasoning(), reason);
	}

	public static PricingSuggestionResponse toResponse(PricingSuggestion suggestion) {
		return new PricingSuggestionResponse(suggestion.getId(), suggestion.getProduct().getId(), suggestion.getCurrentPrice(),
				suggestion.getRecommendedPrice(), suggestion.getDirection(), suggestion.getConfidence(), suggestion.getReasoning(),
				suggestion.getStatus(), suggestion.getTriggerReason(), suggestion.getCreatedAt());
	}

	public static ReorderSuggestionResponse toResponse(ReorderSuggestion suggestion) {
		return new ReorderSuggestionResponse(suggestion.getId(), suggestion.getProduct().getId(), suggestion.getCurrentStock(),
				suggestion.getRecommendedQuantity(), suggestion.getSuggestedLeadTimeDays(), suggestion.getConfidence(), suggestion.getReasoning(),
				suggestion.getStatus(), suggestion.getTriggerReason(), suggestion.getCreatedAt());
	}
}
