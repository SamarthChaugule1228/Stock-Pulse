import SuggestionCard from '../components/suggestion/SuggestionCard.jsx';
import LoadingState from '../components/common/LoadingState.jsx';
import ErrorState from '../components/common/ErrorState.jsx';
import { useState } from 'react';

export default function Suggestions({ suggestions, products, loading, busy, error, decideSuggestion, generateSuggestions, generateThresholdPriceTest }) {
  const [toast, setToast] = useState(null);
  const pending = suggestions.filter((suggestion) => suggestion.status === 'PENDING');
  const history = suggestions.filter((suggestion) => suggestion.status !== 'PENDING');

  const handleDecision = async (suggestionId, status) => {
    const suggestion = suggestions.find((item) => item.id === suggestionId);
    try {
      await decideSuggestion(suggestionId, status);
      const message = status === 'ACCEPTED'
        ? suggestion?.type === 'pricing' ? 'Accepted: product price updated.' : 'Accepted: inbound stock added.'
        : 'Suggestion rejected. Product state unchanged.';
      setToast(message);
      window.setTimeout(() => setToast(null), 3200);
    } catch (decisionError) {
      setToast(`Could not apply decision: ${decisionError.message}`);
      window.setTimeout(() => setToast(null), 4200);
    }
  };

  const handleThresholdTest = async () => {
    try {
      await generateThresholdPriceTest();
      setToast('Threshold pricing suggestion generated. Accept it to apply the price change.');
      window.setTimeout(() => setToast(null), 4200);
    } catch (testError) {
      setToast(testError.message);
      window.setTimeout(() => setToast(null), 4200);
    }
  };

  return <div className="page-heading suggestions-page reveal"><div><div className="breadcrumb"><span>Workspace</span><b>/</b> Suggestions</div><h1>AI suggestions</h1><p>Every recommendation waits here for a human decision.</p></div><div className="suggestion-heading-actions"><div className="suggestion-summary"><strong>{pending.length}</strong><span>pending decisions</span></div><button className="button button-secondary" type="button" onClick={handleThresholdTest} disabled={busy || products.length === 0}>＋ Threshold price test</button><button className="button button-primary" type="button" onClick={generateSuggestions} disabled={busy || products.length < 4}>{busy ? <><span className="spinner" /> Generating</> : '＋ Generate 8 suggestions'}</button></div>{error && <div className="connection-warning">Backend unavailable: {error}</div>}<div className="full-width-panel"><div className="suggestion-page-heading"><div><span className="section-eyebrow">DECISION QUEUE</span><h2>Review queue <small>{pending.length} pending</small></h2></div><span className="queue-status"><i /> AI engine online</span></div>{loading ? <LoadingState /> : pending.length ? <div className="suggestion-page-grid">{pending.map((suggestion) => <SuggestionCard key={suggestion.id} suggestion={suggestion} onDecision={handleDecision} />)}</div> : <div className="empty-state"><strong>Queue clear</strong><span>Generate new suggestions or simulate a sale to create a signal.</span></div>}</div>{history.length > 0 && <div className="full-width-panel history-panel"><div className="suggestion-page-heading"><div><span className="section-eyebrow">AUDIT TRAIL</span><h2>Decision history <small>{history.length} resolved</small></h2></div></div><div className="suggestion-page-grid history-grid">{history.map((suggestion) => <SuggestionCard key={suggestion.id} suggestion={suggestion} onDecision={handleDecision} />)}</div></div>}{toast && <div className="toast"><span>✓</span>{toast}</div>}</div>;
}
