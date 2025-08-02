-- Database initialization script for Load & Booking System
-- This script sets up the database with proper permissions

-- Create additional indexes for performance
CREATE INDEX IF NOT EXISTS idx_loads_shipper_id ON loads(shipper_id);
CREATE INDEX IF NOT EXISTS idx_loads_status ON loads(status);
CREATE INDEX IF NOT EXISTS idx_loads_truck_type ON loads(truck_type);
CREATE INDEX IF NOT EXISTS idx_loads_date_posted ON loads(date_posted);

CREATE INDEX IF NOT EXISTS idx_bookings_load_id ON bookings(load_id);
CREATE INDEX IF NOT EXISTS idx_bookings_transporter_id ON bookings(transporter_id);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings(status);
CREATE INDEX IF NOT EXISTS idx_bookings_requested_at ON bookings(requested_at);

-- Insert some sample data for testing
-- (This will be executed only if tables are empty)

-- Note: Sample data will be automatically created when the application starts
-- due to the hibernate.ddl-auto=update setting