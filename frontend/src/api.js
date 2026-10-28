const API = '/api'
export function getToken() { return localStorage.getItem('findit_token') }
export function getUser() { try { return JSON.parse(localStorage.getItem('findit_user') || 'null') } catch { return null } }
export function setSession(data) {
  localStorage.setItem('findit_token', data.token)
  localStorage.setItem('findit_user', JSON.stringify(data.user))
}
export function clearSession() { localStorage.removeItem('findit_token'); localStorage.removeItem('findit_user') }
export async function request(path, options = {}) {
  const headers = new Headers(options.headers || {})
  const token = getToken()
  if (token) headers.set('Authorization', `Bearer ${token}`)
  if (options.body && !(options.body instanceof FormData) && !headers.has('Content-Type')) headers.set('Content-Type', 'application/json')
  const response = await fetch(`${API}${path}`, { ...options, headers })
  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json') ? await response.json() : await response.text()
  if (!response.ok) throw new Error(data?.detail || data?.message || data?.title || (typeof data === 'string' ? data : 'Request failed'))
  return data
}
export const imageUrl = (path) => path ? (path.startsWith('http') ? path : `http://localhost:8080${path}`) : null
