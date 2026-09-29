import Header from './Header.jsx';
import Sidebar from './Sidebar.jsx';

export default function DashboardLayout({ children, activeView, onNavigate, pendingCount }) {
  return (
    <div className="app-shell">
      <Header activeView={activeView} onNavigate={onNavigate} />
      <div className="app-body">
        <Sidebar activeView={activeView} onNavigate={onNavigate} pendingCount={pendingCount} />
        <main className="main-content">{children}</main>
      </div>
    </div>
  );
}
