import apiClient from './apiClient';
import { buildPaymentPayloadFromCart, createOrderPayment } from './paymentService';

jest.mock('./apiClient', () => ({
    __esModule: true,
    default: {
        post: jest.fn(),
    },
}));

const cart = {
    items: [
        { productId: 10, title: 'Telefon', price: 12000, quantity: 1 },
        { productId: 20, productName: 'Kulaklik', price: 1500, quantity: 2 },
    ],
};

const form = {
    username: 'kubra',
    firstName: 'Kubra',
    lastName: 'Test',
    streetAddress: 'Adres 1',
    city: 'Istanbul',
    country: 'Turkey',
    zipCode: '34000',
    phone: '+905551112233',
    email: 'kubra@example.com',
    identityNumber: '11111111111',
    cardHolderName: 'Kubra Test',
    cardNumber: '5528790000000008',
    expireMonth: '12',
    expireYear: '2030',
    cvc: '123',
};

describe('paymentService', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('builds an Iyzico payment payload from the current cart', () => {
        expect(buildPaymentPayloadFromCart(cart, form)).toEqual({
            username: 'kubra',
            buyer: {
                id: 'kubra',
                name: 'Kubra',
                surname: 'Test',
                email: 'kubra@example.com',
                identityNumber: '11111111111',
                gsmNumber: '+905551112233',
                registrationAddress: 'Adres 1',
                city: 'Istanbul',
                country: 'Turkey',
                ip: '127.0.0.1',
            },
            card: {
                cardHolderName: 'Kubra Test',
                cardNumber: '5528790000000008',
                expireMonth: '12',
                expireYear: '2030',
                cvc: '123',
            },
            billingAddress: {
                address: 'Adres 1',
                city: 'Istanbul',
                country: 'Turkey',
                zipCode: '34000',
            },
            shippingAddress: {
                address: 'Adres 1',
                city: 'Istanbul',
                country: 'Turkey',
                zipCode: '34000',
            },
            items: [
                { productId: 10, productName: 'Telefon', price: 12000, quantity: 1 },
                { productId: 20, productName: 'Kulaklik', price: 1500, quantity: 2 },
            ],
        });
    });

    it('posts payment requests through the API Gateway payment endpoint', async () => {
        apiClient.post.mockResolvedValueOnce({
            data: { orderId: 77, status: 'success' },
        });

        await expect(createOrderPayment(cart, form)).resolves.toEqual({
            orderId: 77,
            status: 'success',
        });

        expect(apiClient.post).toHaveBeenCalledWith('/api/payments', expect.objectContaining({
            username: 'kubra',
            buyer: expect.objectContaining({ gsmNumber: '+905551112233' }),
            card: expect.objectContaining({ cardNumber: '5528790000000008' }),
        }));
    });

    it('propagates backend payment errors', async () => {
        const error = {
            response: {
                data: { message: 'Odeme reddedildi' },
            },
        };
        apiClient.post.mockRejectedValueOnce(error);

        await expect(createOrderPayment(cart, form)).rejects.toEqual(error);
    });

    it('propagates network errors', async () => {
        const error = new Error('Network Error');
        apiClient.post.mockRejectedValueOnce(error);

        await expect(createOrderPayment(cart, form)).rejects.toThrow('Network Error');
    });
});
