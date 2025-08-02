package com.cargopro.loadbooking.mapper;

import com.cargopro.loadbooking.dto.BookingRequestDto;
import com.cargopro.loadbooking.dto.BookingResponseDto;
import com.cargopro.loadbooking.entity.Booking;
import com.cargopro.loadbooking.entity.Load;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    
    public Booking toEntity(BookingRequestDto dto, Load load) {
        Booking booking = new Booking();
        booking.setLoad(load);
        booking.setTransporterId(dto.getTransporterId());
        booking.setProposedRate(dto.getProposedRate());
        booking.setComment(dto.getComment());
        return booking;
    }
    
    public BookingResponseDto toResponseDto(Booking booking) {
        return new BookingResponseDto(
            booking.getId(),
            booking.getLoad().getId(),
            booking.getTransporterId(),
            booking.getProposedRate(),
            booking.getComment(),
            booking.getStatus(),
            booking.getRequestedAt()
        );
    }
    
    public void updateEntity(Booking booking, BookingRequestDto dto) {
        booking.setTransporterId(dto.getTransporterId());
        booking.setProposedRate(dto.getProposedRate());
        booking.setComment(dto.getComment());
    }
}