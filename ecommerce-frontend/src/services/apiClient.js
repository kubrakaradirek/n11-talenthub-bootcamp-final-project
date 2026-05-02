import axios from 'axios';

export const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL ||
    import.meta.env.REACT_APP_API_BASE_URL ||
    'http://localhost:8763';

const apiClient = axios.create({
    baseURL: API_BASE_URL,
});

apiClient.interceptors.request.use((config) => {
    if (config.skipAuth) {
        delete config.headers.Authorization;
        return config;
    }

    const token = localStorage.getItem('kuba_token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export default apiClient;
