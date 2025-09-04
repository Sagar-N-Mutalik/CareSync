import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, Lock, Eye, EyeOff, Heart } from 'lucide-react';
import './Auth.css';

const Login = ({ onLogin, onSendOtp, loading, error }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [showOtpForm, setShowOtpForm] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);
    
    if (showOtpForm) {
      onSendOtp(data.email);
    } else {
      onLogin(data);
    }
  };

  const handleOtpClick = () => {
    const email = document.querySelector('input[name="email"]').value;
    if (email) {
      onSendOtp(email);
      setShowOtpForm(true);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="logo-section">
          <div className="logo">
            <Heart className="logo-icon" />
            <h1>Secured Health Records</h1>
          </div>
        </div>
        
        <div className="form-section">
          <div className="form-card">
            <div className="form-header">
              <h2>{showOtpForm ? 'Verify OTP' : 'Welcome Back'}</h2>
              <p>{showOtpForm ? 'Enter the OTP sent to your email' : 'Secure your medical records with us'}</p>
            </div>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit} className="auth-form">
              <div className="input-group">
                <Mail className="input-icon" />
                <input
                  type="email"
                  name="email"
                  placeholder="Email Address"
                  required
                />
              </div>

              {!showOtpForm && (
                <div className="input-group">
                  <Lock className="input-icon" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    name="password"
                    placeholder="Password"
                    required
                  />
                  <button
                    type="button"
                    className="password-toggle"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    {showPassword ? <EyeOff /> : <Eye />}
                  </button>
                </div>
              )}

              {showOtpForm && (
                <div className="input-group">
                  <Lock className="input-icon" />
                  <input
                    type="text"
                    name="otp"
                    placeholder="OTP Code"
                    maxLength="6"
                    required
                  />
                </div>
              )}

              <button type="submit" className="submit-btn" disabled={loading}>
                {loading ? <div className="loading"></div> : (showOtpForm ? 'Verify OTP' : 'Sign In')}
              </button>
            </form>

            <div className="form-footer">
              {!showOtpForm ? (
                <>
                  <p>
                    Don't have an account? <Link to="/signup">Sign Up</Link>
                  </p>
                  <button type="button" className="otp-btn" onClick={handleOtpClick}>
                    Sign in with OTP
                  </button>
                </>
              ) : (
                <button type="button" className="back-btn" onClick={() => setShowOtpForm(false)}>
                  Back to Login
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
