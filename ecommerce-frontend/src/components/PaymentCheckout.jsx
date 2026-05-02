import React, { useState } from 'react';
import { createOrderPayment } from '../services/paymentService';

const initialForm = {
    firstName: '',
    lastName: '',
    streetAddress: '',
    city: '',
    country: 'Turkey',
    zipCode: '',
    phone: '',
    identityNumber: '',
    cardHolderName: '',
    cardNumber: '',
    expireMonth: '',
    expireYear: '',
    cvc: '',
    email: '',
};

function PaymentCheckout({ cart, username, onPaymentSuccess }) {
    const [form, setForm] = useState(initialForm);
    const [status, setStatus] = useState({ type: '', message: '' });
    const [submitting, setSubmitting] = useState(false);

    const handleChange = (event) => {
        const { name, value } = event.target;
        setForm((current) => ({ ...current, [name]: value }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        if (submitting) return;

        if (!cart?.items?.length) {
            setStatus({ type: 'error', message: 'Sepetiniz bos. Siparis olusturulamaz.' });
            return;
        }

        setSubmitting(true);
        setStatus({ type: 'loading', message: 'Odeme islemi baslatiliyor...' });

        try {
            const order = await createOrderPayment(cart, { ...form, username });
            setStatus({
                type: 'success',
                message: `Siparis basariyla olusturuldu. Siparis No: ${order.orderId}`,
            });
            setTimeout(() => onPaymentSuccess?.(order), 1200);
        } catch (error) {
            const message = error.response?.data?.errorMessage || error.response?.data?.message || 'Odeme sirasinda bir hata olustu.';
            setStatus({ type: 'error', message });
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form className="payment-checkout" onSubmit={handleSubmit}>
            <h3>Odeme ve Teslimat</h3>
            <div className="payment-grid">
                <input name="firstName" placeholder="Ad" value={form.firstName} onChange={handleChange} required />
                <input name="lastName" placeholder="Soyad" value={form.lastName} onChange={handleChange} required />
                <input name="email" type="email" placeholder="E-posta" value={form.email} onChange={handleChange} required />
                <input name="phone" placeholder="Telefon" value={form.phone} onChange={handleChange} required />
                <input name="identityNumber" placeholder="TCKN" value={form.identityNumber} onChange={handleChange} required />
                <input name="city" placeholder="Sehir" value={form.city} onChange={handleChange} required />
                <input name="country" placeholder="Ulke" value={form.country} onChange={handleChange} required />
                <input name="zipCode" placeholder="Posta Kodu" value={form.zipCode} onChange={handleChange} />
            </div>
            <textarea
                name="streetAddress"
                placeholder="Teslimat adresi"
                value={form.streetAddress}
                onChange={handleChange}
                required
            />
            <div className="payment-grid">
                <input name="cardHolderName" placeholder="Kart Uzerindeki Ad" value={form.cardHolderName} onChange={handleChange} required />
                <input name="cardNumber" placeholder="Kart Numarasi" value={form.cardNumber} onChange={handleChange} required />
                <input name="expireMonth" placeholder="Ay" value={form.expireMonth} onChange={handleChange} required />
                <input name="expireYear" placeholder="Yil" value={form.expireYear} onChange={handleChange} required />
                <input name="cvc" placeholder="CVC" value={form.cvc} onChange={handleChange} required />
            </div>

            {status.message && (
                <div className={`payment-message ${status.type}`} role="status">
                    {status.message}
                </div>
            )}
            <button className="checkout-btn" type="submit" disabled={submitting}>
                {submitting ? 'Isleniyor...' : 'Siparisi Tamamla'}
            </button>
        </form>
    );
}

export default PaymentCheckout;
