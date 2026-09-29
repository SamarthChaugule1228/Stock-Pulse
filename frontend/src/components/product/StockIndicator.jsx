export default function StockIndicator({ stock, threshold }) {
  const percentage = Math.min(100, Math.round((stock / Math.max(threshold * 3, 1)) * 100));
  const tone = stock === 0 ? 'critical' : stock < threshold ? 'low' : 'healthy';

  return (
    <div className="stock-indicator">
      <div className="stock-meta"><strong>{stock}</strong><span>{stock < threshold ? 'Below threshold' : 'units'}</span></div>
      <div className="stock-track"><span className={`stock-fill ${tone}`} style={{ width: `${Math.max(percentage, 8)}%` }} /></div>
    </div>
  );
}
