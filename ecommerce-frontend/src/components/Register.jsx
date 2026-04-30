import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Auth.css';

function Register() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [message, setMessage] = useState({ text: '', type: '' });

    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        setMessage({ text: 'Kayıt yapılıyor, lütfen bekleyin...', type: 'loading' });

        try {
            const response = await fetch('http://localhost:8763/api/user/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    email: email,
                    password: password
                })
            });

            if (response.ok) {
                setMessage({ text: "Kayıt Başarılı! Giriş sayfasına yönlendiriliyorsunuz... 🚀", type: "success" });

                setTimeout(() => {
                    navigate('/login');
                }, 2000);

            } else {
                const errorData = await response.json();
                setMessage({ text: errorData.message || "Kayıt olurken bir hata oluştu.", type: "error" });
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
                <p className="auth-welcome">Ayrıcalıklar dünyasına katılın 🚀</p>

                <div className="auth-tabs">
                    <Link to="/login" className="auth-tab">Giriş Yap</Link>
                    <Link to="/register" className="auth-tab active">Üye Ol</Link>
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

                <form onSubmit={handleRegister} className="auth-form">
                    <input
                        type="text"
                        placeholder="Kullanıcı Adı (Örn: kubra1)"
                        className="auth-input"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                    <input
                        type="email"
                        placeholder="E-posta Adresi"
                        className="auth-input"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
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

                    <div className="auth-checkboxes">
                        <label className="checkbox-label">
                            <input type="checkbox" required />
                            <span><a href="#">Üyelik Sözleşmesi</a> şartlarını okudum ve kabul ediyorum.</span>
                        </label>
                    </div>

                    <button type="submit" className="auth-submit-btn">Üye Ol</button>
                </form>
            </div>
        </div>
    );
}

export default Register;