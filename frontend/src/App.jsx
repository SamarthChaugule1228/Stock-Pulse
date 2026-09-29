import { useEffect, useMemo, useState } from 'react';
import Dashboard from './pages/Dashboard.jsx';
import Products from './pages/Products.jsx';
import Suggestions from './pages/Suggestions.jsx';
import DashboardLayout from './components/layout/DashboardLayout.jsx';
import { useStockPulse } from './hooks/useStockPulse.js';

export default function App() {
  const [activeView, setActiveView] = useState('dashboard');
  const [theme, setTheme] = useState(() => localStorage.getItem('stockpulse-theme') || 'dark');
  const stockPulse = useStockPulse();
  const pendingSuggestions = stockPulse.suggestions.filter((suggestion) => suggestion.status === 'PENDING');
  const notifications = useMemo(() => {
    const items = pendingSuggestions.slice(0, 5).map((suggestion) => ({
      id: suggestion.id,
      title: suggestion.trigger === 'DEMAND_SPIKE' ? `Demand spike · ${suggestion.productName}` : `Inventory low · ${suggestion.productName}`,
      detail: suggestion.type === 'pricing' ? 'Pricing review is waiting' : 'Replenishment review is waiting',
      suggestionId: suggestion.id,
      tone: suggestion.trigger === 'DEMAND_SPIKE' ? 'violet' : 'orange',
    }));
    if (pendingSuggestions.length > 0) items.unshift({ id: 'summary', title: `${pendingSuggestions.length} recommendations are waiting`, detail: 'Open the decision center', suggestionId: null, tone: 'cyan' });
    return items;
  }, [pendingSuggestions]);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem('stockpulse-theme', theme);
  }, [theme]);

  return (
    <DashboardLayout activeView={activeView} onNavigate={setActiveView} pendingCount={pendingSuggestions.length} notifications={notifications} theme={theme} onToggleTheme={() => setTheme((current) => current === 'dark' ? 'light' : 'dark')}>
      {activeView === 'dashboard' && <Dashboard onNavigate={setActiveView} {...stockPulse} />}
      {activeView === 'products' && <Products onNavigate={setActiveView} {...stockPulse} />}
      {activeView === 'suggestions' && <Suggestions {...stockPulse} />}
    </DashboardLayout>
  );
}
