package com.cargopro.loadbooking.service;

import com.cargopro.loadbooking.dto.BookingRequestDto;
import com.cargopro.loadbooking.dto.BookingResponseDto;
import com.cargopro.loadbooking.entity.Booking;
import com.cargopro.loadbooking.entity.BookingStatus;
import com.cargopro.loadbooking.entity.Load;
import com.cargopro.loadbooking.entity.LoadStatus;
import com.cargopro.loadbooking.exception.BusinessException;
import com.cargopro.loadbooking.exception.ResourceNotFoundException;
import com.cargopro.loadbooking.mapper.BookingMapper;
import com.cargopro.loadbooking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingService {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final LoadService loadService;
    
    @Autowired
    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, LoadService loadService) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.loadService = loadService;
    }
    
    public BookingResponseDto createBooking(BookingRequestDto requestDto) {
        logger.info("Creating new booking for load: {} by transporter: {}", 
                   requestDto.getLoadId(), requestDto.getTransporterId());
        
        Load load = loadService.getLoadEntityById(requestDto.getLoadId());
        
        // Business rule: Cannot book cancelled loads
        if (load.getStatus() == LoadStatus.CANCELLED) {
            throw new BusinessException("Cannot create booking for cancelled load");
        }
        
        Booking booking = bookingMapper.toEntity(requestDto, load);
        booking.setStatus(BookingStatus.PENDING); // Default status
        
        Booking savedBooking = bookingRepository.save(booking);
        
        // Business rule: When a booking is made, update load status to BOOKED
        if (load.getStatus() == LoadStatus.POSTED) {
            loadService.updateLoadStatus(load.getId(), LoadStatus.BOOKED);
        }
        
        logger.info("Booking created with ID: {}", savedBooking.getId());
        return bookingMapper.toResponseDto(savedBooking);
    }
    
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookings(UUID loadId, String transporterId, BookingStatus status) {
        logger.info("Fetching bookings with filters - loadId: {}, transporterId: {}, status: {}", 
                   loadId, transporterId, status);
        
        List<Booking> bookings = bookingRepository.findBookingsWithFilters(loadId, transporterId, status);
        
        return bookings.stream()
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(UUID bookingId) {
        logger.info("Fetching booking with ID: {}", bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        
        return bookingMapper.toResponseDto(booking);
    }
    
    public BookingResponseDto updateBooking(UUID bookingId, BookingRequestDto requestDto) {
        logger.info("Updating booking with ID: {}", bookingId);
        
        Booking existingBooking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        
        // Business rule: Cannot update rejected bookings
        if (existingBooking.getStatus() == BookingStatus.REJECTED) {
            throw new BusinessException("Cannot update rejected booking");
        }
        
        bookingMapper.updateEntity(existingBooking, requestDto);
        Booking updatedBooking = bookingRepository.save(existingBooking);
        
        logger.info("Booking updated successfully with ID: {}", bookingId);
        return bookingMapper.toResponseDto(updatedBooking);
    }
    
    public BookingResponseDto acceptBooking(UUID bookingId) {
        logger.info("Accepting booking with ID: {}", bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Only pending bookings can be accepted");
        }
        
        booking.setStatus(BookingStatus.ACCEPTED);
        Booking savedBooking = bookingRepository.save(booking);
        
        // Business rule: When a booking is accepted, reject all other pending bookings for the same load
        List<Booking> otherBookings = bookingRepository.findByLoadIdAndStatus(
            booking.getLoad().getId(), BookingStatus.PENDING);
        
        otherBookings.stream()
            .filter(b -> !b.getId().equals(bookingId))
            .forEach(b -> {
                b.setStatus(BookingStatus.REJECTED);
                bookingRepository.save(b);
            });
        
        logger.info("Booking accepted with ID: {} and other pending bookings rejected", bookingId);
        return bookingMapper.toResponseDto(savedBooking);
    }
    
    public BookingResponseDto rejectBooking(UUID bookingId) {
        logger.info("Rejecting booking with ID: {}", bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessException("Only pending bookings can be rejected");
        }
        
        booking.setStatus(BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);
        
        // Check if all bookings for this load are rejected, then revert load status to POSTED
        checkAndUpdateLoadStatus(booking.getLoad().getId());
        
        logger.info("Booking rejected with ID: {}", bookingId);
        return bookingMapper.toResponseDto(savedBooking);
    }
    
    public void deleteBooking(UUID bookingId) {
        logger.info("Deleting booking with ID: {}", bookingId);
        
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        
        UUID loadId = booking.getLoad().getId();
        bookingRepository.delete(booking);
        
        // Business rule: If all bookings are deleted or rejected, revert load status to POSTED
        checkAndUpdateLoadStatus(loadId);
        
        logger.info("Booking deleted with ID: {}", bookingId);
    }
    
    private void checkAndUpdateLoadStatus(UUID loadId) {
        Load load = loadService.getLoadEntityById(loadId);
        
        if (load.getStatus() == LoadStatus.BOOKED) {
            List<Booking> activeBookings = bookingRepository.findByLoadIdAndStatus(loadId, BookingStatus.PENDING);
            List<Booking> acceptedBookings = bookingRepository.findByLoadIdAndStatus(loadId, BookingStatus.ACCEPTED);
            
            // If no active or accepted bookings exist, revert to POSTED
            if (activeBookings.isEmpty() && acceptedBookings.isEmpty()) {
                loadService.updateLoadStatus(loadId, LoadStatus.POSTED);
                logger.info("Load status reverted to POSTED for load ID: {}", loadId);
            }
        }
    }
}