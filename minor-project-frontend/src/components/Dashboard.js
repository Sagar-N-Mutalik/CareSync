import React, { useState, useEffect, useCallback } from 'react';
import { 
  Folder, File, Search, Plus, Upload, Share2, Trash2, Download, 
  Home, Building2, Users, Settings, LogOut, ChevronRight, MoreVertical 
} from 'lucide-react';
import FileUpload from './FileUpload';
import ShareModal from './ShareModal';
import HospitalDirectory from './HospitalDirectory';
import './Dashboard.css';

const Dashboard = ({ user, onLogout, onFileUpload, onFileShare }) => {
  const [currentView, setCurrentView] = useState('files');
  const [files, setFiles] = useState([]);
  const [currentPath, setCurrentPath] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [showUpload, setShowUpload] = useState(false);
  const [showShare, setShowShare] = useState(false);
  const [error, setError] = useState('');

  const navigationItems = [
    { id: 'files', icon: Home, label: 'My Files' },
    { id: 'hospitals', icon: Building2, label: 'Hospitals' },
    { id: 'shared', icon: Users, label: 'Shared' },
    { id: 'settings', icon: Settings, label: 'Settings' }
  ];

  useEffect(() => {
    if (currentView === 'files') {
      fetchFiles();
    }
  }, [currentView, currentPath, fetchFiles]);

  const fetchFiles = useCallback(async () => {
    try {
      const token = localStorage.getItem('token');
      const parentId = currentPath.length > 0 ? currentPath[currentPath.length - 1].id : null;
      
      const response = await fetch(`/api/v1/nodes?parentId=${parentId || ''}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (response.ok) {
        const data = await response.json();
        setFiles(data);
      } else {
        setError('Failed to fetch files');
      }
    } catch (error) {
      setError('Network error');
    } finally {
      setLoading(false);
    }
  }, [currentPath]);

  const handleFileAction = async (action, file) => {
    const token = localStorage.getItem('token');
    
    try {
      switch (action) {
        case 'open':
          if (file.type === 'FOLDER') {
            setCurrentPath([...currentPath, file]);
          } else {
            // Handle file download/view
            window.open(file.downloadUrl, '_blank');
          }
          break;
          
        case 'download':
          if (file.type === 'FILE') {
            window.open(file.downloadUrl, '_blank');
          }
          break;
          
        case 'share':
          setSelectedFile(file);
          setShowShare(true);
          break;
          
        case 'delete':
          if (window.confirm('Are you sure you want to delete this item?')) {
            const response = await fetch(`/api/v1/nodes/${file.id}`, {
              method: 'DELETE',
              headers: { 'Authorization': `Bearer ${token}` }
            });
            
            if (response.ok) {
              fetchFiles();
            } else {
              setError('Failed to delete file');
            }
          }
          break;
      }
    } catch (error) {
      setError('Action failed');
    }
  };

  const handleCreateFolder = async () => {
    const folderName = window.prompt('Enter folder name:');
    if (!folderName) return;

    try {
      const token = localStorage.getItem('token');
      const parentId = currentPath.length > 0 ? currentPath[currentPath.length - 1].id : null;
      
      const response = await fetch('/api/v1/nodes/folder', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          name: folderName,
          parentId: parentId
        })
      });

      if (response.ok) {
        fetchFiles();
      } else {
        setError('Failed to create folder');
      }
    } catch (error) {
      setError('Network error');
    }
  };

  const handleShare = async (shareData) => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch('/api/v1/share', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          recipientEmail: shareData.email,
          nodeIds: [selectedFile.id],
          accessDuration: shareData.duration
        })
      });

      if (response.ok) {
        const data = await response.json();
        onFileShare(data);
        setShowShare(false);
        setSelectedFile(null);
      } else {
        setError('Failed to share file');
      }
    } catch (error) {
      setError('Network error');
    }
  };

  const filteredFiles = files.filter(file => 
    file.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const renderFileCard = (file) => (
    <div key={file.id} className="file-card">
      <div className="file-icon">
        {file.type === 'FOLDER' ? <Folder /> : <File />}
      </div>
      <div className="file-info">
        <h4>{file.name}</h4>
        <p>{file.type === 'FILE' ? file.mimeType : `${file.childrenCount || 0} items`}</p>
      </div>
      <div className="file-actions">
        <button onClick={() => handleFileAction('open', file)} title="Open">
          {file.type === 'FOLDER' ? <ChevronRight /> : <Download />}
        </button>
        {file.type === 'FILE' && (
          <>
            <button onClick={() => handleFileAction('share', file)} title="Share">
              <Share2 />
            </button>
            <button onClick={() => handleFileAction('delete', file)} title="Delete">
              <Trash2 />
            </button>
          </>
        )}
      </div>
    </div>
  );

  const renderContent = () => {
    switch (currentView) {
      case 'files':
        return (
          <div className="content-main">
            <div className="content-header">
              <div className="breadcrumb">
                <button onClick={() => setCurrentPath([])}>Home</button>
                {currentPath.map((item, index) => (
                  <React.Fragment key={item.id}>
                    <ChevronRight />
                    <button onClick={() => setCurrentPath(currentPath.slice(0, index + 1))}>
                      {item.name}
                    </button>
                  </React.Fragment>
                ))}
              </div>
              
              <div className="search-box">
                <Search />
                <input
                  type="text"
                  placeholder="Search files..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
              
              <div className="actions">
                <button onClick={handleCreateFolder} className="btn">
                  <Plus /> New Folder
                </button>
                <button onClick={() => setShowUpload(true)} className="btn">
                  <Upload /> Upload
                </button>
              </div>
            </div>

            {error && <div className="error-message">{error}</div>}
            
            {loading ? (
              <div className="loading">Loading...</div>
            ) : (
              <div className="files-grid">
                {filteredFiles.length > 0 ? (
                  filteredFiles.map(renderFileCard)
                ) : (
                  <div className="empty-state">
                    <Folder />
                    <h3>No files found</h3>
                    <p>Upload your first file or create a folder to get started</p>
                  </div>
                )}
              </div>
            )}
          </div>
        );
        
      case 'hospitals':
        return <HospitalDirectory />;
        
      case 'shared':
        return (
          <div className="content-main">
            <h2>Shared Files</h2>
            <p>View files shared with you and manage your shared files</p>
          </div>
        );
        
      case 'settings':
        return (
          <div className="content-main">
            <h2>Settings</h2>
            <p>Manage your account settings and preferences</p>
          </div>
        );
        
      default:
        return null;
    }
  };

  return (
    <div className="dashboard">
      <div className="sidebar">
        <div className="user-info">
          <div className="user-avatar">
            {user.fullName.charAt(0).toUpperCase()}
          </div>
          <div className="user-details">
            <h3>{user.fullName}</h3>
            <p>{user.email}</p>
          </div>
        </div>

        <nav className="navigation">
          {navigationItems.map(item => (
            <button
              key={item.id}
              className={`nav-item ${currentView === item.id ? 'active' : ''}`}
              onClick={() => setCurrentView(item.id)}
            >
              <item.icon />
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        <button className="logout-btn" onClick={onLogout}>
          <LogOut />
          <span>Logout</span>
        </button>
      </div>

      <div className="main-content">
        {renderContent()}
      </div>

      {showUpload && (
        <FileUpload
          onClose={() => setShowUpload(false)}
          onUpload={() => {
            setShowUpload(false);
            onFileUpload();
          }}
          currentPath={currentPath}
        />
      )}

      {showShare && selectedFile && (
        <ShareModal
          file={selectedFile}
          onClose={() => {
            setShowShare(false);
            setSelectedFile(null);
          }}
          onShare={handleShare}
        />
      )}
    </div>
  );
};

export default Dashboard;
