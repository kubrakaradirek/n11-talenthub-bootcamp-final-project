import apiClient from './apiClient';

export const reserveStock = async (items) => {
    const response = await apiClient.post('/api/stocks/reserve', { items });
    return response.data;
};

export const releaseStock = async (items) => {
    const response = await apiClient.post('/api/stocks/release', { items });
    return response.data;
};

export const commitStock = async (items) => {
    const response = await apiClient.post('/api/stocks/commit', { items });
    return response.data;
};

export const decreaseStock = async (items) => {
    const response = await apiClient.post('/api/stocks/decrease', { items });
    return response.data;
};

export const increaseStock = async (items) => {
    const response = await apiClient.post('/api/stocks/increase', { items });
    return response.data;
};
