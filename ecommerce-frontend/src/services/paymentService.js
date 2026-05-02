import apiClient from './apiClient';

export const buildPaymentPayloadFromCart = (cart, checkoutForm = {}) => ({
    username: checkoutForm.username,
    buyer: {
        id: checkoutForm.username,
        name: checkoutForm.firstName,
        surname: checkoutForm.lastName,
        email: checkoutForm.email,
        identityNumber: checkoutForm.identityNumber,
        gsmNumber: checkoutForm.phone,
        registrationAddress: checkoutForm.streetAddress,
        city: checkoutForm.city,
        country: checkoutForm.country,
        ip: '127.0.0.1',
    },
    card: {
        cardHolderName: checkoutForm.cardHolderName,
        cardNumber: checkoutForm.cardNumber,
        expireMonth: checkoutForm.expireMonth,
        expireYear: checkoutForm.expireYear,
        cvc: checkoutForm.cvc,
    },
    billingAddress: {
        address: checkoutForm.streetAddress,
        city: checkoutForm.city,
        country: checkoutForm.country,
        zipCode: checkoutForm.zipCode,
    },
    shippingAddress: {
        address: checkoutForm.streetAddress,
        city: checkoutForm.city,
        country: checkoutForm.country,
        zipCode: checkoutForm.zipCode,
    },
    items: (cart?.items || []).map((item) => ({
        productId: item.productId,
        productName: item.title || item.productName,
        price: item.price,
        quantity: item.quantity,
    })),
});

export const createOrderPayment = async (cart, checkoutForm) => {
    const payload = buildPaymentPayloadFromCart(cart, checkoutForm);
    const response = await apiClient.post('/api/payments', payload);
    return response.data;
};
