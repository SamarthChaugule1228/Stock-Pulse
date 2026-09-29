import { useState } from 'react';

const links = [
  { id: 'dashboard', label: 'Dashboard', icon: '◈' },
  { id: 'products', label: 'Products', icon: '▦' },
  { id: 'suggestions', label: 'Suggestions', icon: '✦' },
];

export default function Header({ activeView, onNavigate }) {
  const [menuOpen, setMenuOpen] = useState(false);

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
        <span className="live-pill"><i /> Live engine</span>
        <button className="avatar" type="button" aria-label="Open profile">JD</button>
        <button className="menu-toggle" type="button" onClick={() => setMenuOpen(!menuOpen)} aria-label="Toggle navigation menu">
          <span /><span /><span />
        </button>
      </div>
    </header>
  );
}
