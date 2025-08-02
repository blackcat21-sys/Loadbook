import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.error('API Error:', error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Load API
export const loadAPI = {
  getLoads: (params = {}) => {
    const searchParams = new URLSearchParams();
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
        searchParams.append(key, params[key]);
      }
    });
    return api.get(`/load?${searchParams.toString()}`);
  },
  
  getLoadById: (id) => api.get(`/load/${id}`),
  
  createLoad: (loadData) => api.post('/load', loadData),
  
  updateLoad: (id, loadData) => api.put(`/load/${id}`, loadData),
  
  deleteLoad: (id) => api.delete(`/load/${id}`),
};

// Booking API
export const bookingAPI = {
  getBookings: (params = {}) => {
    const searchParams = new URLSearchParams();
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
        searchParams.append(key, params[key]);
      }
    });
    return api.get(`/booking?${searchParams.toString()}`);
  },
  
  getBookingById: (id) => api.get(`/booking/${id}`),
  
  createBooking: (bookingData) => api.post('/booking', bookingData),
  
  updateBooking: (id, bookingData) => api.put(`/booking/${id}`, bookingData),
  
  acceptBooking: (id) => api.put(`/booking/${id}/accept`),
  
  rejectBooking: (id) => api.put(`/booking/${id}/reject`),
  
  deleteBooking: (id) => api.delete(`/booking/${id}`),
};

export default api;