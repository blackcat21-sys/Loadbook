package com.cargopro.loadbooking.service;

import com.cargopro.loadbooking.dto.LoadRequestDto;
import com.cargopro.loadbooking.dto.LoadResponseDto;
import com.cargopro.loadbooking.entity.Load;
import com.cargopro.loadbooking.entity.LoadStatus;
import com.cargopro.loadbooking.exception.BusinessException;
import com.cargopro.loadbooking.exception.ResourceNotFoundException;
import com.cargopro.loadbooking.mapper.LoadMapper;
import com.cargopro.loadbooking.repository.LoadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class LoadService {
    
    private static final Logger logger = LoggerFactory.getLogger(LoadService.class);
    
    private final LoadRepository loadRepository;
    private final LoadMapper loadMapper;
    
    @Autowired
    public LoadService(LoadRepository loadRepository, LoadMapper loadMapper) {
        this.loadRepository = loadRepository;
        this.loadMapper = loadMapper;
    }
    
    public LoadResponseDto createLoad(LoadRequestDto requestDto) {
        logger.info("Creating new load for shipper: {}", requestDto.getShipperId());
        
        Load load = loadMapper.toEntity(requestDto);
        load.setStatus(LoadStatus.POSTED); // Default status
        
        Load savedLoad = loadRepository.save(load);
        logger.info("Load created with ID: {}", savedLoad.getId());
        
        return loadMapper.toResponseDto(savedLoad);
    }
    
    @Transactional(readOnly = true)
    public Page<LoadResponseDto> getLoads(String shipperId, String truckType, LoadStatus status, 
                                         int page, int size) {
        logger.info("Fetching loads with filters - shipperId: {}, truckType: {}, status: {}, page: {}, size: {}", 
                   shipperId, truckType, status, page, size);
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("datePosted").descending());
        Page<Load> loads = loadRepository.findLoadsWithFilters(shipperId, truckType, status, pageable);
        
        return loads.map(loadMapper::toResponseDto);
    }
    
    @Transactional(readOnly = true)
    public LoadResponseDto getLoadById(UUID loadId) {
        logger.info("Fetching load with ID: {}", loadId);
        
        Load load = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with ID: " + loadId));
        
        return loadMapper.toResponseDto(load);
    }
    
    public LoadResponseDto updateLoad(UUID loadId, LoadRequestDto requestDto) {
        logger.info("Updating load with ID: {}", loadId);
        
        Load existingLoad = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with ID: " + loadId));
        
        // Business rule: Cannot update cancelled loads
        if (existingLoad.getStatus() == LoadStatus.CANCELLED) {
            throw new BusinessException("Cannot update cancelled load");
        }
        
        loadMapper.updateEntity(existingLoad, requestDto);
        Load updatedLoad = loadRepository.save(existingLoad);
        
        logger.info("Load updated successfully with ID: {}", loadId);
        return loadMapper.toResponseDto(updatedLoad);
    }
    
    public void deleteLoad(UUID loadId) {
        logger.info("Deleting load with ID: {}", loadId);
        
        Load load = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with ID: " + loadId));
        
        // Business rule: Set status to CANCELLED instead of hard delete to maintain data integrity
        load.setStatus(LoadStatus.CANCELLED);
        loadRepository.save(load);
        
        logger.info("Load cancelled with ID: {}", loadId);
    }
    
    public void updateLoadStatus(UUID loadId, LoadStatus newStatus) {
        logger.info("Updating load status for ID: {} to {}", loadId, newStatus);
        
        Load load = loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with ID: " + loadId));
        
        LoadStatus currentStatus = load.getStatus();
        
        // Business rule: Validate status transitions
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new BusinessException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
        
        load.setStatus(newStatus);
        loadRepository.save(load);
        
        logger.info("Load status updated successfully for ID: {} from {} to {}", 
                   loadId, currentStatus, newStatus);
    }
    
    @Transactional(readOnly = true)
    public Load getLoadEntityById(UUID loadId) {
        return loadRepository.findById(loadId)
            .orElseThrow(() -> new ResourceNotFoundException("Load not found with ID: " + loadId));
    }
    
    private boolean isValidStatusTransition(LoadStatus current, LoadStatus target) {
        // Define valid status transitions
        switch (current) {
            case POSTED:
                return target == LoadStatus.BOOKED || target == LoadStatus.CANCELLED;
            case BOOKED:
                return target == LoadStatus.POSTED || target == LoadStatus.CANCELLED;
            case CANCELLED:
                return false; // Cannot transition from CANCELLED
            default:
                return false;
        }
    }
}