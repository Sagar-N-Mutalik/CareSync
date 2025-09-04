import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { User, Mail, Lock, Eye, EyeOff, Heart, Calendar } from 'lucide-react';
import './Auth.css';

const Signup = ({ onRegister, loading, error }) => {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState({});

  const validatePassword = (password) => {
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumbers = /\d/.test(password);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
    const isLongEnough = password.length >= 8;
    
    return { hasUpperCase, hasLowerCase, hasNumbers, hasSpecialChar, isLongEnough };
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData);
    
    const newErrors = {};
    
    if (!data.fullName.trim()) newErrors.fullName = 'Full name is required';
    if (!data.email.trim()) newErrors.email = 'Email is required';
    if (!data.dob) newErrors.dob = 'Date of birth is required';
    if (!data.gender) newErrors.gender = 'Gender is required';
    if (!data.termsAccepted) newErrors.termsAccepted = 'You must agree to the terms';
    
    const passwordValidation = validatePassword(data.password);
    if (!passwordValidation.isLongEnough || !passwordValidation.hasUpperCase || 
        !passwordValidation.hasNumbers || !passwordValidation.hasSpecialChar) {
      newErrors.password = 'Password must have 8 characters, a capital letter, a special character and a number';
    }
    
    if (data.password !== data.confirmPassword) {
      newErrors.confirmPassword = 'Passwords do not match';
    }
    
    setErrors(newErrors);
    
    if (Object.keys(newErrors).length === 0) {
      onRegister(data);
    }
  };

  const handlePasswordChange = (e) => {
    const password = e.target.value;
    const validation = validatePassword(password);
    
    if (password.length > 0 && (!validation.isLongEnough || !validation.hasUpperCase || 
        !validation.hasNumbers || !validation.hasSpecialChar)) {
      setErrors(prev => ({ ...prev, password: 'Password must have 8 characters, a capital letter, a special character and a number' }));
    } else {
      setErrors(prev => ({ ...prev, password: '' }));
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
          <div className="form-card signup-card">
            <div className="form-header">
              <h2>Create Account</h2>
              <p>Join us to secure your medical records</p>
            </div>

            {error && <div className="error-message">{error}</div>}

            <form onSubmit={handleSubmit} className="auth-form signup-form">
              <div className="input-group">
                <User className="input-icon" />
                <input
                  type="text"
                  name="fullName"
                  placeholder="Full Name"
                  required
                />
                {errors.fullName && <span className="field-error">{errors.fullName}</span>}
              </div>

              <div className="input-group">
                <Mail className="input-icon" />
                <input
                  type="email"
                  name="email"
                  placeholder="Email Address"
                  required
                />
                {errors.email && <span className="field-error">{errors.email}</span>}
              </div>

              <div className="input-group">
                <Calendar className="input-icon" />
                <input
                  type="date"
                  name="dob"
                  required
                />
                {errors.dob && <span className="field-error">{errors.dob}</span>}
              </div>

              <div className="input-group">
                <label className="radio-label">Gender</label>
                <div className="radio-row">
                  <label>
                    <input type="radio" name="gender" value="male" />
                    Male
                  </label>
                  <label>
                    <input type="radio" name="gender" value="female" />
                    Female
                  </label>
                  <label>
                    <input type="radio" name="gender" value="other" />
                    Other
                  </label>
                </div>
                {errors.gender && <span className="field-error">{errors.gender}</span>}
              </div>

              <div className="input-group">
                <Lock className="input-icon" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  placeholder="Password"
                  onChange={handlePasswordChange}
                  required
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff /> : <Eye />}
                </button>
                {errors.password && <span className="field-error">{errors.password}</span>}
              </div>

              <div className="input-group">
                <Lock className="input-icon" />
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  name="confirmPassword"
                  placeholder="Confirm Password"
                  required
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                >
                  {showConfirmPassword ? <EyeOff /> : <Eye />}
                </button>
                {errors.confirmPassword && <span className="field-error">{errors.confirmPassword}</span>}
              </div>

              <div className="input-group">
                <label className="terms-row">
                  <input type="checkbox" name="termsAccepted" required />
                  <span>I agree to the terms of services and privacy policy</span>
                </label>
                {errors.termsAccepted && <span className="field-error">{errors.termsAccepted}</span>}
              </div>

              <button type="submit" className="submit-btn" disabled={loading}>
                {loading ? <div className="loading"></div> : 'Create Account'}
              </button>
            </form>

            <div className="form-footer">
              <p>
                Already have an account? <Link to="/login">Sign In</Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Signup;
