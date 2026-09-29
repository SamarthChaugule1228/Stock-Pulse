import { useState } from 'react';
import { initialSuggestions } from '../constants/mockData.js';

export function useSuggestions() {
  const [suggestions, setSuggestions] = useState(initialSuggestions);

  const decideSuggestion = (id, status) => {
    setSuggestions((current) => current.map((suggestion) => suggestion.id === id ? { ...suggestion, status } : suggestion));
  };

  return { suggestions, loading: false, error: null, decideSuggestion };
}
