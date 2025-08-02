import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, Select, message, Space, Tag, Card, Row, Col } from 'antd';
import { PlusOutlined, CheckOutlined, CloseOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { bookingAPI, loadAPI } from '../services/api';
import moment from 'moment';

const { Option } = Select;

const BookingManagement = () => {
  const [bookings, setBookings] = useState([]);
  const [loads, setLoads] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [filters, setFilters] = useState({});

  useEffect(() => {
    fetchBookings();
    fetchAvailableLoads();
  }, [filters]);

  const fetchBookings = async () => {
    setLoading(true);
    try {
      const response = await bookingAPI.getBookings(filters);
      setBookings(response.data);
    } catch (error) {
      message.error('Failed to fetch bookings');
    } finally {
      setLoading(false);
    }
  };

  const fetchAvailableLoads = async () => {
    try {
      const response = await loadAPI.getLoads({ status: 'POSTED' });
      setLoads(response.data.content || []);
    } catch (error) {
      console.error('Failed to fetch available loads');
    }
  };

  const handleCreate = () => {
    form.resetFields();
    setModalVisible(true);
  };

  const handleAccept = async (id) => {
    try {
      await bookingAPI.acceptBooking(id);
      message.success('Booking accepted successfully');
      fetchBookings();
    } catch (error) {
      message.error('Failed to accept booking');
    }
  };

  const handleReject = async (id) => {
    try {
      await bookingAPI.rejectBooking(id);
      message.success('Booking rejected successfully');
      fetchBookings();
    } catch (error) {
      message.error('Failed to reject booking');
    }
  };

  const handleDelete = async (id) => {
    try {
      await bookingAPI.deleteBooking(id);
      message.success('Booking deleted successfully');
      fetchBookings();
    } catch (error) {
      message.error('Failed to delete booking');
    }
  };

  const handleSubmit = async (values) => {
    try {
      const bookingData = {
        loadId: values.loadId,
        transporterId: values.transporterId,
        proposedRate: parseFloat(values.proposedRate),
        comment: values.comment,
      };

      await bookingAPI.createBooking(bookingData);
      message.success('Booking created successfully');
      setModalVisible(false);
      fetchBookings();
    } catch (error) {
      message.error('Failed to create booking');
    }
  };

  const handleFilterChange = (key, value) => {
    setFilters(prev => ({
      ...prev,
      [key]: value
    }));
  };

  const getStatusTag = (status) => {
    const colors = {
      PENDING: 'orange',
      ACCEPTED: 'green',
      REJECTED: 'red'
    };
    return <Tag color={colors[status]}>{status}</Tag>;
  };

  const columns = [
    {
      title: 'Load ID',
      dataIndex: 'loadId',
      key: 'loadId',
      render: (id) => id.substring(0, 8) + '...',
    },
    {
      title: 'Transporter ID',
      dataIndex: 'transporterId',
      key: 'transporterId',
    },
    {
      title: 'Proposed Rate ($)',
      dataIndex: 'proposedRate',
      key: 'proposedRate',
      render: (rate) => `$${rate.toLocaleString()}`,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: getStatusTag,
    },
    {
      title: 'Requested At',
      dataIndex: 'requestedAt',
      key: 'requestedAt',
      render: (date) => moment(date).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: 'Comment',
      dataIndex: 'comment',
      key: 'comment',
      ellipsis: true,
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <Space>
          {record.status === 'PENDING' && (
            <>
              <Button 
                icon={<CheckOutlined />} 
                type="primary"
                size="small"
                onClick={() => handleAccept(record.id)}
              >
                Accept
              </Button>
              <Button 
                icon={<CloseOutlined />} 
                danger
                size="small"
                onClick={() => handleReject(record.id)}
              >
                Reject
              </Button>
            </>
          )}
          <Button 
            icon={<DeleteOutlined />} 
            danger
            size="small"
            onClick={() => handleDelete(record.id)}
          >
            Delete
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card title="Booking Management" style={{ marginBottom: 16 }}>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Input
              placeholder="Transporter ID"
              prefix={<SearchOutlined />}
              onChange={(e) => handleFilterChange('transporterId', e.target.value)}
            />
          </Col>
          <Col span={6}>
            <Select
              placeholder="Status"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => handleFilterChange('status', value)}
            >
              <Option value="PENDING">Pending</Option>
              <Option value="ACCEPTED">Accepted</Option>
              <Option value="REJECTED">Rejected</Option>
            </Select>
          </Col>
          <Col span={6}>
            <Select
              placeholder="Load ID"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => handleFilterChange('loadId', value)}
            >
              {loads.map(load => (
                <Option key={load.id} value={load.id}>
                  {load.id.substring(0, 8)}... - {load.shipperId}
                </Option>
              ))}
            </Select>
          </Col>
          <Col span={6}>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              Create Booking
            </Button>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={bookings}
          loading={loading}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `Total ${total} bookings`,
          }}
        />
      </Card>

      <Modal
        title="Create New Booking"
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={600}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Form.Item
            name="loadId"
            label="Select Load"
            rules={[{ required: true, message: 'Please select a load' }]}
          >
            <Select placeholder="Select an available load">
              {loads.map(load => (
                <Option key={load.id} value={load.id}>
                  <div>
                    <strong>{load.shipperId}</strong> - {load.productType}
                    <br />
                    <small>{load.facility.loadingPoint} → {load.facility.unloadingPoint}</small>
                  </div>
                </Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item
            name="transporterId"
            label="Transporter ID"
            rules={[{ required: true, message: 'Please enter transporter ID' }]}
          >
            <Input placeholder="Enter transporter ID" />
          </Form.Item>

          <Form.Item
            name="proposedRate"
            label="Proposed Rate ($)"
            rules={[
              { required: true, message: 'Please enter proposed rate' },
              { type: 'number', min: 0.01, message: 'Rate must be greater than 0', transform: (value) => Number(value) }
            ]}
          >
            <Input type="number" step="0.01" placeholder="Enter proposed rate" />
          </Form.Item>

          <Form.Item
            name="comment"
            label="Comment"
          >
            <Input.TextArea rows={3} placeholder="Enter any additional comments" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                Create Booking
              </Button>
              <Button onClick={() => setModalVisible(false)}>
                Cancel
              </Button>
            </Space>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default BookingManagement;