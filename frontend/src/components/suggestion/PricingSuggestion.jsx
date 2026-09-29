import Button from '../common/Button.jsx';
import ConfidenceBadge from './ConfidenceBadge.jsx';
import TriggerBadge from './TriggerBadge.jsx';

export default function PricingSuggestion({ suggestion, onDecision }) {
  const decided = suggestion.status !== 'PENDING';
  const delta = suggestion.recommendedPrice - suggestion.currentPrice;

  return (
    <div className={`suggestion-card pricing-card ${decided ? 'is-decided' : ''}`}>
      <div className="suggestion-card-header"><div className="suggestion-type"><span className="suggestion-icon pricing">↗</span><span><small>PRICING SUGGESTION</small><strong>{suggestion.productName}</strong></span></div><TriggerBadge trigger={suggestion.trigger} /></div>
      <div className="price-comparison"><div><small>Current price</small><strong>${suggestion.currentPrice.toFixed(2)}</strong></div><span className="price-arrow">→</span><div className="recommended"><small>Recommended</small><strong>${suggestion.recommendedPrice.toFixed(2)}</strong><em>+{delta.toFixed(2)} · {suggestion.direction}</em></div></div>
      <div className="suggestion-reasoning"><span className="reasoning-mark">✦</span><p>{suggestion.reasoning}</p></div>
      <div className="suggestion-footer"><ConfidenceBadge value={suggestion.confidence} />{decided ? <span className={`decision-label ${suggestion.status.toLowerCase()}`}>{suggestion.status}</span> : <div className="decision-actions"><Button className="button-ghost" onClick={() => onDecision(suggestion.id, 'REJECTED')}>Reject</Button><Button className="button-primary" onClick={() => onDecision(suggestion.id, 'ACCEPTED')}>Accept <span>✓</span></Button></div>}</div>
    </div>
  );
}
