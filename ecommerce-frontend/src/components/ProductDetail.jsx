import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import './ProductDetail.css';
import Swal from 'sweetalert2';
import { addToCart } from '../services/cartService';
import { getProductById } from '../services/productService';

function ProductDetail() {
    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('aciklama');
    const navigate = useNavigate();

    useEffect(() => {
        setLoading(true);
        setError('');
        getProductById(id)
            .then(data => {
                setProduct(data);
            })
            .catch(error => {
                console.error("Hata:", error);
                setError('Ürün detaylarına şu an ulaşılamıyor, lütfen tekrar deneyin.');
            })
            .finally(() => {
                setLoading(false);
            });
    }, [id]);

    const Toast = Swal.mixin({
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
    });

    const handleAddToCart = async (e, productId, productTitle) => {
        if (e) e.preventDefault();

        const username = localStorage.getItem("kuba_username");
        const token = localStorage.getItem("kuba_token");

        if (!username || !token) {
            Swal.fire({
                icon: 'warning',
                title: 'Giriş Gerekli',
                text: 'Sepete ürün eklemek için lütfen giriş yapın!',
                confirmButtonColor: '#e11d2e'
            }).then(() => {
                navigate('/login');
            });
            return;
        }

        try {
            await addToCart(username, productId, 1);
            Toast.fire({
                icon: 'success',
                title: `${productTitle} sepete eklendi!`
            });
        } catch (error) {
            console.error("Sepete ekleme hatası:", error);
            Toast.fire({
                icon: 'error',
                title: 'Sunucuya bağlanılamadı.'
            });
        }
    };

    if (loading) {
        return (
            <div className="detail-state">
                <div className="lux-loader"></div>
                <h2>Ürün detayları hazırlanıyor...</h2>
                <p>Seçili ürün bilgileri yükleniyor.</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="detail-state error-state">
                <span className="state-badge">Ürün Hatası</span>
                <h2>{error}</h2>
                <button className="retry-btn" onClick={() => navigate('/')}>
                    Vitrine Dön
                </button>
            </div>
        );
    }

    if (!product) return <div className="error-text">Aradığınız ürün bulunamadı.</div>;

    return (
        <div className="detail-page-container">
            <div className="breadcrumb">
                <Link to="/">Anasayfa</Link> &gt; <span>Ev & Yaşam</span> &gt; <span>{product.category || 'Kategori'}</span> &gt; <span>{product.brand || 'Marka'}</span> &gt; <strong>{product.title}</strong>
            </div>

            <div className="product-top-row">
                <div className="product-image-gallery">
                    <div className="main-image-box">
                        <img
                            src={product.img}
                            alt={product.title}
                            onError={(e) => { e.target.src = 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?q=80&w=800'; }}
                        />
                    </div>
                    <div className="thumbnails">
                        <div className="thumb active"><img src={product.img} alt="thumb1" /></div>
                        <div className="thumb"><img src={product.img} alt="thumb2" /></div>
                    </div>
                </div>

                <div className="product-buy-info">
                    <span className="lux-label">KubaShop Özel Seçki</span>
                    <h1 className="detail-title">{product.title}</h1>

                    <div className="price-box">
                        <div className="old-price">{(product.price * 1.15).toFixed(0)} TL</div>
                        <div className="discount-label">KUBASHOP'TA SEPETTE</div>
                        <div className="current-price">{product.price.toLocaleString()} TL</div>
                    </div>

                    <div className="mini-specs">
                        <div className="spec-box">
                            <span className="spec-label">Marka</span>
                            <span className="spec-value">{product.brand || 'Belirtilmedi'} &gt;</span>
                        </div>
                        <div className="spec-box">
                            <span className="spec-label">Kategori</span>
                            <span className="spec-value">{product.category || 'Belirtilmedi'}</span>
                        </div>
                    </div>

                    <div className="color-selection">
                        <strong>Renk:</strong> <span>{product.color || 'Standart'}</span>
                        <div className="color-thumb">
                            <img src={product.img} alt="color-thumb" />
                        </div>
                    </div>

                    <button className="add-to-cart-mega" onClick={(e) => handleAddToCart(e, id, product.title)}>
                        Sepete Ekle
                    </button>

                    <div className="delivery-banner">
                        <div className="delivery-icon">K</div>
                        <div className="delivery-texts">
                            <strong>KubaShop Express - Ücretsiz Kargo</strong>
                            <span>Tahmini kargoya verilme: 2 gün içinde</span>
                        </div>
                        <div className="delivery-arrow">&gt;</div>
                    </div>
                </div>
            </div>

            <div className="product-bottom-row">
                <div className="tabs-header">
                    <div className={`tab-item ${activeTab === 'aciklama' ? 'active' : ''}`} onClick={() => setActiveTab('aciklama')}>
                        Ürün Açıklaması
                    </div>
                    <div className={`tab-item ${activeTab === 'iade' ? 'active' : ''}`} onClick={() => setActiveTab('iade')}>
                        İptal & İade Bilgileri
                    </div>
                    <div className={`tab-item ${activeTab === 'odeme' ? 'active' : ''}`} onClick={() => setActiveTab('odeme')}>
                        Ödeme Kolaylıkları
                    </div>
                </div>

                <div className="tab-content">
                    {activeTab === 'aciklama' && (
                        <div className="tab-pane">
                            <div className="desc-box">
                                <h3>Ürün Açıklaması</h3>
                                <h4>Ayrıntılar</h4>
                                <p>{product.description || 'Bu ürün için henüz detaylı bir açıklama girilmemiştir.'}</p>
                                <div className="publisher-note">
                                    <span>KubaShop</span> tarafından yayınlanmıştır.
                                </div>
                            </div>

                            <div className="tech-specs-box">
                                <div className="tech-header">
                                    <h3>Ürün Bilgileri</h3>
                                    <span className="more-info">Daha Fazla Bilgi &gt;</span>
                                </div>
                                <div className="tech-grid">
                                    <div className="tech-row">
                                        <span className="t-key">Renk</span>
                                        <span className="t-val">{product.color || 'Belirtilmedi'}</span>
                                    </div>
                                    <div className="tech-row">
                                        <span className="t-key">Kategori</span>
                                        <span className="t-val">{product.category || 'Belirtilmedi'}</span>
                                    </div>
                                    <div className="tech-row">
                                        <span className="t-key">Marka</span>
                                        <span className="t-val highlight">{product.brand || 'Belirtilmedi'}</span>
                                    </div>
                                    <div className="tech-row">
                                        <span className="t-key">Gönderici</span>
                                        <span className="t-val">KubaShop Express</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}

                    {activeTab === 'iade' && (
                        <div className="tab-pane">
                            <div className="desc-box">
                                <h3>İptal & İade Koşulları</h3>
                                <p>KubaShop güvencesiyle satın aldığınız ürünleri, teslimat tarihinden itibaren 15 gün içerisinde ücretsiz olarak iade edebilirsiniz.</p>
                            </div>
                        </div>
                    )}

                    {activeTab === 'odeme' && (
                        <div className="tab-pane">
                            <div className="desc-box">
                                <h3>Ödeme Kolaylıkları</h3>
                                <p>Tüm kredi kartlarına vade farksız 3 taksit, dilerseniz 12 aya varan taksit seçenekleriyle güvenle alışveriş yapabilirsiniz.</p>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ProductDetail;
