export default function ConfidenceBadge({ value }) {
  return <span className="confidence-badge"><i style={{ '--confidence': `${value * 100}%` }} />{Math.round(value * 100)}% confidence</span>;
}
