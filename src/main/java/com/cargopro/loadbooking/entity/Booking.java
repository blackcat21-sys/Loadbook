package com.cargopro.loadbooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @NotNull(message = "Load ID is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_load"))
    private Load load;
    
    @NotBlank(message = "Transporter ID is required")
    @Column(name = "transporter_id", nullable = false)
    private String transporterId;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Proposed rate must be greater than 0")
    @Column(name = "proposed_rate", nullable = false)
    private Double proposedRate;
    
    @Column(name = "comment")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status = BookingStatus.PENDING;
    
    @CreationTimestamp
    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;
    
    // Default constructor
    public Booking() {}
    
    // Constructor
    public Booking(Load load, String transporterId, Double proposedRate, String comment) {
        this.load = load;
        this.transporterId = transporterId;
        this.proposedRate = proposedRate;
        this.comment = comment;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public Load getLoad() {
        return load;
    }
    
    public void setLoad(Load load) {
        this.load = load;
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