import { useState } from 'react';
import ProductTable from '../components/product/ProductTable.jsx';
import SuggestionCard from '../components/suggestion/SuggestionCard.jsx';
import LoadingState from '../components/common/LoadingState.jsx';
import ErrorState from '../components/common/ErrorState.jsx';

export default function Dashboard({ onNavigate, products, suggestions, loading, busy, error, simulateSale, updateStock, decideSuggestion }) {
  const [selectedProduct, setSelectedProduct] = useState(3);
  const [toast, setToast] = useState(null);
  const lowStock = products.filter((product) => product.stock < product.threshold).length;
  const pending = suggestions.filter((suggestion) => suggestion.status === 'PENDING').length;
  const spikes = products.filter((product) => product.velocity > 10).length;

  const sale = async (productId = selectedProduct) => {
    setSelectedProduct(productId);
    await simulateSale(productId);
    setToast('Sale simulated · inventory signal refreshed');
    window.setTimeout(() => setToast(null), 2800);
  };

  const decide = (id, status) => {
    decideSuggestion(id, status);
    setToast(status === 'ACCEPTED' ? 'Recommendation accepted · state updated' : 'Recommendation rejected');
    window.setTimeout(() => setToast(null), 2800);
  };

  return (
    <>
      <div className="page-heading reveal"><div><div className="breadcrumb"><span>Workspace</span><b>/</b> Overview</div><h1>Good morning, Jordan <span className="wave">✦</span></h1><p>Here’s what’s happening across your inventory today.</p></div><div className="heading-actions"><span className="updated-label"><i /> Updated just now</span><button className="button button-primary sale-button" type="button" onClick={() => sale()} disabled={loading}>{loading ? <><span className="spinner" /> Processing</> : <><span>＋</span> Simulate sale</>}</button></div></div>
      {error && <div className="connection-warning">Backend unavailable: {error}. Start Spring Boot on port 8080.</div>}
      <div className="metric-grid reveal reveal-delay-1">
        <Metric label="Total products" value={products.length} detail="Across 3 categories" accent="violet" icon="▦" />
        <Metric label="Low stock" value={lowStock} detail="Needs your attention" accent="orange" icon="⌁" trend="+1 today" />
        <Metric label="Pending suggestions" value={pending} detail="Awaiting approval" accent="cyan" icon="✦" />
        <Metric label="Demand spikes" value={spikes} detail="In the last 24 hours" accent="pink" icon="↗" trend="+2.4%" />
      </div>
      <section className="signal-banner reveal reveal-delay-2"><div className="signal-orbit"><span /><span /><span /></div><div><span className="banner-kicker">AGENTIC SIGNAL LOOP <i /></span><h2>Your commerce engine is watching.</h2><p>Signals are analyzed in real time. You stay in control of every decision.</p></div><div className="banner-stats"><span><b>24</b> signals today</span><span><b>98.4%</b> signal quality</span></div></section>
      <section className="section-block reveal reveal-delay-3"><SectionHeading eyebrow="INVENTORY PULSE" title="Product intelligence" action="View all products" onAction={() => onNavigate('products')} /><div className="product-toolbar"><div className="filter-tabs"><button className="is-active" type="button">All products <b>{products.length}</b></button><button type="button">Low stock <b className="orange-count">{lowStock}</b></button><button type="button">Trending <b className="cyan-count">{spikes}</b></button></div><button className="view-toggle" type="button">▤ <span>Table view</span></button></div>{loading ? <LoadingState /> : products.length ? <ProductTable products={products.slice(0, 5)} onSale={sale} onStockChange={updateStock} /> : <ErrorState />}</section>
      <section className="section-block reveal reveal-delay-4"><SectionHeading eyebrow="HUMAN CHECKPOINT" title="AI suggestions" action="Review all" onAction={() => onNavigate('suggestions')} /><div className="suggestion-grid">{suggestions.filter((suggestion) => suggestion.status === 'PENDING').slice(0, 2).map((suggestion) => <SuggestionCard key={suggestion.id} suggestion={suggestion} onDecision={decide} />)}{!suggestions.filter((suggestion) => suggestion.status === 'PENDING').length && <div className="empty-state">No pending suggestions</div>}</div></section>
      <Footer />
      {toast && <div className="toast"><span>✓</span>{toast}</div>}
    </>
  );
}

function Metric({ label, value, detail, accent, icon, trend }) { return <div className={`metric-card ${accent}`}><div className="metric-top"><span className="metric-icon">{icon}</span>{trend && <span className="metric-trend">{trend}</span>}</div><strong className="metric-value">{value}</strong><span className="metric-label">{label}</span><small>{detail}</small><div className="metric-glow" /></div>; }
function SectionHeading({ eyebrow, title, action, onAction }) { return <div className="section-heading"><div><span className="section-eyebrow">{eyebrow}</span><h2>{title}</h2></div><button className="link-button" type="button" onClick={onAction}>{action} <span>↗</span></button></div>; }
function Footer() { return <footer className="footer"><div className="footer-brand"><span className="brand-mark small"><span /></span><strong>Stock<span>Pulse</span></strong></div><p>AI-powered inventory intelligence for the decisions that move commerce forward.</p><span className="footer-project">Built for the future of retail <span>✦</span></span></footer>; }
