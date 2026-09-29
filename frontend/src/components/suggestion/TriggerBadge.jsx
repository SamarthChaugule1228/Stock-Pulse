export default function TriggerBadge({ trigger }) {
  return <span className={`trigger-badge ${trigger.toLowerCase()}`}><i />{trigger.replace('_', ' ')}</span>;
}
