import apiClient from './apiClient';

export const getProducts = async (page = 0, size = 8) => {
    const response = await apiClient.get('/api/products', {
        params: { page, size },
        skipAuth: true,
    });
    return response.data;
};

export const getProductById = async (id) => {
    const response = await apiClient.get(`/api/products/${id}`, {
        skipAuth: true,
    });
    return response.data;
};
