import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './ProductList.css';
import Swal from 'sweetalert2';
import { addToCart } from '../services/cartService';
import { getProducts } from '../services/productService';

function ProductList() {
    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        setLoading(true);
        setError('');
        getProducts(currentPage, 8)
            .then(data => {
                const result = data.content || (Array.isArray(data) ? data : []);
                setProducts(result);
                setTotalPages(data.totalPages || 1);
            })
            .catch(error => {
                console.error("Veri çekme hatası:", error);
                setError('Şu an ürünlere ulaşılamıyor, lütfen tekrar deneyin.');
            })
            .finally(() => {
                setLoading(false);
            });
    }, [currentPage]);

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
            <div className="lux-state">
                <div className="lux-loader"></div>
                <h2>KubaShop hazırlanıyor...</h2>
                <p>Seçili ürünler senin için getiriliyor.</p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="lux-state error-state">
                <span className="state-badge">Bağlantı Hatası</span>
                <h2>{error}</h2>
                <button className="retry-btn" onClick={() => window.location.reload()}>
                    Tekrar Dene
                </button>
            </div>
        );
    }

    return (
        <div className="home-container">
            <section className="hero-slider">
                <div className="hero-slide hero-slide-one"></div>
                <div className="hero-slide hero-slide-two"></div>
                <div className="hero-slide hero-slide-three"></div>
                <div className="hero-overlay">
                    <span className="hero-eyebrow">Premium Alışveriş Deneyimi</span>
                    <h1>Kuba<span>Shop</span></h1>
                    <p>Teknoloji, ev yaşamı ve seçili markalarda lüks alışveriş hissi.</p>
                    <button className="hero-cta" onClick={() => window.scrollTo({ top: 420, behavior: 'smooth' })}>
                        Ürünleri İncele
                    </button>
                </div>
            </section>

            <div className="home-banner">
                <h2>Eviniz İçin En İyisi</h2>
                <p>KubaShop güvencesiyle incele ve anında satın al</p>
            </div>

            <div className="product-grid">
                {products.map((product) => {
                    const stars = "★".repeat(Math.floor(Math.random() * 2) + 4);

                    return (
                        <Link to={`/product/${product.id}`} key={product.id} className="product-card-link">
                            <div className="product-card">
                                <div className="image-wrapper">
                                    <img
                                        src={product.img}
                                        alt={product.title}
                                        onError={(e) => {
                                            e.target.src = 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?q=80&w=500';
                                        }}
                                    />
                                </div>
                                <div className="product-details">
                                    <h3 className="product-name">{product.title}</h3>
                                    <div className="product-rating">{stars}</div>
                                    <div className="price-container">
                                        <span className="old-price">{(product.price * 1.15).toFixed(0)} TL</span>
                                        <span className="current-price">{product.price.toLocaleString()} TL</span>
                                    </div>
                                    <span className="shipping-badge">Kargo Bedava</span>

                                    <button className="buy-btn" onClick={(e) => handleAddToCart(e, product.id, product.title)}>
                                        Sepete Ekle
                                    </button>
                                </div>
                            </div>
                        </Link>
                    );
                })}
            </div>

            <div className="pagination-wrapper">
                <button
                    onClick={() => setCurrentPage(prev => Math.max(prev - 1, 0))}
                    disabled={currentPage === 0}
                    className="page-btn">
                    Önceki
                </button>

                <span className="page-info">
                    Sayfa <strong>{currentPage + 1}</strong> / {totalPages}
                </span>

                <button
                    onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages - 1))}
                    disabled={currentPage === totalPages - 1}
                    className="page-btn">
                    Sonraki
                </button>
            </div>
        </div>
    );
}

export default ProductList;
