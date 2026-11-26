export class ApiClient {
  constructor(getToken, onUnauthorized) {
    this.getToken = getToken;
    this.onUnauthorized = onUnauthorized;
  }

  async request(path, options = {}) {
    const token = this.getToken();
    const headers = { 'Content-Type': 'application/json', ...(options.headers || {}) };
    if (token) headers.Authorization = `Bearer ${token}`;

    const response = await fetch(path, { ...options, headers });
    if (!response.ok) {
      if (response.status === 401 && this.onUnauthorized) this.onUnauthorized();
      let message;
      try {
        const body = await response.json();
        message = body.message || message;
      } catch {}
      throw new Error(message);
    }
    return response.status === 204 ? null : response.json();
  }
}
