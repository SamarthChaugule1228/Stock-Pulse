package com.stockpulse.controller;

import com.stockpulse.dto.suggestion.ReorderSuggestionResponse;
import com.stockpulse.dto.suggestion.SuggestionDecisionRequest;
import com.stockpulse.service.ReorderSuggestionService;
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
@RequestMapping("/reorder-suggestions")
public class ReorderSuggestionController {

	private final ReorderSuggestionService reorderSuggestionService;
	private final SuggestionService suggestionService;

	public ReorderSuggestionController(ReorderSuggestionService reorderSuggestionService,
									   SuggestionService suggestionService) {
		this.reorderSuggestionService = reorderSuggestionService;
		this.suggestionService = suggestionService;
	}

	@GetMapping
	public List<ReorderSuggestionResponse> findAll() {
		return suggestionService.reorderSuggestions();
	}

	@PatchMapping("/{id}")
	public ReorderSuggestionResponse decide(@PathVariable Long id,
											@Valid @RequestBody SuggestionDecisionRequest request) {
		return SuggestionService.toResponse(reorderSuggestionService.decide(id, request));
	}
}
