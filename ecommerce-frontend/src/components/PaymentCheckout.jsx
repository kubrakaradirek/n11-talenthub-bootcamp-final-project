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

function PaymentCheckout({ cart, username, userId, couponCode, onPaymentSuccess }) {
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
            setStatus({ type: 'error', message: 'Sepetiniz boş. Sipariş oluşturulamaz.' });
            return;
        }

        if (!userId) {
            setStatus({ type: 'error', message: 'Kullanıcı id bulunamadı. Lütfen çıkış yapıp tekrar giriş yapın.' });
            return;
        }

        setSubmitting(true);
        setStatus({ type: 'loading', message: 'Ödeme işlemi başlatılıyor...' });

        try {
            const order = await createOrderPayment(cart, { ...form, username, userId, couponCode });
            setStatus({
                type: 'success',
                message: `Sipariş başarıyla oluşturuldu. Sipariş No: ${order.orderId}`,
            });
            setTimeout(() => onPaymentSuccess?.(order), 1200);
        } catch (error) {
            const message =
                error.response?.data?.errorMessage ||
                error.response?.data?.message ||
                error.response?.data?.mesaj ||
                error.message ||
                'Ödeme sırasında bir hata oluştu.';
            setStatus({ type: 'error', message });
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <form className="payment-checkout" onSubmit={handleSubmit}>
            <h3>Ödeme ve Teslimat</h3>
            <div className="payment-grid">
                <input name="firstName" placeholder="Ad" value={form.firstName} onChange={handleChange} required />
                <input name="lastName" placeholder="Soyad" value={form.lastName} onChange={handleChange} required />
                <input name="email" type="email" placeholder="E-posta" value={form.email} onChange={handleChange} required />
                <input name="phone" placeholder="Telefon" value={form.phone} onChange={handleChange} required />
                <input name="identityNumber" placeholder="TCKN" value={form.identityNumber} onChange={handleChange} required />
                <input name="city" placeholder="Şehir" value={form.city} onChange={handleChange} required />
                <input name="country" placeholder="Ülke" value={form.country} onChange={handleChange} required />
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
                <input name="cardHolderName" placeholder="Kart Üzerindeki Ad" value={form.cardHolderName} onChange={handleChange} required />
                <input name="cardNumber" placeholder="Kart Numarası" value={form.cardNumber} onChange={handleChange} required />
                <input name="expireMonth" placeholder="Ay" value={form.expireMonth} onChange={handleChange} required />
                <input name="expireYear" placeholder="Yıl" value={form.expireYear} onChange={handleChange} required />
                <input name="cvc" placeholder="CVC" value={form.cvc} onChange={handleChange} required />
            </div>

            {status.message && (
                <div className={`payment-message ${status.type}`} role="status">
                    {status.message}
                </div>
            )}
            <button className="checkout-btn" type="submit" disabled={submitting}>
                {submitting ? 'İşleniyor...' : 'Siparişi Tamamla'}
            </button>
        </form>
    );
}

export default PaymentCheckout;
