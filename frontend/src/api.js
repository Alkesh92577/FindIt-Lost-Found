
const API = (import.meta.env.VITE_API_URL || '/api').replace(/\/+$/, '');

export function getToken() {
  return localStorage.getItem('findit_token');
}

export function getUser() {
  try {
    return JSON.parse(localStorage.getItem('findit_user') || 'null');
  } catch {
    return null;
  }
}

export function setSession(data) {
  localStorage.setItem('findit_token', data.token);
  localStorage.setItem('findit_user', JSON.stringify(data.user));
}

export function clearSession() {
  localStorage.removeItem('findit_token');
  localStorage.removeItem('findit_user');
}

export async function request(path, options = {}) {
  const headers = new Headers(options.headers || {});
  const token = getToken();

  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  if (
    options.body &&
    !(options.body instanceof FormData) &&
    !headers.has('Content-Type')
  ) {
    headers.set('Content-Type', 'application/json');
  }

  const cleanPath = path.startsWith('/') ? path : `/${path}`;

  const response = await fetch(`${API}${cleanPath}`, {
    ...options,
    headers,
  });

  const contentType = response.headers.get('content-type') || '';
  const data = contentType.includes('application/json')
    ? await response.json()
    : await response.text();

  if (!response.ok) {
    throw new Error(
      data?.detail ||
      data?.message ||
      data?.title ||
      (typeof data === 'string' ? data : 'Request failed')
    );
  }

  return data;
}

const API_BASE = (
  import.meta.env.VITE_API_URL || '/api'
).replace(/\/+$/, '');

const BACKEND_URL = API_BASE.replace(/\/api\/?$/, '');

export const imageUrl = (path) =>
  path
    ? path.startsWith('http')
      ? path
      : `${BACKEND_URL}${path.startsWith('/') ? path : `/${path}`}`
    : null;