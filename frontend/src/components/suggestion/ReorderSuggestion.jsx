import Button from '../common/Button.jsx';
import ConfidenceBadge from './ConfidenceBadge.jsx';
import TriggerBadge from './TriggerBadge.jsx';

export default function ReorderSuggestion({ suggestion, onDecision }) {
  const decided = suggestion.status !== 'PENDING';

  return (
    <div className={`suggestion-card reorder-card ${decided ? 'is-decided' : ''}`}>
      <div className="suggestion-card-header"><div className="suggestion-type"><span className="suggestion-icon reorder">⌁</span><span><small>REORDER SUGGESTION</small><strong>{suggestion.productName}</strong></span></div><TriggerBadge trigger={suggestion.trigger} /></div>
      <div className="reorder-metrics"><div><small>Current stock</small><strong>{suggestion.currentStock}<span> units</span></strong></div><span className="metric-divider" /><div><small>Recommended</small><strong className="cyan-text">+{suggestion.quantity}<span> units</span></strong></div><span className="metric-divider" /><div><small>Lead time</small><strong>{suggestion.leadTime}<span> days</span></strong></div></div>
      <div className="suggestion-reasoning"><span className="reasoning-mark cyan-mark">✦</span><p>{suggestion.reasoning}</p></div>
      <div className="suggestion-footer"><ConfidenceBadge value={suggestion.confidence} />{decided ? <span className={`decision-label ${suggestion.status.toLowerCase()}`}>{suggestion.status}</span> : <div className="decision-actions"><Button className="button-ghost" onClick={() => onDecision(suggestion.id, 'REJECTED')}>Reject</Button><Button className="button-primary cyan-button" onClick={() => onDecision(suggestion.id, 'ACCEPTED')}>Accept <span>✓</span></Button></div>}</div>
    </div>
  );
}
