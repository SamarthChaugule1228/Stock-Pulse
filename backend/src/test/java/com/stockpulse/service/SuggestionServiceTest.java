package com.stockpulse.service;

import com.stockpulse.commerce.CommerceAdvisor;
import com.stockpulse.commerce.CommerceContext;
import com.stockpulse.commerce.CommerceRecommendation;
import com.stockpulse.entity.Product;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.PricingDirection;
import com.stockpulse.enums.SuggestionStatus;
import com.stockpulse.enums.TriggerReason;
import com.stockpulse.exception.AiServiceException;
import com.stockpulse.repository.PricingSuggestionRepository;
import com.stockpulse.repository.ProductRepository;
import com.stockpulse.repository.ReorderSuggestionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SuggestionServiceTest {

    @Test
    void doesNotDuplicatePendingPair() {
        ProductRepository products = mock(ProductRepository.class);
        PricingSuggestionRepository pricing = mock(PricingSuggestionRepository.class);
        ReorderSuggestionRepository reorder = mock(ReorderSuggestionRepository.class);
        CommerceAdvisor advisor = mock(CommerceAdvisor.class);
        Product product = product();
        when(products.findById(1L)).thenReturn(Optional.of(product));
        when(pricing.existsByProductIdAndTriggerReasonAndStatus(1L, TriggerReason.INVENTORY_LOW, SuggestionStatus.PENDING)).thenReturn(true);
        when(reorder.existsByProductIdAndTriggerReasonAndStatus(1L, TriggerReason.INVENTORY_LOW, SuggestionStatus.PENDING)).thenReturn(true);

        new SuggestionService(products, pricing, reorder, advisor).process(1L, TriggerReason.INVENTORY_LOW);

        verifyNoInteractions(advisor);
        verify(pricing, never()).save(any());
        verify(reorder, never()).save(any());
    }

    @Test
    void fallsBackToRulesWhenAdvisorFails() {
        ProductRepository products = mock(ProductRepository.class);
        PricingSuggestionRepository pricing = mock(PricingSuggestionRepository.class);
        ReorderSuggestionRepository reorder = mock(ReorderSuggestionRepository.class);
        CommerceAdvisor advisor = mock(CommerceAdvisor.class);
        Product product = product();
        when(products.findById(1L)).thenReturn(Optional.of(product));
        when(products.averageDemandVelocityByCategory(Category.APPAREL)).thenReturn(5.0);
        when(advisor.getActiveStrategy()).thenReturn("AI");
        when(advisor.recommend(any(CommerceContext.class))).thenThrow(new AiServiceException("timeout"));
        when(advisor.ruleRecommendation(any(CommerceContext.class))).thenReturn(recommendation());

        new SuggestionService(products, pricing, reorder, advisor).process(1L, TriggerReason.DEMAND_SPIKE);

        verify(advisor).ruleRecommendation(any(CommerceContext.class));
        verify(pricing).save(any());
        verify(reorder).save(any());
    }

    @Test
    void usesSelectedAiStrategyForInventoryAndDemandSuggestions() {
        ProductRepository products = mock(ProductRepository.class);
        PricingSuggestionRepository pricing = mock(PricingSuggestionRepository.class);
        ReorderSuggestionRepository reorder = mock(ReorderSuggestionRepository.class);
        CommerceAdvisor advisor = mock(CommerceAdvisor.class);
        Product product = product();
        when(products.findById(1L)).thenReturn(Optional.of(product));
        when(products.averageDemandVelocityByCategory(Category.APPAREL)).thenReturn(5.0);
        when(advisor.getActiveStrategy()).thenReturn("AI");
        when(advisor.recommend(any(CommerceContext.class))).thenReturn(recommendation());
        SuggestionService service = new SuggestionService(products, pricing, reorder, advisor);

        service.process(1L, TriggerReason.INVENTORY_LOW);
        service.process(1L, TriggerReason.DEMAND_SPIKE);

        verify(advisor, times(2)).recommend(any(CommerceContext.class));
        verify(advisor, never()).ruleRecommendation(any(CommerceContext.class));
        verify(pricing, times(2)).save(any());
        verify(reorder, times(2)).save(any());
    }

    @Test
    void fallsBackToRulesForInventoryLowWhenAiResponseIsInvalid() {
        ProductRepository products = mock(ProductRepository.class);
        PricingSuggestionRepository pricing = mock(PricingSuggestionRepository.class);
        ReorderSuggestionRepository reorder = mock(ReorderSuggestionRepository.class);
        CommerceAdvisor advisor = mock(CommerceAdvisor.class);
        Product product = product();
        when(products.findById(1L)).thenReturn(Optional.of(product));
        when(products.averageDemandVelocityByCategory(Category.APPAREL)).thenReturn(5.0);
        when(advisor.getActiveStrategy()).thenReturn("AI");
        when(advisor.recommend(any(CommerceContext.class))).thenThrow(new AiServiceException("Malformed AI response"));
        when(advisor.ruleRecommendation(any(CommerceContext.class))).thenReturn(recommendation());

        new SuggestionService(products, pricing, reorder, advisor).process(1L, TriggerReason.INVENTORY_LOW);

        verify(advisor).ruleRecommendation(any(CommerceContext.class));
        verify(pricing).save(any());
        verify(reorder).save(any());
    }

    private Product product() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);
        when(product.getName()).thenReturn("Test product");
        when(product.getCategory()).thenReturn(Category.APPAREL);
        when(product.getCurrentPrice()).thenReturn(new BigDecimal("10.00"));
        when(product.getStockLevel()).thenReturn(5);
        when(product.getReorderThreshold()).thenReturn(10);
        when(product.getDemandVelocity()).thenReturn(4);
        return product;
    }

    private CommerceRecommendation recommendation() {
        return new CommerceRecommendation(new BigDecimal("11.00"), PricingDirection.INCREASE,
                0.9, "rule", 25, 7, 0.9, "rule");
    }
}