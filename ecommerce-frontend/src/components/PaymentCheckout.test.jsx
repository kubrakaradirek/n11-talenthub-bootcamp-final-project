import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import PaymentCheckout from './PaymentCheckout';
import { createOrderPayment } from '../services/paymentService';

jest.mock('../services/paymentService', () => ({
    createOrderPayment: jest.fn(),
}));

const cart = {
    items: [{ productId: 1, title: 'Laptop', price: 24000, quantity: 1 }],
};

const fillCheckoutForm = () => {
    fireEvent.change(screen.getByPlaceholderText('Ad'), { target: { value: 'Kubra' } });
    fireEvent.change(screen.getByPlaceholderText('Soyad'), { target: { value: 'Yilmaz' } });
    fireEvent.change(screen.getByPlaceholderText('E-posta'), { target: { value: 'kubra@example.com' } });
    fireEvent.change(screen.getByPlaceholderText('Telefon'), { target: { value: '+905551112233' } });
    fireEvent.change(screen.getByPlaceholderText('TCKN'), { target: { value: '11111111111' } });
    fireEvent.change(screen.getByPlaceholderText('Sehir'), { target: { value: 'Istanbul' } });
    fireEvent.change(screen.getByPlaceholderText('Teslimat adresi'), { target: { value: 'Adres 1' } });
    fireEvent.change(screen.getByPlaceholderText('Kart Uzerindeki Ad'), { target: { value: 'Kubra Yilmaz' } });
    fireEvent.change(screen.getByPlaceholderText('Kart Numarasi'), { target: { value: '5528790000000008' } });
    fireEvent.change(screen.getByPlaceholderText('Ay'), { target: { value: '12' } });
    fireEvent.change(screen.getByPlaceholderText('Yil'), { target: { value: '2030' } });
    fireEvent.change(screen.getByPlaceholderText('CVC'), { target: { value: '123' } });
};

describe('PaymentCheckout', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    it('shows success feedback when payment succeeds', async () => {
        const onPaymentSuccess = jest.fn();
        createOrderPayment.mockResolvedValueOnce({ orderId: 42, status: 'success' });

        render(<PaymentCheckout cart={cart} username="kubra" onPaymentSuccess={onPaymentSuccess} />);
        fillCheckoutForm();
        fireEvent.click(screen.getByRole('button', { name: 'Siparisi Tamamla' }));

        expect(screen.getByRole('status')).toHaveTextContent('Odeme islemi baslatiliyor...');

        await waitFor(() => {
            expect(screen.getByRole('status')).toHaveTextContent('Siparis basariyla olusturuldu. Siparis No: 42');
        });
        expect(createOrderPayment).toHaveBeenCalledWith(cart, expect.objectContaining({
            username: 'kubra',
            identityNumber: '11111111111',
            cardNumber: '5528790000000008',
        }));
        expect(onPaymentSuccess).toHaveBeenCalledWith({ orderId: 42, status: 'success' });
    });

    it('shows backend error feedback when payment creation fails', async () => {
        createOrderPayment.mockRejectedValueOnce({
            response: { data: { errorMessage: 'Odeme reddedildi' } },
        });

        render(<PaymentCheckout cart={cart} username="kubra" />);
        fillCheckoutForm();
        fireEvent.click(screen.getByRole('button', { name: 'Siparisi Tamamla' }));

        await waitFor(() => {
            expect(screen.getByRole('status')).toHaveTextContent('Odeme reddedildi');
        });
    });

    it('shows generic error feedback for network errors', async () => {
        createOrderPayment.mockRejectedValueOnce(new Error('Network Error'));

        render(<PaymentCheckout cart={cart} username="kubra" />);
        fillCheckoutForm();
        fireEvent.click(screen.getByRole('button', { name: 'Siparisi Tamamla' }));

        await waitFor(() => {
            expect(screen.getByRole('status')).toHaveTextContent('Odeme sirasinda bir hata olustu.');
        });
    });
});
