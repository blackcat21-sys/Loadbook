#!/bin/bash

# Load & Booking Management System - Quick Start Script

echo "🚀 Starting Load & Booking Management System..."

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Start database containers
echo "📊 Starting PostgreSQL database..."
docker-compose up -d

# Wait for database to be ready
echo "⏳ Waiting for database to be ready..."
sleep 10

# Check if database is ready
until docker-compose exec postgres pg_isready -U postgres >/dev/null 2>&1; do
    echo "⏳ Waiting for database..."
    sleep 2
done

echo "✅ Database is ready!"

# Start backend in background
echo "🔧 Starting Spring Boot backend..."
mvn clean install -q
nohup mvn spring-boot:run > backend.log 2>&1 &
BACKEND_PID=$!

# Wait for backend to start
echo "⏳ Waiting for backend to start..."
sleep 30

# Check if backend is running
until curl -f http://localhost:8080/actuator/health >/dev/null 2>&1; do
    echo "⏳ Waiting for backend..."
    sleep 5
done

echo "✅ Backend is ready!"

# Start frontend
echo "🌐 Starting React frontend..."
cd frontend

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "📦 Installing frontend dependencies..."
    npm install
fi

echo "🚀 Starting frontend development server..."
npm start &
FRONTEND_PID=$!

echo ""
echo "🎉 Application is starting up!"
echo ""
echo "📍 Access points:"
echo "   Frontend:  http://localhost:3000"
echo "   Backend:   http://localhost:8080"
echo "   API Docs:  http://localhost:8080/swagger-ui.html"
echo "   PgAdmin:   http://localhost:5050 (admin@cargopro.ai / admin123)"
echo ""
echo "📋 To stop the application:"
echo "   Frontend: Ctrl+C in this terminal"
echo "   Backend:  kill $BACKEND_PID"
echo "   Database: docker-compose down"
echo ""
echo "📝 Backend logs: tail -f backend.log"
echo ""

# Keep frontend running in foreground
wait $FRONTEND_PID