package com.cargopro.loadbooking.controller;

import com.cargopro.loadbooking.dto.LoadRequestDto;
import com.cargopro.loadbooking.dto.LoadResponseDto;
import com.cargopro.loadbooking.entity.LoadStatus;
import com.cargopro.loadbooking.service.LoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/load")
@Tag(name = "Load Management", description = "APIs for managing loads")
@CrossOrigin(origins = "*")
public class LoadController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadController.class);
    
    private final LoadService loadService;
    
    @Autowired
    public LoadController(LoadService loadService) {
        this.loadService = loadService;
    }
    
    @PostMapping
    @Operation(summary = "Create a new load", description = "Creates a new load with default status POSTED")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Load created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoadResponseDto> createLoad(@Valid @RequestBody LoadRequestDto requestDto) {
        logger.info("Creating new load for shipper: {}", requestDto.getShipperId());
        
        LoadResponseDto responseDto = loadService.createLoad(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get loads with filters", description = "Retrieves loads with optional filtering and pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Loads retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<LoadResponseDto>> getLoads(
            @Parameter(description = "Filter by shipper ID") @RequestParam(required = false) String shipperId,
            @Parameter(description = "Filter by truck type") @RequestParam(required = false) String truckType,
            @Parameter(description = "Filter by status") @RequestParam(required = false) LoadStatus status,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        logger.info("Fetching loads with filters - shipperId: {}, truckType: {}, status: {}, page: {}, size: {}", 
                   shipperId, truckType, status, page, size);
        
        Page<LoadResponseDto> loads = loadService.getLoads(shipperId, truckType, status, page, size);
        return ResponseEntity.ok(loads);
    }
    
    @GetMapping("/{loadId}")
    @Operation(summary = "Get load by ID", description = "Retrieves a specific load by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Load found"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoadResponseDto> getLoadById(
            @Parameter(description = "Load ID") @PathVariable UUID loadId) {
        
        logger.info("Fetching load with ID: {}", loadId);
        
        LoadResponseDto responseDto = loadService.getLoadById(loadId);
        return ResponseEntity.ok(responseDto);
    }
    
    @PutMapping("/{loadId}")
    @Operation(summary = "Update load", description = "Updates an existing load")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Load updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or business rule violation"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoadResponseDto> updateLoad(
            @Parameter(description = "Load ID") @PathVariable UUID loadId,
            @Valid @RequestBody LoadRequestDto requestDto) {
        
        logger.info("Updating load with ID: {}", loadId);
        
        LoadResponseDto responseDto = loadService.updateLoad(loadId, requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    @DeleteMapping("/{loadId}")
    @Operation(summary = "Delete load", description = "Marks a load as cancelled (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Load cancelled successfully"),
        @ApiResponse(responseCode = "404", description = "Load not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteLoad(
            @Parameter(description = "Load ID") @PathVariable UUID loadId) {
        
        logger.info("Deleting load with ID: {}", loadId);
        
        loadService.deleteLoad(loadId);
        return ResponseEntity.noContent().build();
    }
}