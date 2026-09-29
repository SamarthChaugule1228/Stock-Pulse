const navItems = [
  { id: 'dashboard', label: 'Overview', icon: '◉' },
  { id: 'products', label: 'Product intelligence', icon: '⌘' },
  { id: 'suggestions', label: 'AI suggestions', icon: '✧' },
];

export default function Sidebar({ activeView, onNavigate, pendingCount = 0 }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-section-label">Workspace</div>
      <div className="side-nav">
        {navItems.map((item) => (
          <button
            className={`side-link ${activeView === item.id ? 'is-active' : ''}`}
            key={item.id}
            type="button"
            onClick={() => onNavigate(item.id)}
          >
            <span className="side-icon">{item.icon}</span>
            <span>{item.label}</span>
            {item.id === 'suggestions' && pendingCount > 0 && <b>{pendingCount}</b>}
          </button>
        ))}
      </div>

      <div className="sidebar-divider" />
      <div className="sidebar-section-label">System</div>
      <div className="system-status"><i /> Commerce engine <span>v1.0</span></div>
      <div className="system-status"><i className="cyan" /> H2 database <span>Connected</span></div>

      <div className="sidebar-footer">
        <div className="mini-sparkline"><span /><span /><span /><span /><span /><span /><span /></div>
        <small>Signal quality</small>
        <strong>98.4%</strong>
      </div>
    </aside>
  );
}
