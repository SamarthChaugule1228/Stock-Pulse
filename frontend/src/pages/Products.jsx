import { useState } from 'react';
import ProductCard from '../components/product/ProductCard.jsx';
import ProductTable from '../components/product/ProductTable.jsx';
import { categories } from '../constants/mockData.js';
import LoadingState from '../components/common/LoadingState.jsx';
import ErrorState from '../components/common/ErrorState.jsx';

export default function Products({ products, loading, error, simulateSale, updateStock }) {
  const [category, setCategory] = useState('ALL');
  const filtered = category === 'ALL' ? products : products.filter((product) => product.category === category);

  return <div className="page-heading products-page reveal"><div><div className="breadcrumb"><span>Workspace</span><b>/</b> Products</div><h1>Product intelligence</h1><p>Monitor stock health, velocity, and lifecycle signals across your catalog.</p></div>{error && <div className="connection-warning">Backend unavailable: {error}</div>}<div className="full-width-panel"><div className="product-page-toolbar"><div className="filter-tabs category-tabs">{categories.map((item) => <button className={category === item ? 'is-active' : ''} key={item} type="button" onClick={() => setCategory(item)}>{item}</button>)}</div><span className="catalog-count">{filtered.length} products</span></div>{loading ? <LoadingState /> : filtered.length ? <><div className="product-card-grid">{filtered.map((product) => <ProductCard key={product.id} product={product} onSale={simulateSale} onStockChange={updateStock} />)}</div><ProductTable products={filtered} onSale={simulateSale} onStockChange={updateStock} /></> : <ErrorState />}</div></div>;
}
