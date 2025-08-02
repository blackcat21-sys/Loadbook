package com.cargopro.loadbooking.controller;

import com.cargopro.loadbooking.dto.BookingRequestDto;
import com.cargopro.loadbooking.dto.BookingResponseDto;
import com.cargopro.loadbooking.entity.BookingStatus;
import com.cargopro.loadbooking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/booking")
@Tag(name = "Booking Management", description = "APIs for managing bookings")
@CrossOrigin(origins = "*")
public class BookingController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);
    
    private final BookingService bookingService;
    
    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new booking", description = "Creates a new booking with default status PENDING")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto requestDto) {
        logger.info("Creating new booking for load: {} by transporter: {}", 
                   requestDto.getLoadId(), requestDto.getTransporterId());
        
        BookingResponseDto responseDto = bookingService.createBooking(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get bookings with filters", description = "Retrieves bookings with optional filtering")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<BookingResponseDto>> getBookings(
            @Parameter(description = "Filter by load ID") @RequestParam(required = false) UUID loadId,
            @Parameter(description = "Filter by transporter ID") @RequestParam(required = false) String transporterId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) BookingStatus status) {
        
        logger.info("Fetching bookings with filters - loadId: {}, transporterId: {}, status: {}", 
                   loadId, transporterId, status);
        
        List<BookingResponseDto> bookings = bookingService.getBookings(loadId, transporterId, status);
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID", description = "Retrieves a specific booking by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking found"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingResponseDto> getBookingById(
            @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {
        
        logger.info("Fetching booking with ID: {}", bookingId);
        
        BookingResponseDto responseDto = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/{bookingId}")
    @Operation(summary = "Update booking", description = "Updates an existing booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingResponseDto> updateBooking(
            @Parameter(description = "Booking ID") @PathVariable UUID bookingId,
            @Valid @RequestBody BookingRequestDto requestDto) {
        
        logger.info("Updating booking with ID: {}", bookingId);
        
        BookingResponseDto responseDto = bookingService.updateBooking(bookingId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/{bookingId}/accept")
    @Operation(summary = "Accept booking", description = "Accepts a pending booking and rejects others for the same load")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking accepted successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingResponseDto> acceptBooking(
            @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {
        
        logger.info("Accepting booking with ID: {}", bookingId);
        
        BookingResponseDto responseDto = bookingService.acceptBooking(bookingId);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/{bookingId}/reject")
    @Operation(summary = "Reject booking", description = "Rejects a pending booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking rejected successfully"),
        @ApiResponse(responseCode = "400", description = "Business rule violation"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BookingResponseDto> rejectBooking(
            @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {
        
        logger.info("Rejecting booking with ID: {}", bookingId);
        
        BookingResponseDto responseDto = bookingService.rejectBooking(bookingId);
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{bookingId}")
    @Operation(summary = "Delete booking", description = "Deletes a booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Booking deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteBooking(
            @Parameter(description = "Booking ID") @PathVariable UUID bookingId) {
        
        logger.info("Deleting booking with ID: {}", bookingId);
        
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
}