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
	return response.status === 204 ? null : response.json();
}

export const productApi = {
	list: () => request('/products'),
	order: (id, quantity = 1) => request(`/products/${id}/orders`, { method: 'POST', body: JSON.stringify({ quantity }) }),
	updateStock: (id, stockLevel) => request(`/products/${id}/stock`, { method: 'PATCH', body: JSON.stringify({ stockLevel }) }),
	suggestPricing: (id) => request(`/products/${id}/suggest-pricing`, { method: 'POST' }),
	suggestReorder: (id) => request(`/products/${id}/suggest-reorder`, { method: 'POST' }),
};
