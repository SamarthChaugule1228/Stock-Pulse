package com.stockpulse.service;

import com.stockpulse.dto.suggestion.SuggestionDecisionRequest;
import com.stockpulse.entity.PricingSuggestion;
import com.stockpulse.entity.Product;
import com.stockpulse.entity.ReorderSuggestion;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.repository.PricingSuggestionRepository;
import com.stockpulse.repository.ProductRepository;
import com.stockpulse.repository.ReorderSuggestionRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class SuggestionApprovalServiceTest {

    @Test
    void acceptingPricingAppliesPrice() {
        PricingSuggestionRepository suggestions = mock(PricingSuggestionRepository.class);
        ProductRepository products = mock(ProductRepository.class);
        PricingSuggestion suggestion = mock(PricingSuggestion.class);
        Product product = mock(Product.class);
        when(suggestions.findById(1L)).thenReturn(Optional.of(suggestion));
        when(suggestion.getStatus()).thenReturn(SuggestionStatus.PENDING);
        when(suggestion.getProduct()).thenReturn(product);

        new PricingSuggestionService(suggestions, products).decide(1L,
                new SuggestionDecisionRequest(SuggestionStatus.ACCEPTED));

        verify(product).applyPrice(suggestion.getRecommendedPrice());
        verify(products).save(product);
        verify(suggestion).decide(SuggestionStatus.ACCEPTED);
    }

    @Test
    void acceptingReorderAppliesStock() {
        ReorderSuggestionRepository suggestions = mock(ReorderSuggestionRepository.class);
        ProductRepository products = mock(ProductRepository.class);
        ReorderSuggestion suggestion = mock(ReorderSuggestion.class);
        Product product = mock(Product.class);
        when(suggestions.findById(1L)).thenReturn(Optional.of(suggestion));
        when(suggestion.getStatus()).thenReturn(SuggestionStatus.PENDING);
        when(suggestion.getProduct()).thenReturn(product);

        new ReorderSuggestionService(suggestions, products).decide(1L,
                new SuggestionDecisionRequest(SuggestionStatus.ACCEPTED));

        verify(product).receiveStock(suggestion.getRecommendedQuantity());
        verify(products).save(product);
    }

    @Test
    void rejectingPricingLeavesProductUnchanged() {
        PricingSuggestionRepository suggestions = mock(PricingSuggestionRepository.class);
        ProductRepository products = mock(ProductRepository.class);
        PricingSuggestion suggestion = mock(PricingSuggestion.class);
        when(suggestions.findById(1L)).thenReturn(Optional.of(suggestion));
        when(suggestion.getStatus()).thenReturn(SuggestionStatus.PENDING);

        new PricingSuggestionService(suggestions, products).decide(1L,
                new SuggestionDecisionRequest(SuggestionStatus.REJECTED));

        verifyNoInteractions(products);
        verify(suggestion).decide(SuggestionStatus.REJECTED);
    }
}