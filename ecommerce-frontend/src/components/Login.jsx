import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './Auth.css';
import apiClient from '../services/apiClient';

function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    const handleLogin = async (e) => {
        e.preventDefault();
        setMessage({ text: 'Giriş yapılıyor, lütfen bekleyin...', type: 'loading' });

        try {
            localStorage.removeItem("kuba_token");
            const response = await apiClient.post('/api/user/signin', { username, password }, { skipAuth: true });

            if (response.status >= 200 && response.status < 300) {
                const data = response.data;

                // Giriş bilgisini tarayıcıda tutuyoruz.
                localStorage.setItem("kuba_token", data.accessToken);
                localStorage.setItem("kuba_username", data.username);
                localStorage.setItem("kuba_user_id", data.id);
                localStorage.setItem("kuba_user", JSON.stringify(data));

                setMessage({ text: "Giriş Başarılı! Anasayfaya yönlendiriliyorsunuz... 🚀", type: "success" });

                // Navbar güncellensin diye sayfayı ana ekrana alıyoruz.
                setTimeout(() => {
                    window.location.href = "/";
                }, 1500);
            }
        } catch (error) {
            console.error("Bağlantı hatası:", error);
            setMessage({ text: error.response ? "Hatalı Kullanıcı Adı veya Şifre! Lütfen tekrar dene." : "Sunucuya bağlanılamadı. API Gateway (8763) açık mı?", type: "error" });
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

                {/* Giriş mesajı */}
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

                    {/* Şifre alanı */}
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
