import React from 'react';
import { Card, Row, Col, Statistic, Typography } from 'antd';
import { TruckOutlined, BookOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const { Title } = Typography;

const Dashboard = () => {
  return (
    <div>
      <Title level={2}>Dashboard</Title>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Total Loads"
              value={1}
              prefix={<TruckOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Active Bookings"
              value={0}
              prefix={<BookOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Completed"
              value={0}
              prefix={<CheckCircleOutlined />}
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card>
            <Statistic
              title="Cancelled"
              value={0}
              prefix={<CloseCircleOutlined />}
              valueStyle={{ color: '#cf1322' }}
            />
          </Card>
        </Col>
      </Row>
      
      <Card style={{ marginTop: 24 }}>
        <Title level={3}>Welcome to Load & Booking Management System</Title>
        <p>
          This system helps you manage cargo loads and transportation bookings efficiently. 
          Use the navigation menu above to:
        </p>
        <ul>
          <li><strong>Loads:</strong> Create, view, and manage cargo loads</li>
          <li><strong>Bookings:</strong> Handle booking requests and status updates</li>
        </ul>
        
        <Title level={4}>Quick Start Guide:</Title>
        <ol>
          <li>Create a new load by clicking on "Loads" and then "Add New Load"</li>
          <li>Fill in the load details including pickup and delivery information</li>
          <li>Transporters can create bookings for available loads</li>
          <li>Accept or reject booking requests as needed</li>
        </ol>
        
        <p>
          The system automatically manages load statuses based on booking activities:
        </p>
        <ul>
          <li><span className="status-badge status-posted">POSTED</span> - Load is available for booking</li>
          <li><span className="status-badge status-booked">BOOKED</span> - Load has active bookings</li>
          <li><span className="status-badge status-cancelled">CANCELLED</span> - Load is no longer available</li>
        </ul>
      </Card>
    </div>
  );
};

export default Dashboard;