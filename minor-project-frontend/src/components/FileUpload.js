import React, { useState, useRef } from 'react';
import { Upload, X, File, CheckCircle, AlertCircle, Loader } from 'lucide-react';
import './FileUpload.css';

const FileUpload = ({ onClose, onUpload, currentPath }) => {
  const [files, setFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef();

  const handleFileSelect = (selectedFiles) => {
    const newFiles = Array.from(selectedFiles).map(file => ({
      file,
      id: Math.random().toString(36).substr(2, 9),
      name: file.name,
      size: file.size,
      status: 'pending'
    }));
    setFiles(prev => [...prev, ...newFiles]);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    const droppedFiles = e.dataTransfer.files;
    handleFileSelect(droppedFiles);
  };

  const handleDragOver = (e) => e.preventDefault();

  const removeFile = (fileId) => {
    setFiles(prev => prev.filter(f => f.id !== fileId));
  };

  const uploadFiles = async () => {
    if (files.length === 0) return;

    setUploading(true);
    const token = localStorage.getItem('token');
    const parentId = currentPath.length > 0 ? currentPath[currentPath.length - 1].id : null;

    for (let fileData of files) {
      try {
        setFiles(prev => prev.map(f => 
          f.id === fileData.id ? { ...f, status: 'uploading' } : f
        ));

        const formData = new FormData();
        formData.append('file', fileData.file);
        if (parentId) formData.append('parentId', parentId);

        const response = await fetch('/api/v1/nodes/file', {
          method: 'POST',
          headers: { 'Authorization': `Bearer ${token}` },
          body: formData
        });

        if (response.ok) {
          setFiles(prev => prev.map(f => 
            f.id === fileData.id ? { ...f, status: 'success' } : f
          ));
        } else {
          setFiles(prev => prev.map(f => 
            f.id === fileData.id ? { ...f, status: 'error' } : f
          ));
        }
      } catch (error) {
        setFiles(prev => prev.map(f => 
          f.id === fileData.id ? { ...f, status: 'error' } : f
        ));
      }
    }

    setUploading(false);
    setTimeout(() => {
      onUpload();
    }, 1000);
  };

  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'success': return <CheckCircle className="status-icon success" />;
      case 'error': return <AlertCircle className="status-icon error" />;
      case 'uploading': return <Loader className="status-icon uploading" />;
      default: return null;
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Upload Files</h2>
          <button className="close-btn" onClick={onClose}>
            <X />
          </button>
        </div>

        <div className="modal-body">
          <div 
            className="drop-zone"
            onDrop={handleDrop}
            onDragOver={handleDragOver}
            onClick={() => fileInputRef.current?.click()}
          >
            <Upload className="upload-icon" />
            <p>Drag and drop files here or click to browse</p>
            <input
              ref={fileInputRef}
              type="file"
              multiple
              onChange={(e) => handleFileSelect(e.target.files)}
              style={{ display: 'none' }}
            />
          </div>

          {files.length > 0 && (
            <div className="file-list">
              <h3>Selected Files ({files.length})</h3>
              {files.map(fileData => (
                <div key={fileData.id} className="file-item">
                  <div className="file-info">
                    <File className="file-icon" />
                    <div>
                      <p className="file-name">{fileData.name}</p>
                      <p className="file-size">{formatFileSize(fileData.size)}</p>
                    </div>
                  </div>
                  <div className="file-actions">
                    {getStatusIcon(fileData.status)}
                    <button 
                      className="remove-btn"
                      onClick={() => removeFile(fileData.id)}
                      disabled={fileData.status === 'uploading'}
                    >
                      <X />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose}>
            Cancel
          </button>
          <button 
            className="btn btn-primary"
            onClick={uploadFiles}
            disabled={files.length === 0 || uploading}
          >
            {uploading ? 'Uploading...' : 'Upload Files'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default FileUpload;
