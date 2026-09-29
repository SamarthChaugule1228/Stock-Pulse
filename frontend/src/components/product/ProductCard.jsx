import StockIndicator from './StockIndicator.jsx';

const formatPrice = (price) => `$${price.toFixed(2)}`;

export default function ProductCard({ product, onSale, onStockChange }) {
  const isLow = product.stock < product.threshold;

  return (
    <article className="product-card">
      <div className={`product-art ${product.accent}`}><span>{product.category === 'ELECTRONICS' ? '◒' : product.category === 'APPAREL' ? '⌁' : '◇'}</span></div>
      <div className="product-card-content">
        <div className="product-card-top"><span className="eyebrow">{product.sku}</span><span className={`status-dot ${isLow ? 'warning' : 'success'}`} /></div>
        <h3>{product.name}</h3>
        <div className="product-card-price">{formatPrice(product.price)} <span>/ current</span></div>
        <StockIndicator stock={product.stock} threshold={product.threshold} />
        <div className="product-card-footer"><span><b>{product.velocity}</b> orders / 24h</span><button className="text-action" type="button" onClick={() => onSale(product.id)}>Simulate sale <span>↗</span></button></div>
        {isLow && <div className="low-note">⚠ Review pending <span>Threshold {product.threshold}</span></div>}
      </div>
      <div className="stock-stepper" aria-label={`Adjust stock for ${product.name}`}>
        <button type="button" onClick={() => onStockChange(product.id, -1)}>-</button>
        <span>{product.stock}</span>
        <button type="button" onClick={() => onStockChange(product.id, 1)}>+</button>
      </div>
    </article>
  );
}
