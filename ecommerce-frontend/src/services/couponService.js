/*import apiClient from './apiClient';

export const previewCoupon = async (userId, couponCode, totalPrice) => {
    const response = await apiClient.get('/api/orders/coupons/preview', {
        params: { userId, couponCode, totalPrice },
        skipAuth: true,
    });
    return response.data;
};

export const getUnusedCoupons = async (userId) => {
    const response = await apiClient.get(`/api/orders/coupons/user/${userId}`, { skipAuth: true });
    return response.data;
};

export const getUnusedCouponsByUsername = async (username) => {
    const response = await apiClient.get(`/api/orders/coupons/username/${username}`, { skipAuth: true });
    return response.data;
};*/
import apiClient from './apiClient';

export const previewCoupon = async (userId, couponCode, totalPrice) => {
    const response = await apiClient.get('/api/orders/coupons/preview', {
        params: { userId, couponCode, totalPrice }
        // skipAuth silindi, çünkü kuponu denemek için de giriş yapmış olması lazım
    });
    return response.data;
};

export const getUnusedCoupons = async (userId) => {
    // skipAuth silindi! Artık token gönderilecek.
    const response = await apiClient.get(`/api/orders/coupons/user/${userId}`);
    return response.data;
};

export const getUnusedCouponsByUsername = async (username) => {
    // skipAuth silindi! Artık token gönderilecek.
    const response = await apiClient.get(`/api/orders/coupons/username/${username}`);
    return response.data;
};
