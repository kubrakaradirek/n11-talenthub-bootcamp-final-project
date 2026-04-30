import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './ProductList.css';

function ProductList() {
    const [products, setProducts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setLoading(true);
        fetch(`http://localhost:8763/api/products?page=${currentPage}&size=8`)
            .then(response => response.json())
            .then(data => {
                const result = data.content || (Array.isArray(data) ? data : []);
                setProducts(result);
                setTotalPages(data.totalPages || 1);
                setLoading(false);
            })
            .catch(error => {
                console.error("Veri çekme hatası:", error);
                setLoading(false);
            });
    }, [currentPage]);

    if (loading) return <div className="loading-text">KubaShop Yükleniyor... 🚀</div>;

    return (
        <div className="home-container">
            {/* Çift logoyu kaldırdık, yerine şık bir karşılama afişi ekledik */}
            <div className="home-banner">
                <h2>Eviniz İçin En İyisi</h2>
                <p>KubaShop Güvencesiyle İncele ve Anında Satın Al</p>
            </div>

            <div className="product-grid">
                {products.map((product) => {
                    const stars = "⭐".repeat(Math.floor(Math.random() * 2) + 4);

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
                                    <button className="buy-btn">Sepete Ekle</button>
                                </div>
                            </div>
                        </Link>
                    );
                })}
            </div>

            {/* Sayfalama (Pagination) */}
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