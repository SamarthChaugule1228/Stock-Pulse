import Header from './Header.jsx';
import Sidebar from './Sidebar.jsx';

export default function DashboardLayout({ children, activeView, onNavigate, pendingCount, notifications, theme, onToggleTheme }) {
  return (
    <div className="app-shell">
      <Header activeView={activeView} onNavigate={onNavigate} notifications={notifications} theme={theme} onToggleTheme={onToggleTheme} />
      <div className="app-body">
        <Sidebar activeView={activeView} onNavigate={onNavigate} pendingCount={pendingCount} />
        <main className="main-content">{children}</main>
      </div>
    </div>
  );
}
