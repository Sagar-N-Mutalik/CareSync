import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Signup from './components/Signup';
import Dashboard from './components/Dashboard';
import './App.css';

const App = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [authError, setAuthError] = useState('');

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      try { setUser(JSON.parse(userData)); } catch (_) { localStorage.removeItem('token'); localStorage.removeItem('user'); }
    }
    setLoading(false);
  }, []);

  const apiCall = async (endpoint, data) => {
    const response = await fetch(`/api/v1/auth/${endpoint}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(data) });
    return response.json();
  };

  const handleAuth = async (action, formData) => {
    setAuthError('');
    try {
      let data;
      switch (action) {
        case 'login': data = await apiCall('login', { email: formData.email, password: formData.password }); break;
        case 'register': data = await apiCall('register', { fullName: formData.fullName, email: formData.email, password: formData.password }); break;
        case 'sendOtp': data = await apiCall('send-otp', { email: formData }); break;
        case 'verifyOtp': data = await apiCall('verify-otp', { email: formData.email, otp: formData.otp, fullName: formData.fullName, password: formData.password }); break;
        default: data = {}; break;
      }
      if (data.token) {
        const userData = { id: data.userId, email: data.email, fullName: data.fullName };
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(userData));
        setUser(userData);
      } else if (data.message) {
        setAuthError(data.message);
      }
    } catch (e) {
      setAuthError('Network error. Please try again.');
    }
  };

  const handleLogin = (formData) => handleAuth('login', formData);
  const handleRegister = (formData) => handleAuth('register', formData);
  const handleSendOtp = (email) => handleAuth('sendOtp', email);
  const handleVerifyOtp = (formData) => handleAuth('verifyOtp', formData);

  const handleLogout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); setUser(null); };
  const handleFileUpload = () => window.location.reload();
  const handleFileShare = (shareData) => {
    // File sharing functionality will be implemented
    // TODO: Implement file sharing logic
  };

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loading"></div>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={<Navigate to={user ? '/dashboard' : '/login'} replace />} />
          <Route path="/login" element={ user ? <Navigate to="/dashboard" replace /> : (
            <Login onLogin={handleLogin} onSendOtp={handleSendOtp} loading={loading} error={authError} />
          ) } />
          <Route path="/signup" element={ user ? <Navigate to="/dashboard" replace /> : (
            <Signup onRegister={handleRegister} loading={loading} error={authError} />
          ) } />
          <Route path="/dashboard" element={ user ? (
            <Dashboard user={user} onLogout={handleLogout} onFileUpload={handleFileUpload} onFileShare={handleFileShare} />
          ) : (<Navigate to="/login" replace />) } />
          <Route path="/share/:token" element={<div className="share-view"><h2>Shared Medical Records</h2><p>This feature will be implemented to view shared files.</p></div>} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
