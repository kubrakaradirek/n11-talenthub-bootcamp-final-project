import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CartPage.css';
import PaymentCheckout from './PaymentCheckout';
import { getCart, removeCartItem, updateCartQuantity } from '../services/cartService';

const CartPage = () => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchCart();
    }, []);

    const fetchCart = async () => {
        const username = localStorage.getItem("kuba_username");
        const token = localStorage.getItem("kuba_token");

        setLoading(true);
        setError('');

        if (!username || !token) {
            setLoading(false);
            return;
        }

        try {
            const data = await getCart(username);
            setCart(data);
        } catch (error) {
            console.error("Sepet yüklenemedi:", error);
            setError('Sepet bilgilerine şu an ulaşılamıyor, lütfen tekrar deneyin.');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdateQuantity = async (productId, currentQuantity, change) => {
        const username = localStorage.getItem("kuba_username");
        const newQuantity = currentQuantity + change;

        if (newQuantity < 1) return;

        try {
            await updateCartQuantity(username, productId, newQuantity);
            fetchCart();
        } catch (error) {
            console.error("Miktar güncellenirken hata:", error);
            setError('Ürün adedi güncellenemedi, lütfen tekrar deneyin.');
        }
    };

    const handleRemoveItem = async (productId) => {
        const username = localStorage.getItem("kuba_username");

        try {
            await removeCartItem(username, productId);
            fetchCart();
        } catch (error) {
            console.error("Ürün silinirken hata:", error);
            setError('Ürün sepetten silinemedi, lütfen tekrar deneyin.');
        }
    };

    if (loading) {
        return (
            <div className="cart-page-empty">
                <div className="lux-loader"></div>
                <h2>Sepetim yükleniyor...</h2>
                <p>Ürünlerin ve ödeme özeti hazırlanıyor.</p>
            </div>
        );
    }

    if (!localStorage.getItem("kuba_username")) {
        return (
            <div className="cart-page-empty">
                <span className="state-badge">KubaShop Hesabım</span>
                <h2>Sepetim</h2>
                <p>Sepetinizi görmek için lütfen giriş yapın.</p>
                <button onClick={() => navigate('/login')} className="checkout-btn">Giriş Yap</button>
            </div>
        );
    }

    if (error && (!cart || !cart.items)) {
        return (
            <div className="cart-page-empty error-state">
                <span className="state-badge">Sepet Hatası</span>
                <h2>{error}</h2>
                <button onClick={fetchCart} className="checkout-btn">Tekrar Dene</button>
            </div>
        );
    }

    if (!cart || !cart.items || cart.items.length === 0) {
        return (
            <div className="cart-page-empty">
                <span className="state-badge">Sepetim</span>
                <h2>Sepetiniz boş</h2>
                <p>Sepetinizde şu an ürün bulunmamaktadır. Hemen alışverişe başlayın.</p>
                <button onClick={() => navigate('/')} className="checkout-btn">Alışverişe Dön</button>
            </div>
        );
    }

    return (
        <div className="cart-page-wrapper">
            <div className="cart-hero">
                <span>KubaShop Güvencesi</span>
                <h1>Sepetim</h1>
                <p>Seçtiğiniz ürünleri güvenli ödeme ile tamamlayın.</p>
            </div>

            {error && <div className="cart-inline-error">{error}</div>}

            <div className="cart-page-container">
                <div className="cart-items-section">
                    <div className="cart-header-top">
                        <h2>Sepetim</h2>
                        <span className="cart-count">({cart.items.length} Ürün)</span>
                        <button onClick={() => navigate('/')} className="clear-all-btn">
                            Alışverişe Devam Et &gt;
                        </button>
                    </div>

                    <div className="cart-table-header">
                        <div className="th-product">ÜRÜN</div>
                        <div className="th-price">FİYAT</div>
                        <div className="th-quantity">ADET</div>
                        <div className="th-total">TOPLAM</div>
                    </div>

                    <div className="cart-items-list">
                        {cart.items.map((item) => (
                            <div key={item.productId} className="cart-item-row">
                                <div className="td-product">
                                    <img
                                        src={item.imageUrl || item.image || "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=150&q=80"}
                                        alt={item.title}
                                        className="product-cart-img"
                                    />
                                    <div className="product-info-col">
                                        <h3>{item.title}</h3>
                                        <span className="product-color">Renk: {item.color || "Standart"}</span>
                                        <button className="remove-item-btn" onClick={() => handleRemoveItem(item.productId)}>
                                            Sil
                                        </button>
                                    </div>
                                </div>

                                <div className="td-price">
                                    {item.price.toLocaleString('tr-TR')} TL
                                </div>

                                <div className="td-quantity">
                                    <div className="quantity-controls">
                                        <button onClick={() => handleUpdateQuantity(item.productId, item.quantity, -1)}>-</button>
                                        <span>{item.quantity}</span>
                                        <button onClick={() => handleUpdateQuantity(item.productId, item.quantity, 1)}>+</button>
                                    </div>
                                </div>

                                <div className="td-total">
                                    {(item.price * item.quantity).toLocaleString('tr-TR')} TL
                                </div>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="cart-summary-section">
                    <h3>Sipariş Özeti</h3>
                    <div className="summary-row">
                        <span>Ara Toplam</span>
                        <span>{cart.totalPrice.toLocaleString('tr-TR')} TL</span>
                    </div>
                    <div className="summary-row">
                        <span>Kargo Ücreti</span>
                        <span className="text-green">Ücretsiz</span>
                    </div>
                    <div className="summary-row discount">
                        <span>İndirim</span>
                        <span>-0,00 TL</span>
                    </div>
                    <hr className="summary-divider" />
                    <div className="summary-total">
                        <span>Toplam <small>KDV Dahil</small></span>
                        <span className="total-price">{cart.totalPrice.toLocaleString('tr-TR')} TL</span>
                    </div>
                    <PaymentCheckout
                        cart={cart}
                        username={localStorage.getItem("kuba_username")}
                        onPaymentSuccess={fetchCart}
                    />

                    <div className="payment-methods-container">
                        <p>Ödeme Yöntemleri</p>
                        <div className="payment-icons">
                            <span className="pay-badge visa">VISA</span>
                            <span className="pay-badge master">MasterCard</span>
                            <span className="pay-badge troy">troy</span>
                            <span className="pay-badge iyzico">iyzico <small>ile Öde</small></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CartPage;
