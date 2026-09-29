export const initialProducts = [
  { id: 1, sku: 'SKU-ELEC-001', name: 'Wireless Noise-Cancelling Headphones', category: 'ELECTRONICS', price: 129.99, stock: 42, threshold: 10, velocity: 6, lifecycle: 'ACTIVE', accent: 'violet' },
  { id: 2, sku: 'SKU-ELEC-002', name: 'Smart Home Hub', category: 'ELECTRONICS', price: 89.99, stock: 25, threshold: 8, velocity: 4, lifecycle: 'ACTIVE', accent: 'blue' },
  { id: 3, sku: 'SKU-APP-001', name: 'Organic Cotton T-Shirt', category: 'APPAREL', price: 24.99, stock: 8, threshold: 15, velocity: 12, lifecycle: 'PRICE_REVIEW_PENDING', accent: 'orange' },
  { id: 4, sku: 'SKU-HOME-001', name: 'Bamboo Storage Basket', category: 'HOME', price: 32.5, stock: 31, threshold: 10, velocity: 3, lifecycle: 'ACTIVE', accent: 'green' },
  { id: 5, sku: 'SKU-ELEC-003', name: 'Portable Bluetooth Speaker', category: 'ELECTRONICS', price: 59.99, stock: 18, threshold: 7, velocity: 5, lifecycle: 'ACTIVE', accent: 'cyan' },
  { id: 6, sku: 'SKU-HOME-002', name: 'Ceramic Pour-Over Set', category: 'HOME', price: 41, stock: 14, threshold: 9, velocity: 4, lifecycle: 'ACTIVE', accent: 'peach' },
  { id: 7, sku: 'SKU-APP-002', name: 'Merino Wool Beanie', category: 'APPAREL', price: 28.99, stock: 22, threshold: 9, velocity: 5, lifecycle: 'ACTIVE', accent: 'pink' },
  { id: 8, sku: 'SKU-APP-003', name: 'Hoodie - Heather Grey', category: 'APPAREL', price: 54.99, stock: 11, threshold: 12, velocity: 15, lifecycle: 'ACTIVE', accent: 'yellow' },
];

export const initialSuggestions = [
  {
    id: 'price-3', type: 'pricing', productId: 3, productName: 'Organic Cotton T-Shirt', sku: 'SKU-APP-001', trigger: 'INVENTORY_LOW', status: 'PENDING',
    currentPrice: 24.99, recommendedPrice: 27.49, direction: 'INCREASE', confidence: 0.9,
    reasoning: 'Inventory is below the reorder threshold. A moderate increase protects remaining units while replenishment is evaluated.',
  },
  {
    id: 'reorder-3', type: 'reorder', productId: 3, productName: 'Organic Cotton T-Shirt', sku: 'SKU-APP-001', trigger: 'INVENTORY_LOW', status: 'PENDING',
    currentStock: 8, quantity: 37, leadTime: 7, confidence: 0.9,
    reasoning: 'Restock to three times the threshold to create a buffer for the current demand velocity.',
  },
  {
    id: 'price-8', type: 'pricing', productId: 8, productName: 'Hoodie - Heather Grey', sku: 'SKU-APP-003', trigger: 'DEMAND_SPIKE', status: 'PENDING',
    currentPrice: 54.99, recommendedPrice: 57.74, direction: 'INCREASE', confidence: 0.85,
    reasoning: 'Demand is trending well above the apparel baseline. The 5% move remains measured while preserving margin headroom.',
  },
  {
    id: 'reorder-8', type: 'reorder', productId: 8, productName: 'Hoodie - Heather Grey', sku: 'SKU-APP-003', trigger: 'DEMAND_SPIKE', status: 'PENDING',
    currentStock: 11, quantity: 25, leadTime: 7, confidence: 0.9,
    reasoning: 'Replenish ahead of the demand curve to avoid stockout while the product is trending.',
  },
];

export const categories = ['ALL', 'ELECTRONICS', 'APPAREL', 'HOME'];