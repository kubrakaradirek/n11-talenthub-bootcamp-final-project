import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import ProductList from './components/ProductList';
import ProductDetail from './components/ProductDetail';
import Login from './components/Login';
import Register from './components/Register';
import CartPage from './components/CartPage';
import './App.css';

function App() {
    const [loggedInUser, setLoggedInUser] = useState(null);

    const checkUser = () => {
        const user = localStorage.getItem("kuba_username");
        if (user) {
            setLoggedInUser(user);
        } else {
            setLoggedInUser(null);
        }
    };

    useEffect(() => {
        checkUser();
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("kuba_token");
        localStorage.removeItem("kuba_username");
        setLoggedInUser(null);
        window.location.href = "/";
    };

    return (
        <Router>
            <div className="min-h-screen">
                <nav className="kubashop-navbar">
                    <div className="navbar-container">
                        <Link to="/" className="navbar-logo" onClick={checkUser}>
                            Kuba<span>Shop</span><span className="dot">.</span>
                        </Link>

                        <div className="navbar-account">
                            <div className="account-icon">KS</div>
                            <div className="account-links">
                                <span className="account-title">HESABIM</span>
                                <div className="auth-links">
                                    {loggedInUser ? (
                                        <>
                                            <Link to="/sepet" className="cart-nav-link">
                                                Sepetim
                                            </Link>

                                            <span className="welcome-user">
                                                HOŞGELDİN, {loggedInUser}
                                            </span>
                                            <span className="divider">|</span>
                                            <span onClick={handleLogout} className="logout-link">
                                                Çıkış Yap
                                            </span>
                                        </>
                                    ) : (
                                        <>
                                            <Link to="/register">Üye Ol</Link>
                                            <span className="divider">|</span>
                                            <Link to="/login">Giriş Yap</Link>
                                        </>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                </nav>

                <div className="w-full pb-10">
                    <Routes>
                        <Route path="/" element={<ProductList />} />
                        <Route path="/product/:id" element={<ProductDetail />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/register" element={<Register />} />
                        <Route path="/sepet" element={<CartPage />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
