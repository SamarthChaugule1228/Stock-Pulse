const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request(path, options = {}) {
	const response = await fetch(`${API_BASE_URL}${path}`, {
		headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
		...options,
	});
	if (!response.ok) {
		const body = await response.json().catch(() => ({}));
		throw new Error(body.error || `Request failed with status ${response.status}`);
	}
	return response.json();
}

export const suggestionApi = {
	listPricing: () => request('/pricing-suggestions'),
	listReorder: () => request('/reorder-suggestions'),
	decidePricing: (id, status) => request(`/pricing-suggestions/${id}`, { method: 'PATCH', body: JSON.stringify({ status }) }),
	decideReorder: (id, status) => request(`/reorder-suggestions/${id}`, { method: 'PATCH', body: JSON.stringify({ status }) }),
};
