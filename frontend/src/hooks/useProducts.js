import { useState } from 'react';
import { initialProducts } from '../constants/mockData.js';

export function useProducts() {
  const [products, setProducts] = useState(initialProducts);
  const [loading, setLoading] = useState(false);

  const simulateSale = (productId) => {
    setLoading(true);
    return new Promise((resolve) => {
      window.setTimeout(() => {
        setProducts((current) => current.map((product) => {
          if (product.id !== productId) return product;
          const stock = Math.max(0, product.stock - 1);
          return { ...product, stock, velocity: product.velocity + 1, lifecycle: stock < product.threshold ? 'PRICE_REVIEW_PENDING' : product.lifecycle };
        }));
        setLoading(false);
        resolve();
      }, 650);
    });
  };

  const updateStock = (productId, amount) => {
    setProducts((current) => current.map((product) => {
      if (product.id !== productId) return product;
      const stock = Math.max(0, product.stock + amount);
      return { ...product, stock, lifecycle: stock === 0 ? 'OUT_OF_STOCK' : stock < product.threshold ? 'PRICE_REVIEW_PENDING' : 'ACTIVE' };
    }));
  };

  return { products, loading, error: null, simulateSale, updateStock };
}
