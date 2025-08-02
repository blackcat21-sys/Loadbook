package com.cargopro.loadbooking.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class BookingRequestDto {
    
    @NotNull(message = "Load ID is required")
    private UUID loadId;
    
    @NotBlank(message = "Transporter ID is required")
    private String transporterId;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Proposed rate must be greater than 0")
    @NotNull(message = "Proposed rate is required")
    private Double proposedRate;
    
    private String comment;
    
    // Default constructor
    public BookingRequestDto() {}
    
    // Constructor
    public BookingRequestDto(UUID loadId, String transporterId, Double proposedRate, String comment) {
        this.loadId = loadId;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
    }
    
    // Getters and Setters
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
}