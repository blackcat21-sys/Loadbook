package com.cargopro.loadbooking.dto;

import com.cargopro.loadbooking.entity.LoadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class LoadResponseDto {
    
    private UUID id;
    private String shipperId;
    private FacilityDto facility;
    private String productType;
    private String truckType;
    private Integer noOfTrucks;
    private Double weight;
    private String comment;
    private LocalDateTime datePosted;
    private LoadStatus status;
    
    // Default constructor
    public LoadResponseDto() {}
    
    // Constructor
    public LoadResponseDto(UUID id, String shipperId, FacilityDto facility, String productType,
                          String truckType, Integer noOfTrucks, Double weight, String comment,
                          LocalDateTime datePosted, LoadStatus status) {
        this.id = id;
        this.shipperId = shipperId;
        this.facility = facility;
        this.productType = productType;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.weight = weight;
        this.comment = comment;
        this.datePosted = datePosted;
        this.status = status;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getShipperId() {
        return shipperId;
    }
    
    public void setShipperId(String shipperId) {
        this.shipperId = shipperId;
    }
    
    public FacilityDto getFacility() {
        return facility;
    }
    
    public void setFacility(FacilityDto facility) {
        this.facility = facility;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public String getTruckType() {
        return truckType;
    }
    
    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }
    
    public Integer getNoOfTrucks() {
        return noOfTrucks;
    }
    
    public void setNoOfTrucks(Integer noOfTrucks) {
        this.noOfTrucks = noOfTrucks;
    }
    
    public Double getWeight() {
        return weight;
    }
    
    public void setWeight(Double weight) {
        this.weight = weight;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public LocalDateTime getDatePosted() {
        return datePosted;
    }
    
    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }
    
    public LoadStatus getStatus() {
        return status;
    }
    
    public void setStatus(LoadStatus status) {
        this.status = status;
    }
}