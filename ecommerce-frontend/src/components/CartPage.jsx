import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './CartPage.css';
import PaymentCheckout from './PaymentCheckout';
import { getCart, removeCartItem, updateCartQuantity } from '../services/cartService';
import { getUnusedCoupons, getUnusedCouponsByUsername, previewCoupon } from '../services/couponService';

const CartPage = () => {
    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [couponCode, setCouponCode] = useState('');
    const [appliedCoupon, setAppliedCoupon] = useState(null);
    const [couponMessage, setCouponMessage] = useState('');
    const [activeCoupons, setActiveCoupons] = useState([]);
    const navigate = useNavigate();

    const getCurrentUserId = () => {
        const storedUserId = localStorage.getItem("kuba_user_id");
        if (storedUserId && storedUserId !== "undefined" && storedUserId !== "null") {
            return Number(storedUserId);
        }

        const storedUser = localStorage.getItem("kuba_user");
        if (!storedUser) {
            return null;
        }

        try {
            const user = JSON.parse(storedUser);
            return user?.id ? Number(user.id) : null;
        } catch {
            return null;
        }
    };

    useEffect(() => {
        fetchCart();
        fetchActiveCoupons();
    }, []);

    const fetchActiveCoupons = async () => {
        const userId = getCurrentUserId();
        const username = localStorage.getItem("kuba_username");

        if (!userId && !username) {
            setActiveCoupons([]);
            return;
        }

        try {
            const coupons = username
                ? await getUnusedCouponsByUsername(username)
                : await getUnusedCoupons(userId);
            setActiveCoupons(Array.isArray(coupons) ? coupons : []);
        } catch (error) {
            console.error("Aktif kuponlar yüklenemedi:", error);
            if (!userId) {
                setActiveCoupons([]);
                return;
            }

            try {
                const coupons = await getUnusedCoupons(userId);
                setActiveCoupons(Array.isArray(coupons) ? coupons : []);
            } catch (fallbackError) {
                console.error("Aktif kuponlar userId ile de yüklenemedi:", fallbackError);
                setActiveCoupons([]);
            }
        }
    };

    const renderActiveCoupons = () => (
        activeCoupons.length > 0 && (
            <div className="active-coupons-inline">
                <span className="active-coupons-title">Aktif Kuponlar</span>
                {activeCoupons.map((coupon) => (
                    <div className="active-coupon-row" key={coupon.id}>
                        <div>
                            <strong>{coupon.code}</strong>
                            <small>%20 indirim kuponu</small>
                        </div>
                        <button type="button" onClick={() => setCouponCode(coupon.code)}>
                            Kodu Kullan
                        </button>
                    </div>
                ))}
            </div>
        )
    );

    const handlePaymentSuccess = async () => {
        await fetchCart();
        await fetchActiveCoupons();
    };

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
            setAppliedCoupon(null);
            setCouponMessage('');
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

        const previousCart = cart; // Hata olursa eski sepeti geri alırız.
        const updatedItems = cart.items.map((item) =>
            item.productId === productId ? { ...item, quantity: newQuantity } : item
        );
        const updatedTotalPrice = updatedItems.reduce(
            (total, item) => total + (item.price * item.quantity),
            0
        );

        setCart({ ...cart, items: updatedItems, totalPrice: updatedTotalPrice });
        setAppliedCoupon(null); // Sepet değişince kupon hesabını sıfırla.
        setCouponMessage('Sepet değiştiği için kuponu tekrar uygulayabilirsiniz.');

        try {
            await updateCartQuantity(username, productId, newQuantity);
        } catch (error) {
            console.error("Miktar güncellenirken hata:", error);
            setCart(previousCart);
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

    const handleApplyCoupon = async () => {
        const userId = getCurrentUserId();

        if (!userId) {
            setCouponMessage('Kupon kullanmak için lütfen çıkış yapıp tekrar giriş yapın.');
            return;
        }

        if (!couponCode.trim()) {
            setCouponMessage('Lütfen kupon kodu girin.');
            return;
        }

        try {
            const coupon = await previewCoupon(userId, couponCode, cart.totalPrice);
            setAppliedCoupon(coupon);
            setCouponMessage(coupon.message || 'Kupon Başarıyla Uygulandı: %20 İndirim');
        } catch (error) {
            console.error("Kupon uygulanamadı:", error);
            setAppliedCoupon(null);
            setCouponMessage('Kupon kodu geçersiz veya daha önce kullanılmış.');
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
                {renderActiveCoupons()}
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
                    <div className="cart-promo-banner">
                        <div>
                            <span>Yeni Kampanya</span>
                            <h3>İlk kez 10.000 TL ve üzeri satın alımınızı tamamlayın, sonraki alışveriş için %20 kupon kazanın!</h3>
                        </div>
                        <strong>10.000 TL+</strong>
                    </div>

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
                        <span>-{(appliedCoupon?.discountAmount || 0).toLocaleString('tr-TR')} TL</span>
                    </div>
                    <hr className="summary-divider" />
                    <div className="summary-total">
                        <span>Toplam <small>KDV Dahil</small></span>
                        <span className="total-price">{(appliedCoupon?.discountedTotal || cart.totalPrice).toLocaleString('tr-TR')} TL</span>
                    </div>
                    <div className="coupon-box">
                        <label>Kupon Kodu Gir</label>
                        <div className="coupon-input-row">
                            <input
                                value={couponCode}
                                onChange={(event) => setCouponCode(event.target.value)}
                                placeholder="KUBA20-ABC123"
                            />
                            <button type="button" onClick={handleApplyCoupon}>Uygula</button>
                        </div>
                        {couponMessage && (
                            <div className={`coupon-message ${appliedCoupon ? 'success' : 'error'}`}>
                                {couponMessage}
                            </div>
                        )}
                        {renderActiveCoupons()}
                    </div>
                    <PaymentCheckout
                        cart={cart}
                        username={localStorage.getItem("kuba_username")}
                        userId={getCurrentUserId()}
                        couponCode={appliedCoupon ? couponCode : ''}
                        onPaymentSuccess={handlePaymentSuccess}
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
