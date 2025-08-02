package com.cargopro.loadbooking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public class LoadRequestDto {
    
    @NotBlank(message = "Shipper ID is required")
    private String shipperId;
    
    @Valid
    @NotNull(message = "Facility information is required")
    private FacilityDto facility;
    
    @NotBlank(message = "Product type is required")
    private String productType;
    
    @NotBlank(message = "Truck type is required")
    private String truckType;
    
    @Min(value = 1, message = "Number of trucks must be at least 1")
    @NotNull(message = "Number of trucks is required")
    private Integer noOfTrucks;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    @NotNull(message = "Weight is required")
    private Double weight;
    
    private String comment;
    
    // Default constructor
    public LoadRequestDto() {}
    
    // Constructor
    public LoadRequestDto(String shipperId, FacilityDto facility, String productType, 
                         String truckType, Integer noOfTrucks, Double weight, String comment) {
        this.shipperId = shipperId;
        this.facility = facility;
        this.productType = productType;
        this.truckType = truckType;
        this.noOfTrucks = noOfTrucks;
        this.weight = weight;
        this.comment = comment;
    }
    
    // Getters and Setters
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
}