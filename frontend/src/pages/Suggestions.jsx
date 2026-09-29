import { useState } from 'react';
import SuggestionCard from '../components/suggestion/SuggestionCard.jsx';
import LoadingState from '../components/common/LoadingState.jsx';
import ErrorState from '../components/common/ErrorState.jsx';

export default function Suggestions({ suggestions, products, loading, busy, error, decideSuggestion }) {
  const [toast, setToast] = useState(null);
  const [filter, setFilter] = useState('PENDING');
  const pending = suggestions.filter((suggestion) => suggestion.status === 'PENDING');
  const history = suggestions.filter((suggestion) => suggestion.status !== 'PENDING');
  const filteredPending = pending.filter((suggestion) => filter === 'PENDING'
    || filter === 'ALL'
    || suggestion.trigger === filter
    || suggestion.productId === Number(filter));

  const showToast = (message, duration = 3200) => {
    setToast(message);
    window.setTimeout(() => setToast(null), duration);
  };

  const handleDecision = async (suggestionId, status) => {
    const suggestion = suggestions.find((item) => item.id === suggestionId);
    try {
      await decideSuggestion(suggestionId, status);
      showToast(status === 'ACCEPTED'
        ? suggestion?.type === 'pricing' ? 'Price applied successfully.' : 'Inbound stock applied successfully.'
        : 'Suggestion rejected. Product state unchanged.');
    } catch (decisionError) {
      showToast(`Could not apply decision: ${decisionError.message}`, 4200);
    }
  };

  return (
    <div className="page-heading suggestions-page reveal">
      <div>
        <div className="breadcrumb"><span>Workspace</span><b>/</b> Suggestions</div>
        <h1>AI suggestions</h1>
        <p>Recommendations arrive automatically after inventory and demand signals.</p>
      </div>
      <div className="suggestion-heading-actions">
        <div className="suggestion-summary"><strong>{pending.length}</strong><span>pending decisions</span></div>
      </div>
      {error && <div className="connection-warning">Backend unavailable: {error}</div>}
      <div className="full-width-panel">
        <div className="suggestion-page-heading">
          <div><span className="section-eyebrow">DECISION QUEUE</span><h2>Review queue <small>{pending.length} pending</small></h2></div>
          <span className="queue-status"><i /> AI engine online</span>
        </div>
        <div className="decision-filters">
          <span>Filter queue</span>
          {[
            ['PENDING', 'All pending'],
            ['INVENTORY_LOW', 'Inventory low'],
            ['DEMAND_SPIKE', 'Demand spike'],
            ...products.slice(0, 3).map((product) => [String(product.id), product.category]),
          ].map(([value, label]) => <button className={filter === value ? 'is-active' : ''} key={value} type="button" onClick={() => setFilter(value)}>{label}</button>)}
        </div>
        {loading ? <LoadingState /> : filteredPending.length ? (
          <div className="suggestion-page-grid">
            {filteredPending.map((suggestion) => <SuggestionCard key={suggestion.id} suggestion={suggestion} onDecision={handleDecision} />)}
          </div>
        ) : <div className="empty-state"><strong>Queue clear</strong><span>Simulate a sale to create an inventory or demand signal.</span></div>}
      </div>
      {history.length > 0 && <div className="full-width-panel history-panel">
        <div className="suggestion-page-heading">
          <div><span className="section-eyebrow">AUDIT TRAIL</span><h2>Decision history <small>{history.length} resolved</small></h2></div>
        </div>
        <div className="decision-filters history-filters">
          <span>History</span>
          <button className={filter === 'ALL' ? 'is-active' : ''} type="button" onClick={() => setFilter('ALL')}>All decisions</button>
          <button className={filter === 'ACCEPTED' ? 'is-active' : ''} type="button" onClick={() => setFilter('ACCEPTED')}>Accepted</button>
          <button className={filter === 'REJECTED' ? 'is-active' : ''} type="button" onClick={() => setFilter('REJECTED')}>Rejected</button>
        </div>
        <div className="suggestion-page-grid history-grid">
          {history.filter((suggestion) => filter !== 'ACCEPTED' && filter !== 'REJECTED' || suggestion.status === filter).map((suggestion) => <SuggestionCard key={suggestion.id} suggestion={suggestion} onDecision={handleDecision} />)}
        </div>
      </div>}
      {toast && <div className="toast"><span>✓</span>{toast}</div>}
    </div>
  );
}

