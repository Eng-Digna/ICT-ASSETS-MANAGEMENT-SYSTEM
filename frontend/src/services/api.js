const BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';

export async function api(path, options = {}) {
  const token = localStorage.getItem('token');
  const response = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: `Bearer ${token}` } : {}), ...(options.headers || {}) }
  });
  const body = await response.json().catch(() => null);
  if (!response.ok) throw new Error(body?.message || body?.error || 'Request failed');
  return body?.data ?? body;
}
