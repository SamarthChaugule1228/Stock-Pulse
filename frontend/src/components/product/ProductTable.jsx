import StockIndicator from './StockIndicator.jsx';

export default function ProductTable({ products, onSale, onStockChange }) {
  return (
    <div className="table-wrap">
      <table className="product-table">
        <thead><tr><th>Product</th><th>Category</th><th>Stock health</th><th>Price</th><th>Velocity</th><th>Status</th><th /></tr></thead>
        <tbody>
          {products.map((product) => {
            const low = product.stock < product.threshold;
            return <tr key={product.id}>
              <td><div className="table-product"><span className={`table-art ${product.accent}`}>{product.category === 'APPAREL' ? '⌁' : product.category === 'HOME' ? '◇' : '◒'}</span><span><strong>{product.name}</strong><small>{product.sku}</small></span></div></td>
              <td><span className="category-label">{product.category}</span></td>
              <td><StockIndicator stock={product.stock} threshold={product.threshold} /></td>
              <td><strong className="table-price">${product.price.toFixed(2)}</strong></td>
              <td><span className="velocity"><i />{product.velocity}<small>/24h</small></span></td>
              <td><span className={`lifecycle ${low ? 'review' : 'active'}`}>{low ? 'REVIEW' : 'ACTIVE'}</span></td>
              <td><button className="table-sale" type="button" onClick={() => onSale(product.id)} aria-label={`Simulate sale for ${product.name}`}>↗</button></td>
            </tr>;
          })}
        </tbody>
      </table>
      <div className="table-foot"><span>Showing <b>{products.length}</b> of 8 products</span><span className="table-tip">Select a product to inspect its signal history <span>→</span></span></div>
    </div>
  );
}
