package com.stockpulse.service;

import com.stockpulse.dto.suggestion.SuggestionDecisionRequest;
import com.stockpulse.entity.ReorderSuggestion;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.exception.InvalidSuggestionException;
import com.stockpulse.exception.ResourceNotFoundException;
import com.stockpulse.repository.ProductRepository;
import com.stockpulse.repository.ReorderSuggestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReorderSuggestionService {

	private final ReorderSuggestionRepository repository;
	private final ProductRepository productRepository;

	public ReorderSuggestionService(ReorderSuggestionRepository repository, ProductRepository productRepository) {
		this.repository = repository;
		this.productRepository = productRepository;
	}

	@Transactional
	public ReorderSuggestion decide(Long id, SuggestionDecisionRequest request) {
		ReorderSuggestion suggestion = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reorder suggestion not found: " + id));
		if (suggestion.getStatus() != SuggestionStatus.PENDING
				|| (request.status() != SuggestionStatus.ACCEPTED && request.status() != SuggestionStatus.REJECTED)) {
			throw new InvalidSuggestionException("Only a pending suggestion can be accepted or rejected");
		}
		suggestion.decide(request.status());
		if (request.status() == SuggestionStatus.ACCEPTED) {
			var product = suggestion.getProduct();
			product.receiveStock(suggestion.getRecommendedQuantity());
			productRepository.save(product);
		}
		return repository.save(suggestion);
	}
}
