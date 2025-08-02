import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, DatePicker, Select, message, Space, Tag, Card, Row, Col } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, SearchOutlined } from '@ant-design/icons';
import { loadAPI } from '../services/api';
import moment from 'moment';

const { Option } = Select;
const { RangePicker } = DatePicker;

const LoadManagement = () => {
  const [loads, setLoads] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingLoad, setEditingLoad] = useState(null);
  const [form] = Form.useForm();
  const [filters, setFilters] = useState({});

  useEffect(() => {
    fetchLoads();
  }, [filters]);

  const fetchLoads = async () => {
    setLoading(true);
    try {
      const response = await loadAPI.getLoads(filters);
      setLoads(response.data.content || []);
    } catch (error) {
      message.error('Failed to fetch loads');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingLoad(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (load) => {
    setEditingLoad(load);
    form.setFieldsValue({
      ...load,
      loadingDate: moment(load.facility.loadingDate),
      unloadingDate: moment(load.facility.unloadingDate),
      loadingPoint: load.facility.loadingPoint,
      unloadingPoint: load.facility.unloadingPoint,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id) => {
    try {
      await loadAPI.deleteLoad(id);
      message.success('Load cancelled successfully');
      fetchLoads();
    } catch (error) {
      message.error('Failed to cancel load');
    }
  };

  const handleSubmit = async (values) => {
    try {
      const loadData = {
        shipperId: values.shipperId,
        facility: {
          loadingPoint: values.loadingPoint,
          unloadingPoint: values.unloadingPoint,
          loadingDate: values.loadingDate.toISOString(),
          unloadingDate: values.unloadingDate.toISOString(),
        },
        productType: values.productType,
        truckType: values.truckType,
        noOfTrucks: values.noOfTrucks,
        weight: values.weight,
        comment: values.comment,
      };

      if (editingLoad) {
        await loadAPI.updateLoad(editingLoad.id, loadData);
        message.success('Load updated successfully');
      } else {
        await loadAPI.createLoad(loadData);
        message.success('Load created successfully');
      }

      setModalVisible(false);
      fetchLoads();
    } catch (error) {
      message.error(`Failed to ${editingLoad ? 'update' : 'create'} load`);
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
      POSTED: 'blue',
      BOOKED: 'green',
      CANCELLED: 'orange'
    };
    return <Tag color={colors[status]}>{status}</Tag>;
  };

  const columns = [
    {
      title: 'Shipper ID',
      dataIndex: 'shipperId',
      key: 'shipperId',
    },
    {
      title: 'Product Type',
      dataIndex: 'productType',
      key: 'productType',
    },
    {
      title: 'Truck Type',
      dataIndex: 'truckType',
      key: 'truckType',
    },
    {
      title: 'Route',
      key: 'route',
      render: (_, record) => 
        `${record.facility.loadingPoint} → ${record.facility.unloadingPoint}`,
    },
    {
      title: 'Weight (kg)',
      dataIndex: 'weight',
      key: 'weight',
    },
    {
      title: 'Trucks',
      dataIndex: 'noOfTrucks',
      key: 'noOfTrucks',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: getStatusTag,
    },
    {
      title: 'Date Posted',
      dataIndex: 'datePosted',
      key: 'datePosted',
      render: (date) => moment(date).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Button 
            icon={<EditOutlined />} 
            onClick={() => handleEdit(record)}
            disabled={record.status === 'CANCELLED'}
          >
            Edit
          </Button>
          <Button 
            icon={<DeleteOutlined />} 
            danger 
            onClick={() => handleDelete(record.id)}
            disabled={record.status === 'CANCELLED'}
          >
            Cancel
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <Card title="Load Management" style={{ marginBottom: 16 }}>
        <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
          <Col span={6}>
            <Input
              placeholder="Shipper ID"
              prefix={<SearchOutlined />}
              onChange={(e) => handleFilterChange('shipperId', e.target.value)}
            />
          </Col>
          <Col span={6}>
            <Select
              placeholder="Truck Type"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => handleFilterChange('truckType', value)}
            >
              <Option value="Flatbed">Flatbed</Option>
              <Option value="Container">Container</Option>
              <Option value="Refrigerated">Refrigerated</Option>
              <Option value="Tanker">Tanker</Option>
            </Select>
          </Col>
          <Col span={6}>
            <Select
              placeholder="Status"
              allowClear
              style={{ width: '100%' }}
              onChange={(value) => handleFilterChange('status', value)}
            >
              <Option value="POSTED">Posted</Option>
              <Option value="BOOKED">Booked</Option>
              <Option value="CANCELLED">Cancelled</Option>
            </Select>
          </Col>
          <Col span={6}>
            <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
              Add New Load
            </Button>
          </Col>
        </Row>

        <Table
          columns={columns}
          dataSource={loads}
          loading={loading}
          rowKey="id"
          pagination={{
            pageSize: 10,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `Total ${total} loads`,
          }}
        />
      </Card>

      <Modal
        title={editingLoad ? 'Edit Load' : 'Create New Load'}
        visible={modalVisible}
        onCancel={() => setModalVisible(false)}
        footer={null}
        width={800}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={handleSubmit}
        >
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="shipperId"
                label="Shipper ID"
                rules={[{ required: true, message: 'Please enter shipper ID' }]}
              >
                <Input placeholder="Enter shipper ID" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="productType"
                label="Product Type"
                rules={[{ required: true, message: 'Please enter product type' }]}
              >
                <Input placeholder="Enter product type" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="loadingPoint"
                label="Loading Point"
                rules={[{ required: true, message: 'Please enter loading point' }]}
              >
                <Input placeholder="Enter loading point" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="unloadingPoint"
                label="Unloading Point"
                rules={[{ required: true, message: 'Please enter unloading point' }]}
              >
                <Input placeholder="Enter unloading point" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="loadingDate"
                label="Loading Date"
                rules={[{ required: true, message: 'Please select loading date' }]}
              >
                <DatePicker showTime style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="unloadingDate"
                label="Unloading Date"
                rules={[{ required: true, message: 'Please select unloading date' }]}
              >
                <DatePicker showTime style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                name="truckType"
                label="Truck Type"
                rules={[{ required: true, message: 'Please select truck type' }]}
              >
                <Select placeholder="Select truck type">
                  <Option value="Flatbed">Flatbed</Option>
                  <Option value="Container">Container</Option>
                  <Option value="Refrigerated">Refrigerated</Option>
                  <Option value="Tanker">Tanker</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="noOfTrucks"
                label="Number of Trucks"
                rules={[{ required: true, message: 'Please enter number of trucks' }]}
              >
                <Input type="number" min={1} placeholder="Enter number of trucks" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item
                name="weight"
                label="Weight (kg)"
                rules={[{ required: true, message: 'Please enter weight' }]}
              >
                <Input type="number" min={0} placeholder="Enter weight in kg" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="comment"
            label="Comment"
          >
            <Input.TextArea rows={3} placeholder="Enter any additional comments" />
          </Form.Item>

          <Form.Item>
            <Space>
              <Button type="primary" htmlType="submit">
                {editingLoad ? 'Update' : 'Create'} Load
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

export default LoadManagement;