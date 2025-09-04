import React, { useState, useEffect } from 'react';
import { Search, MapPin, Phone, Building2 } from 'lucide-react';
import './HospitalDirectory.css';

const HospitalDirectory = () => {
  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [cityFilter, setCityFilter] = useState('');

  useEffect(() => {
    fetchHospitals();
  }, []);

  const fetchHospitals = async () => {
    try {
      const params = new URLSearchParams();
      if (searchTerm) params.append('name', searchTerm);
      if (cityFilter) params.append('city', cityFilter);

      const response = await fetch(`/api/v1/hospitals/search?${params}`);
      
      if (response.ok) {
        const data = await response.json();
        setHospitals(data);
      } else {
        console.error('Failed to fetch hospitals');
      }
    } catch (error) {
      console.error('Network error:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    setLoading(true);
    fetchHospitals();
  };

  const clearFilters = () => {
    setSearchTerm('');
    setCityFilter('');
    setLoading(true);
    fetchHospitals();
  };

  const HospitalCard = ({ hospital }) => (
    <div key={hospital.id} className="hospital-card">
      <div className="hospital-header">
        <Building2 className="hospital-icon" />
        <h3>{hospital.name}</h3>
      </div>
      
      <div className="hospital-details">
        <div className="detail-item">
          <MapPin />
          <div>
            <p className="label">Address</p>
            <p>{hospital.address}</p>
          </div>
        </div>
        
        <div className="detail-item">
          <MapPin />
          <div>
            <p className="label">City</p>
            <p>{hospital.city}</p>
          </div>
        </div>
        
        {hospital.phone && (
          <div className="detail-item">
            <Phone />
            <div>
              <p className="label">Phone</p>
              <p>{hospital.phone}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );

  return (
    <div className="hospital-directory">
      <div className="directory-header">
        <div className="header-content">
          <h1>Hospital Directory</h1>
          <p>Find hospitals and healthcare facilities near you</p>
        </div>
      </div>

      <div className="search-section">
        <form onSubmit={handleSearch} className="search-form">
          <div className="search-inputs">
            <div className="input-group">
              <Search />
              <input
                type="text"
                placeholder="Search by hospital name..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
            
            <div className="input-group">
              <MapPin />
              <input
                type="text"
                placeholder="Filter by city..."
                value={cityFilter}
                onChange={(e) => setCityFilter(e.target.value)}
              />
            </div>
          </div>
          
          <div className="search-actions">
            <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Searching...' : 'Search'}
            </button>
            <button type="button" className="btn btn-secondary" onClick={clearFilters}>
              Clear Filters
            </button>
          </div>
        </form>
      </div>

      <div className="hospitals-grid">
        {loading ? (
          <div className="loading">Loading hospitals...</div>
        ) : hospitals.length > 0 ? (
          hospitals.map(HospitalCard)
        ) : (
          <div className="empty-state">
            <Building2 />
            <h3>No hospitals found</h3>
            <p>Try adjusting your search criteria or browse all hospitals</p>
            <button className="btn btn-primary" onClick={clearFilters}>
              View All Hospitals
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default HospitalDirectory;
