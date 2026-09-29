import PricingSuggestion from './PricingSuggestion.jsx';
import ReorderSuggestion from './ReorderSuggestion.jsx';

export default function SuggestionCard({ suggestion, onDecision }) {
  return suggestion.type === 'pricing'
    ? <PricingSuggestion suggestion={suggestion} onDecision={onDecision} />
    : <ReorderSuggestion suggestion={suggestion} onDecision={onDecision} />;
}
