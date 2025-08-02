package com.cargopro.loadbooking.dto;

import com.cargopro.loadbooking.entity.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class BookingResponseDto {
    
    private UUID id;
    private UUID loadId;
    private String transporterId;
    private Double proposedRate;
    private String comment;
    private BookingStatus status;
    private LocalDateTime requestedAt;
    
    // Default constructor
    public BookingResponseDto() {}
    
    // Constructor
    public BookingResponseDto(UUID id, UUID loadId, String transporterId, Double proposedRate,
                             String comment, BookingStatus status, LocalDateTime requestedAt) {
        this.id = id;
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
        this.status = status;
        this.requestedAt = requestedAt;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getLoadId() {
        return loadId;
    }
    
    public void setLoadId(UUID loadId) {
        this.loadId = loadId;
    }
    
    public String getTransporterId() {
        return transporterId;
    }
    
    public void setTransporterId(String transporterId) {
        this.transporterId = transporterId;
    }
    
    public Double getProposedRate() {
        return proposedRate;
    }
    
    public void setProposedRate(Double proposedRate) {
        this.proposedRate = proposedRate;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
}