import { useState } from 'react';
import Dashboard from './pages/Dashboard.jsx';
import Products from './pages/Products.jsx';
import Suggestions from './pages/Suggestions.jsx';
import DashboardLayout from './components/layout/DashboardLayout.jsx';
import { useStockPulse } from './hooks/useStockPulse.js';

export default function App() {
  const [activeView, setActiveView] = useState('dashboard');
  const stockPulse = useStockPulse();

  return (
    <DashboardLayout activeView={activeView} onNavigate={setActiveView} pendingCount={stockPulse.suggestions.filter((suggestion) => suggestion.status === 'PENDING').length}>
      {activeView === 'dashboard' && <Dashboard onNavigate={setActiveView} {...stockPulse} />}
      {activeView === 'products' && <Products onNavigate={setActiveView} {...stockPulse} />}
      {activeView === 'suggestions' && <Suggestions {...stockPulse} />}
    </DashboardLayout>
  );
}
