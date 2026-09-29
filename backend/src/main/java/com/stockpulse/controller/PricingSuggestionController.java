package com.stockpulse.controller;

import com.stockpulse.dto.suggestion.PricingSuggestionResponse;
import com.stockpulse.dto.suggestion.SuggestionDecisionRequest;
import com.stockpulse.service.PricingSuggestionService;
import com.stockpulse.service.SuggestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pricing-suggestions")
public class PricingSuggestionController {

	private final PricingSuggestionService pricingSuggestionService;
	private final SuggestionService suggestionService;

	public PricingSuggestionController(PricingSuggestionService pricingSuggestionService,
									   SuggestionService suggestionService) {
		this.pricingSuggestionService = pricingSuggestionService;
		this.suggestionService = suggestionService;
	}

	@GetMapping
	public List<PricingSuggestionResponse> findAll() {
		return suggestionService.pricingSuggestions();
	}

	@PatchMapping("/{id}")
	public PricingSuggestionResponse decide(@PathVariable Long id,
											@Valid @RequestBody SuggestionDecisionRequest request) {
		return SuggestionService.toResponse(pricingSuggestionService.decide(id, request));
	}
}
