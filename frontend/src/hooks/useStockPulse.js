import { useCallback, useEffect, useRef, useState } from 'react';
import { productApi } from '../api/productApi.js';
import { suggestionApi } from '../api/suggestionApi.js';

function normalizeProducts(products) {
  return products.map((product) => ({
    id: product.id,
    sku: product.sku,
    name: product.name,
    category: product.category,
    price: Number(product.currentPrice),
    stock: product.stockLevel,
    threshold: product.reorderThreshold,
    velocity: product.demandVelocity,
    lifecycle: product.lifecycle,
    accent: ['violet', 'blue', 'orange', 'green', 'cyan', 'peach', 'pink', 'yellow'][(product.id - 1) % 8],
  }));
}

function normalizeSuggestions(pricing, reorder, products) {
  const productName = (productId) => products.find((product) => product.id === productId)?.name || `Product #${productId}`;
  return [
    ...pricing.map((item) => ({
      id: `price-${item.id}`,
      backendId: item.id,
      type: 'pricing',
      productId: item.productId,
      productName: productName(item.productId),
      currentPrice: Number(item.currentPrice),
      recommendedPrice: Number(item.recommendedPrice),
      direction: item.direction,
      confidence: item.confidence,
      reasoning: item.reasoning,
      trigger: item.triggerReason,
      status: item.status,
      createdAt: item.createdAt,
    })),
    ...reorder.map((item) => ({
      id: `reorder-${item.id}`,
      backendId: item.id,
      type: 'reorder',
      productId: item.productId,
      productName: productName(item.productId),
      currentStock: item.currentStock,
      quantity: item.recommendedQuantity,
      leadTime: item.suggestedLeadTimeDays,
      confidence: item.confidence,
      reasoning: item.reasoning,
      trigger: item.triggerReason,
      status: item.status,
      createdAt: item.createdAt,
    })),
  ].sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));
}

export function useStockPulse() {
  const [products, setProducts] = useState([]);
  const [suggestions, setSuggestions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);
  const refreshTimer = useRef(null);

  const refresh = useCallback(async () => {
    const [productData, pricing, reorder] = await Promise.all([
      productApi.list(), suggestionApi.listPricing(), suggestionApi.listReorder(),
    ]);
    setProducts(normalizeProducts(productData));
    setSuggestions(normalizeSuggestions(pricing, reorder, normalizeProducts(productData)));
    setError(null);
  }, []);

  useEffect(() => {
    refresh().catch((requestError) => setError(requestError.message)).finally(() => setLoading(false));
    const interval = window.setInterval(() => refresh().catch(() => {}), 2500);
    return () => {
      window.clearInterval(interval);
      window.clearTimeout(refreshTimer.current);
    };
  }, [refresh]);

  const runAction = async (action) => {
    setBusy(true);
    try {
      await action();
      await refresh();
    } finally {
      setBusy(false);
    }
  };

  const simulateSale = (productId) => runAction(async () => {
    await productApi.order(productId, 1);
    refreshTimer.current = window.setTimeout(() => refresh().catch(() => {}), 900);
  });

  const updateStock = (productId, amount) => {
    const product = products.find((item) => item.id === productId);
    if (!product) return Promise.resolve();
    return runAction(() => productApi.updateStock(productId, Math.max(0, product.stock + amount)));
  };

  const decideSuggestion = (suggestionId, status) => {
    const suggestion = suggestions.find((item) => item.id === suggestionId);
    if (!suggestion) return Promise.resolve();
    const decide = suggestion.type === 'pricing' ? suggestionApi.decidePricing : suggestionApi.decideReorder;
    return runAction(() => decide(suggestion.backendId, status));
  };

  return { products, suggestions, loading, busy, error, refresh, simulateSale, updateStock, decideSuggestion };
}