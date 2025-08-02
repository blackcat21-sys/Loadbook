import React from 'react';
import { Routes, Route, Link } from 'react-router-dom';
import { Layout, Menu } from 'antd';
import { TruckOutlined, BookOutlined } from '@ant-design/icons';
import LoadManagement from './components/LoadManagement';
import BookingManagement from './components/BookingManagement';
import Dashboard from './components/Dashboard';

const { Header, Content } = Layout;

function App() {
  return (
    <div className="app-container">
      <Layout>
        <Header style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <h1 style={{ color: 'white', margin: 0, fontSize: '1.5rem' }}>
              Load & Booking Management System
            </h1>
            <Menu
              theme="dark"
              mode="horizontal"
              style={{ background: 'transparent', borderBottom: 'none' }}
            >
              <Menu.Item key="dashboard">
                <Link to="/">Dashboard</Link>
              </Menu.Item>
              <Menu.Item key="loads" icon={<TruckOutlined />}>
                <Link to="/loads">Loads</Link>
              </Menu.Item>
              <Menu.Item key="bookings" icon={<BookOutlined />}>
                <Link to="/bookings">Bookings</Link>
              </Menu.Item>
            </Menu>
          </div>
        </Header>
        <Content className="content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/loads" element={<LoadManagement />} />
            <Route path="/bookings" element={<BookingManagement />} />
          </Routes>
        </Content>
      </Layout>
    </div>
  );
}

export default App;