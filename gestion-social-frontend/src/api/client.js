import axios from 'axios';

const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

// Interceptor: agrega token JWT a cada request
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('bg_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptor: redirige al login si 401
client.interceptors.response.use(
  (response) => response,
  (error) => {
    const esEndpointLogin = error.config?.url?.includes('/auth/login');
    if (error.response?.status === 401 && !esEndpointLogin) {
      localStorage.removeItem('bg_token');
      localStorage.removeItem('bg_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default client;
