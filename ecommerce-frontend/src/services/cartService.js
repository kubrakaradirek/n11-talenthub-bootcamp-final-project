import apiClient from './apiClient';

export const getCart = async (username) => {
    const response = await apiClient.get(`/api/shopping-cart/${username}`, { skipAuth: true });
    return response.data;
};

export const addToCart = async (username, productId, quantity = 1) => {
    const response = await apiClient.post(`/api/shopping-cart/${username}/add`, null, {
        params: { productId, quantity },
        skipAuth: true,
    });
    return response.data;
};

export const updateCartQuantity = async (username, productId, quantity) => {
    const response = await apiClient.post(`/api/shopping-cart/${username}/update`, null, {
        params: { productId, quantity },
        skipAuth: true,
    });
    return response.data;
};

export const removeCartItem = async (username, productId) => {
    const response = await apiClient.delete(`/api/shopping-cart/${username}/remove`, {
        params: { productId },
        skipAuth: true,
    });
    return response.data;
};
