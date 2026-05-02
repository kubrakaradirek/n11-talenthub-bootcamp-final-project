import apiClient from './apiClient';

export const previewCoupon = async (userId, couponCode, totalPrice) => {
    const response = await apiClient.get('/api/orders/coupons/preview', {
        params: { userId, couponCode, totalPrice },
    });
    return response.data;
};

export const getUnusedCoupons = async (userId) => {
    const response = await apiClient.get(`/api/orders/coupons/user/${userId}`);
    return response.data;
};

export const getUnusedCouponsByUsername = async (username) => {
    const response = await apiClient.get(`/api/orders/coupons/username/${username}`);
    return response.data;
};
