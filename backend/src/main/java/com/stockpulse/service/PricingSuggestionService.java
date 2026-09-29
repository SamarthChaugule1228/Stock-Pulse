package com.stockpulse.service;

import com.stockpulse.dto.suggestion.SuggestionDecisionRequest;
import com.stockpulse.entity.PricingSuggestion;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.exception.InvalidSuggestionException;
import com.stockpulse.exception.ResourceNotFoundException;
import com.stockpulse.repository.PricingSuggestionRepository;
import com.stockpulse.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PricingSuggestionService {

	private final PricingSuggestionRepository repository;
	private final ProductRepository productRepository;

	public PricingSuggestionService(PricingSuggestionRepository repository, ProductRepository productRepository) {
		this.repository = repository;
		this.productRepository = productRepository;
	}

	@Transactional
	public PricingSuggestion decide(Long id, SuggestionDecisionRequest request) {
		PricingSuggestion suggestion = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Pricing suggestion not found: " + id));
		validateDecision(suggestion.getStatus(), request.status());
		suggestion.decide(request.status());
		if (request.status() == SuggestionStatus.ACCEPTED) {
			var product = suggestion.getProduct();
			product.applyPrice(suggestion.getRecommendedPrice());
			productRepository.save(product);
		}
		return repository.save(suggestion);
	}

	private void validateDecision(SuggestionStatus current, SuggestionStatus decision) {
		if (current != SuggestionStatus.PENDING || (decision != SuggestionStatus.ACCEPTED && decision != SuggestionStatus.REJECTED)) {
			throw new InvalidSuggestionException("Only a pending suggestion can be accepted or rejected");
		}
	}
}
