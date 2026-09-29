import { useState } from 'react';

const links = [
  { id: 'dashboard', label: 'Dashboard', icon: '◈' },
  { id: 'products', label: 'Products', icon: '▦' },
  { id: 'suggestions', label: 'Suggestions', icon: '✦' },
];

export default function Header({ activeView, onNavigate, notifications = [], theme, onToggleTheme }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const [notificationsOpen, setNotificationsOpen] = useState(false);

  const navigate = (view) => {
    onNavigate(view);
    setMenuOpen(false);
  };

  return (
    <header className="topbar">
      <button className="brand" type="button" onClick={() => navigate('dashboard')} aria-label="Go to dashboard">
        <span className="brand-mark"><span /></span>
        <span className="brand-copy">
          <strong>Stock<span>Pulse</span></strong>
          <small>AI INVENTORY INTELLIGENCE</small>
        </span>
      </button>

      <nav className={`top-nav ${menuOpen ? 'is-open' : ''}`} aria-label="Primary navigation">
        {links.map((link) => (
          <button
            className={`nav-link ${activeView === link.id ? 'is-active' : ''}`}
            key={link.id}
            type="button"
            onClick={() => navigate(link.id)}
          >
            <span>{link.icon}</span>{link.label}
          </button>
        ))}
      </nav>

      <div className="topbar-actions">
        <span className="live-pill"><i /> Online <b>· AI Active</b></span>
        <button className="icon-button notification-button" type="button" aria-label="Open notifications" onClick={() => setNotificationsOpen((open) => !open)}><span>♢</span>{notifications.length > 0 && <b>{notifications.length > 9 ? '9+' : notifications.length}</b>}</button>
        <button className="icon-button theme-button" type="button" aria-label={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`} onClick={onToggleTheme}>{theme === 'dark' ? '☼' : '◐'}</button>
        <button className="avatar" type="button" aria-label="Open profile">JD</button>
        <button className="menu-toggle" type="button" onClick={() => setMenuOpen(!menuOpen)} aria-label="Toggle navigation menu">
          <span /><span /><span />
        </button>
      </div>
      {notificationsOpen && <div className="notification-panel"><div className="notification-panel-head"><strong>Activity center</strong><span>{notifications.length} unread</span></div>{notifications.length ? notifications.map((notification) => <button className="notification-item" key={notification.id} type="button" onClick={() => { onNavigate('suggestions'); setNotificationsOpen(false); }}><i className={notification.tone} /><span><strong>{notification.title}</strong><small>{notification.detail}</small></span><b>→</b></button>) : <div className="notification-empty">No new signals</div>}</div>}
    </header>
  );
}
