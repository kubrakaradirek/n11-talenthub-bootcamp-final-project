import React, { useState } from 'react';
import { Link } from 'react-router-dom'; // Sadece Link kalmalı, sayfanın görünmesi için şart!
import './Auth.css';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage({ text: 'Giriş yapılıyor, lütfen bekleyin...', type: 'loading' });

        try {
            const response = await fetch('http://localhost:8763/api/user/signin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username: username, password: password })
            });

            if (response.ok) {
                const data = await response.json();

                // Başarılı girişte bilgileri localStorage'a kaydet
                localStorage.setItem("kuba_token", data.accessToken);
                localStorage.setItem("kuba_username", data.username);

                setMessage({ text: "Giriş Başarılı! Anasayfaya yönlendiriliyorsunuz... 🚀", type: "success" });

                // Navbar'ın kullanıcı adını okuyabilmesi için sayfayı href ile yönlendiriyoruz
                setTimeout(() => {
                    window.location.href = "/";
                }, 1500);

            } else {
                setMessage({ text: "Hatalı Kullanıcı Adı veya Şifre! Lütfen tekrar dene.", type: "error" });
            }
        } catch (error) {
            console.error("Bağlantı hatası:", error);
            setMessage({ text: "Sunucuya bağlanılamadı. API Gateway (8763) açık mı?", type: "error" });
        }
    };

    return (
        <div className="auth-container">
            <div className="auth-card">
                <div className="auth-logo">
                    Kuba<span>Shop</span><span className="dot">.</span>
                </div>
                <p className="auth-welcome">KubaShop dünyasına hoş geldiniz 👋</p>

                <div className="auth-tabs">
                    <Link to="/login" className="auth-tab active">Giriş Yap</Link>
                    <Link to="/register" className="auth-tab">Üye Ol</Link>
                </div>

                {/* Mesaj Gösterim Alanı */}
                {message.text && (
                    <div style={{
                        padding: '10px',
                        marginBottom: '15px',
                        borderRadius: '8px',
                        textAlign: 'center',
                        fontWeight: '600',
                        fontSize: '14px',
                        backgroundColor: message.type === 'error' ? '#ffeaa7' : message.type === 'success' ? '#55efc4' : '#81ecec',
                        color: '#2d3436'
                    }}>
                        {message.text}
                    </div>
                )}

                <form onSubmit={handleLogin} className="auth-form">
                    <input
                        type="text"
                        placeholder="Kullanıcı Adı"
                        className="auth-input"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />

                    {/* Şifre ve Göz İkonu */}
                    <div className="password-wrapper">
                        <input
                            type={showPassword ? "text" : "password"}
                            placeholder="Şifre"
                            className="auth-input"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                        <button
                            type="button"
                            className="password-toggle-btn"
                            onClick={() => setShowPassword(!showPassword)}
                        >
                            {showPassword ? "👁️" : "🙈"}
                        </button>
                    </div>

                    <button type="submit" className="auth-submit-btn">Giriş Yap</button>
                </form>

                <div className="auth-footer">
                    <p>Yardıma mı ihtiyacın var?</p>
                    <p><strong>000 000 00 00</strong> numarası üzerinden Müşteri Hizmetleri'ni arayabilirsin.</p>
                </div>
            </div>
        </div>
    );
}

export default Login;