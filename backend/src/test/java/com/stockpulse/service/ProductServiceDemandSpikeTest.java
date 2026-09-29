package com.stockpulse.service;

import com.stockpulse.entity.Product;
import com.stockpulse.enums.Category;
import com.stockpulse.enums.ProductLifecycle;
import com.stockpulse.event.DemandSpikeEvent;
import com.stockpulse.repository.InventorySnapshotRepository;
import com.stockpulse.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceDemandSpikeTest {

    @Test
    void publishesDemandSpikeWhenVelocityCrossesPeerThreshold() {
        ProductRepository products = mock(ProductRepository.class);
        InventorySnapshotRepository snapshots = mock(InventorySnapshotRepository.class);
        ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
        Product product = new Product("SKU-TEST-001", "Test Hoodie", Category.APPAREL,
                new BigDecimal("50.00"), 40, 10, 14, ProductLifecycle.ACTIVE);
        when(products.findById(1L)).thenReturn(Optional.of(product));
        when(products.averageDemandVelocityByCategoryExcludingProduct(any(Category.class), nullable(Long.class)))
                .thenReturn(5.0);
        when(products.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        new ProductService(products, snapshots, events, 3.0).order(1L, new com.stockpulse.dto.order.OrderRequest(2));

        verify(events).publishEvent(any(DemandSpikeEvent.class));
    }

    @Test
    void publishesDemandSpikeForTrendingProductOnNextSale() {
        ProductRepository products = mock(ProductRepository.class);
        InventorySnapshotRepository snapshots = mock(InventorySnapshotRepository.class);
        ApplicationEventPublisher events = mock(ApplicationEventPublisher.class);
        Product product = new Product("SKU-TEST-002", "Trending Basket", Category.HOME,
                new BigDecimal("32.50"), 23, 10, 17, ProductLifecycle.ACTIVE);
        when(products.findById(2L)).thenReturn(Optional.of(product));
        when(products.averageDemandVelocityByCategoryExcludingProduct(any(Category.class), nullable(Long.class)))
                .thenReturn(20.0);
        when(products.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        new ProductService(products, snapshots, events, 3.0).order(2L, new com.stockpulse.dto.order.OrderRequest(1));

        verify(events).publishEvent(any(DemandSpikeEvent.class));
    }
}