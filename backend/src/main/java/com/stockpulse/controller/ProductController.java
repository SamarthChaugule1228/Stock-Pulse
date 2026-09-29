package com.stockpulse.controller;

import com.stockpulse.dto.order.OrderRequest;
import com.stockpulse.dto.product.CreateProductRequest;
import com.stockpulse.dto.product.ProductResponse;
import com.stockpulse.dto.product.UpdateStockRequest;
import com.stockpulse.dto.suggestion.PricingSuggestionResponse;
import com.stockpulse.dto.suggestion.ReorderSuggestionResponse;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import com.stockpulse.service.ProductService;
import com.stockpulse.service.SuggestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;
	private final SuggestionService suggestionService;

	public ProductController(ProductService productService, SuggestionService suggestionService) {
		this.productService = productService;
		this.suggestionService = suggestionService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
		return productService.create(request);
	}

	@GetMapping
	public List<ProductResponse> find(@RequestParam(required = false) ProductLifecycle status,
									  @RequestParam(required = false) Category category) {
		return productService.find(category, status);
	}

	@PatchMapping("/{id}/stock")
	public ProductResponse updateStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest request) {
		return productService.updateStock(id, request);
	}

	@PostMapping("/{id}/orders")
	public ProductResponse order(@PathVariable Long id, @Valid @RequestBody(required = false) OrderRequest request) {
		return productService.order(id, request == null ? new OrderRequest(1) : request);
	}

	@PostMapping("/{id}/suggest-pricing")
	@ResponseStatus(HttpStatus.CREATED)
	public PricingSuggestionResponse suggestPricing(@PathVariable Long id) {
		return SuggestionService.toResponse(suggestionService.createPricing(id, com.stockpulse.enums.TriggerReason.MANUAL));
	}

	@PostMapping("/{id}/suggest-reorder")
	@ResponseStatus(HttpStatus.CREATED)
	public ReorderSuggestionResponse suggestReorder(@PathVariable Long id) {
		return SuggestionService.toResponse(suggestionService.createReorder(id, com.stockpulse.enums.TriggerReason.MANUAL));
	}
}
