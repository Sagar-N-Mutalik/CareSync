import React, { useState } from 'react';
import { X, Copy, CheckCircle, Shield, Clock, Mail } from 'lucide-react';
import './ShareModal.css';

const ShareModal = ({ file, onClose, onShare }) => {
  const [formData, setFormData] = useState({
    email: '',
    duration: '24'
  });
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [shareLink, setShareLink] = useState('');
  const [copied, setCopied] = useState(false);

  const durationOptions = [
    { value: '1', label: '1 hour' },
    { value: '24', label: '24 hours' },
    { value: '168', label: '1 week' },
    { value: '720', label: '1 month' }
  ];

  const handleInputChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await onShare(formData);
      setSuccess(true);
      setShareLink(`${window.location.origin}/share/token-here`);
    } catch (error) {
      console.error('Share error:', error);
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(shareLink);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (error) {
      console.error('Copy failed:', error);
    }
  };

  const resetForm = () => {
    setFormData({ email: '', duration: '24' });
    setSuccess(false);
    setShareLink('');
    setCopied(false);
  };

  if (success) {
    return (
      <div className="modal-overlay" onClick={onClose}>
        <div className="modal-content success" onClick={e => e.stopPropagation()}>
          <div className="modal-header">
            <h2>File Shared Successfully!</h2>
            <button className="close-btn" onClick={onClose}>
              <X />
            </button>
          </div>

          <div className="modal-body">
            <div className="success-message">
              <CheckCircle className="success-icon" />
              <h3>Your file has been shared</h3>
              <p>The recipient will receive an email with access instructions.</p>
            </div>

            <div className="share-link-section">
              <label>Share Link:</label>
              <div className="link-container">
                <input
                  type="text"
                  value={shareLink}
                  readOnly
                  className="share-link"
                />
                <button 
                  className="copy-btn"
                  onClick={copyToClipboard}
                  title="Copy to clipboard"
                >
                  {copied ? <CheckCircle /> : <Copy />}
                </button>
              </div>
              {copied && <span className="copied-text">Copied to clipboard!</span>}
            </div>

            <div className="security-notice">
              <Shield />
              <div>
                <h4>Security Features</h4>
                <ul>
                  <li>Time-limited access (expires in {durationOptions.find(d => d.value === formData.duration)?.label})</li>
                  <li>Secure token-based access</li>
                  <li>Recipient email verification</li>
                </ul>
              </div>
            </div>
          </div>

          <div className="modal-footer">
            <button className="btn btn-secondary" onClick={resetForm}>
              Share Another File
            </button>
            <button className="btn btn-primary" onClick={onClose}>
              Done
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Share File</h2>
          <button className="close-btn" onClick={onClose}>
            <X />
          </button>
        </div>

        <div className="modal-body">
          <div className="file-preview">
            <div className="file-icon">
              {file.type === 'FOLDER' ? '📁' : '📄'}
            </div>
            <div className="file-details">
              <h3>{file.name}</h3>
              <p>{file.type === 'FILE' ? file.mimeType : 'Folder'}</p>
            </div>
          </div>

          <form onSubmit={handleSubmit} className="share-form">
            <div className="form-group">
              <label>
                <Mail />
                Recipient Email
              </label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                placeholder="Enter recipient's email address"
                required
              />
            </div>

            <div className="form-group">
              <label>
                <Clock />
                Access Duration
              </label>
              <select
                name="duration"
                value={formData.duration}
                onChange={handleInputChange}
              >
                {durationOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>

            <div className="security-notice">
              <Shield />
              <div>
                <h4>Security Notice</h4>
                <p>This file will be shared securely with time-limited access. The recipient will receive an email with a secure link to access the file.</p>
              </div>
            </div>
          </form>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>
            Cancel
          </button>
          <button 
            className="btn btn-primary"
            onClick={handleSubmit}
            disabled={loading || !formData.email}
          >
            {loading ? 'Sharing...' : 'Share File'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ShareModal;
