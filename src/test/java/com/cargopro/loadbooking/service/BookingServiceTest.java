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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private LoadService loadService;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private BookingRequestDto testRequestDto;
    private BookingResponseDto testResponseDto;
    private Load testLoad;

    @BeforeEach
    void setUp() {
        // Setup test data
        testLoad = new Load();
        testLoad.setId(UUID.randomUUID());
        testLoad.setStatus(LoadStatus.POSTED);

        testRequestDto = new BookingRequestDto(
            testLoad.getId(),
            "TRANSPORTER001",
            2500.0,
            "Urgent delivery required"
        );

        testBooking = new Booking(
            testLoad,
            "TRANSPORTER001",
            2500.0,
            "Urgent delivery required"
        );
        testBooking.setId(UUID.randomUUID());
        testBooking.setStatus(BookingStatus.PENDING);
        testBooking.setRequestedAt(LocalDateTime.now());

        testResponseDto = new BookingResponseDto(
            testBooking.getId(),
            testLoad.getId(),
            testBooking.getTransporterId(),
            testBooking.getProposedRate(),
            testBooking.getComment(),
            testBooking.getStatus(),
            testBooking.getRequestedAt()
        );
    }

    @Test
    void createBooking_ShouldReturnBookingResponseDto_WhenValidRequest() {
        // Given
        when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);
        when(bookingMapper.toEntity(testRequestDto, testLoad)).thenReturn(testBooking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(bookingMapper.toResponseDto(testBooking)).thenReturn(testResponseDto);

        // When
        BookingResponseDto result = bookingService.createBooking(testRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDto.getId(), result.getId());
        assertEquals(BookingStatus.PENDING, testBooking.getStatus());
        verify(loadService).getLoadEntityById(testLoad.getId());
        verify(loadService).updateLoadStatus(testLoad.getId(), LoadStatus.BOOKED);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_ShouldThrowBusinessException_WhenLoadIsCancelled() {
        // Given
        testLoad.setStatus(LoadStatus.CANCELLED);
        when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.createBooking(testRequestDto));
        verify(loadService).getLoadEntityById(testLoad.getId());
        verifyNoInteractions(bookingMapper);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookings_ShouldReturnBookingList_WhenValidFilters() {
        // Given
        List<Booking> bookings = Arrays.asList(testBooking);
        when(bookingRepository.findBookingsWithFilters(any(), any(), any())).thenReturn(bookings);
        when(bookingMapper.toResponseDto(testBooking)).thenReturn(testResponseDto);

        // When
        List<BookingResponseDto> result = bookingService.getBookings(
            testLoad.getId(), "TRANSPORTER001", BookingStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testResponseDto.getId(), result.get(0).getId());
        verify(bookingRepository).findBookingsWithFilters(testLoad.getId(), "TRANSPORTER001", BookingStatus.PENDING);
    }

    @Test
    void getBookingById_ShouldReturnBookingResponseDto_WhenBookingExists() {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingMapper.toResponseDto(testBooking)).thenReturn(testResponseDto);

        // When
        BookingResponseDto result = bookingService.getBookingById(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDto.getId(), result.getId());
        verify(bookingRepository).findById(bookingId);
        verify(bookingMapper).toResponseDto(testBooking);
    }

    @Test
    void getBookingById_ShouldThrowResourceNotFoundException_WhenBookingNotExists() {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.getBookingById(bookingId));
        verify(bookingRepository).findById(bookingId);
        verifyNoInteractions(bookingMapper);
    }

    @Test
    void updateBooking_ShouldReturnUpdatedBooking_WhenValidRequest() {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(testBooking)).thenReturn(testBooking);
        when(bookingMapper.toResponseDto(testBooking)).thenReturn(testResponseDto);

        // When
        BookingResponseDto result = bookingService.updateBooking(bookingId, testRequestDto);

        // Then
        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
        verify(bookingMapper).updateEntity(testBooking, testRequestDto);
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void updateBooking_ShouldThrowBusinessException_WhenBookingIsRejected() {
        // Given
        UUID bookingId = testBooking.getId();
        testBooking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.updateBooking(bookingId, testRequestDto));
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void acceptBooking_ShouldAcceptBookingAndRejectOthers_WhenValidRequest() {
        // Given
        UUID bookingId = testBooking.getId();
        Booking otherBooking = new Booking(testLoad, "TRANSPORTER002", 2000.0, "Alternative option");
        otherBooking.setId(UUID.randomUUID());
        otherBooking.setStatus(BookingStatus.PENDING);
        
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(testBooking)).thenReturn(testBooking);
        when(bookingRepository.findByLoadIdAndStatus(testLoad.getId(), BookingStatus.PENDING))
            .thenReturn(Arrays.asList(testBooking, otherBooking));
        when(bookingMapper.toResponseDto(testBooking)).thenReturn(testResponseDto);

        // When
        BookingResponseDto result = bookingService.acceptBooking(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.ACCEPTED, testBooking.getStatus());
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, times(2)).save(any(Booking.class)); // testBooking + otherBooking
    }

    @Test
    void acceptBooking_ShouldThrowBusinessException_WhenBookingNotPending() {
        // Given
        UUID bookingId = testBooking.getId();
        testBooking.setStatus(BookingStatus.ACCEPTED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));

        // When & Then
        assertThrows(BusinessException.class, () -> bookingService.acceptBooking(bookingId));
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void rejectBooking_ShouldRejectBooking_WhenValidRequest() {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(testBooking)).thenReturn(testBooking);
        when(bookingMapper.toResponseDto(testBooking)).thenReturn(testResponseDto);
        when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

        // When
        BookingResponseDto result = bookingService.rejectBooking(bookingId);

        // Then
        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, testBooking.getStatus());
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void deleteBooking_ShouldDeleteBooking_WhenBookingExists() {
        // Given
        UUID bookingId = testBooking.getId();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(testBooking));
        when(loadService.getLoadEntityById(testLoad.getId())).thenReturn(testLoad);

        // When
        bookingService.deleteBooking(bookingId);

        // Then
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository).delete(testBooking);
    }

    @Test
    void deleteBooking_ShouldThrowResourceNotFoundException_WhenBookingNotExists() {
        // Given
        UUID bookingId = UUID.randomUUID();
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> bookingService.deleteBooking(bookingId));
        verify(bookingRepository).findById(bookingId);
        verify(bookingRepository, never()).delete(any());
    }
}