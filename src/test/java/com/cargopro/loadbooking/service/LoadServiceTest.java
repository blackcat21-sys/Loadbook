package com.cargopro.loadbooking.service;

import com.cargopro.loadbooking.dto.FacilityDto;
import com.cargopro.loadbooking.dto.LoadRequestDto;
import com.cargopro.loadbooking.dto.LoadResponseDto;
import com.cargopro.loadbooking.entity.Facility;
import com.cargopro.loadbooking.entity.Load;
import com.cargopro.loadbooking.entity.LoadStatus;
import com.cargopro.loadbooking.exception.BusinessException;
import com.cargopro.loadbooking.exception.ResourceNotFoundException;
import com.cargopro.loadbooking.mapper.LoadMapper;
import com.cargopro.loadbooking.repository.LoadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @Mock
    private LoadMapper loadMapper;

    @InjectMocks
    private LoadService loadService;

    private Load testLoad;
    private LoadRequestDto testRequestDto;
    private LoadResponseDto testResponseDto;
    private FacilityDto testFacilityDto;
    private Facility testFacility;

    @BeforeEach
    void setUp() {
        // Setup test data
        testFacilityDto = new FacilityDto(
            "Origin City",
            "Destination City",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3)
        );

        testFacility = new Facility(
            "Origin City",
            "Destination City",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3)
        );

        testRequestDto = new LoadRequestDto(
            "SHIPPER001",
            testFacilityDto,
            "Electronics",
            "Flatbed",
            2,
            1500.0,
            "Handle with care"
        );

        testLoad = new Load(
            "SHIPPER001",
            testFacility,
            "Electronics",
            "Flatbed",
            2,
            1500.0,
            "Handle with care"
        );
        testLoad.setId(UUID.randomUUID());
        testLoad.setStatus(LoadStatus.POSTED);
        testLoad.setDatePosted(LocalDateTime.now());

        testResponseDto = new LoadResponseDto(
            testLoad.getId(),
            testLoad.getShipperId(),
            testFacilityDto,
            testLoad.getProductType(),
            testLoad.getTruckType(),
            testLoad.getNoOfTrucks(),
            testLoad.getWeight(),
            testLoad.getComment(),
            testLoad.getDatePosted(),
            testLoad.getStatus()
        );
    }

    @Test
    void createLoad_ShouldReturnLoadResponseDto_WhenValidRequest() {
        // Given
        when(loadMapper.toEntity(testRequestDto)).thenReturn(testLoad);
        when(loadRepository.save(any(Load.class))).thenReturn(testLoad);
        when(loadMapper.toResponseDto(testLoad)).thenReturn(testResponseDto);

        // When
        LoadResponseDto result = loadService.createLoad(testRequestDto);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDto.getId(), result.getId());
        assertEquals(LoadStatus.POSTED, testLoad.getStatus());
        verify(loadRepository).save(any(Load.class));
        verify(loadMapper).toEntity(testRequestDto);
        verify(loadMapper).toResponseDto(testLoad);
    }

    @Test
    void getLoads_ShouldReturnPagedLoads_WhenValidParameters() {
        // Given
        List<Load> loads = List.of(testLoad);
        Page<Load> loadPage = new PageImpl<>(loads, PageRequest.of(0, 10), 1);
        
        when(loadRepository.findLoadsWithFilters(any(), any(), any(), any(Pageable.class)))
            .thenReturn(loadPage);
        when(loadMapper.toResponseDto(testLoad)).thenReturn(testResponseDto);

        // When
        Page<LoadResponseDto> result = loadService.getLoads("SHIPPER001", "Flatbed", LoadStatus.POSTED, 1, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testResponseDto.getId(), result.getContent().get(0).getId());
        verify(loadRepository).findLoadsWithFilters(eq("SHIPPER001"), eq("Flatbed"), eq(LoadStatus.POSTED), any(Pageable.class));
    }

    @Test
    void getLoadById_ShouldReturnLoadResponseDto_WhenLoadExists() {
        // Given
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadMapper.toResponseDto(testLoad)).thenReturn(testResponseDto);

        // When
        LoadResponseDto result = loadService.getLoadById(loadId);

        // Then
        assertNotNull(result);
        assertEquals(testResponseDto.getId(), result.getId());
        verify(loadRepository).findById(loadId);
        verify(loadMapper).toResponseDto(testLoad);
    }

    @Test
    void getLoadById_ShouldThrowResourceNotFoundException_WhenLoadNotExists() {
        // Given
        UUID loadId = UUID.randomUUID();
        when(loadRepository.findById(loadId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> loadService.getLoadById(loadId));
        verify(loadRepository).findById(loadId);
        verifyNoInteractions(loadMapper);
    }

    @Test
    void updateLoad_ShouldReturnUpdatedLoad_WhenValidRequest() {
        // Given
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);
        when(loadMapper.toResponseDto(testLoad)).thenReturn(testResponseDto);

        // When
        LoadResponseDto result = loadService.updateLoad(loadId, testRequestDto);

        // Then
        assertNotNull(result);
        verify(loadRepository).findById(loadId);
        verify(loadMapper).updateEntity(testLoad, testRequestDto);
        verify(loadRepository).save(testLoad);
        verify(loadMapper).toResponseDto(testLoad);
    }

    @Test
    void updateLoad_ShouldThrowBusinessException_WhenLoadIsCancelled() {
        // Given
        UUID loadId = testLoad.getId();
        testLoad.setStatus(LoadStatus.CANCELLED);
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));

        // When & Then
        assertThrows(BusinessException.class, () -> loadService.updateLoad(loadId, testRequestDto));
        verify(loadRepository).findById(loadId);
        verify(loadRepository, never()).save(any());
    }

    @Test
    void deleteLoad_ShouldSetStatusToCancelled_WhenLoadExists() {
        // Given
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);

        // When
        loadService.deleteLoad(loadId);

        // Then
        assertEquals(LoadStatus.CANCELLED, testLoad.getStatus());
        verify(loadRepository).findById(loadId);
        verify(loadRepository).save(testLoad);
    }

    @Test
    void updateLoadStatus_ShouldUpdateStatus_WhenValidTransition() {
        // Given
        UUID loadId = testLoad.getId();
        testLoad.setStatus(LoadStatus.POSTED);
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));
        when(loadRepository.save(testLoad)).thenReturn(testLoad);

        // When
        loadService.updateLoadStatus(loadId, LoadStatus.BOOKED);

        // Then
        assertEquals(LoadStatus.BOOKED, testLoad.getStatus());
        verify(loadRepository).findById(loadId);
        verify(loadRepository).save(testLoad);
    }

    @Test
    void updateLoadStatus_ShouldThrowBusinessException_WhenInvalidTransition() {
        // Given
        UUID loadId = testLoad.getId();
        testLoad.setStatus(LoadStatus.CANCELLED);
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));

        // When & Then
        assertThrows(BusinessException.class, () -> loadService.updateLoadStatus(loadId, LoadStatus.POSTED));
        verify(loadRepository).findById(loadId);
        verify(loadRepository, never()).save(any());
    }

    @Test
    void getLoadEntityById_ShouldReturnLoad_WhenExists() {
        // Given
        UUID loadId = testLoad.getId();
        when(loadRepository.findById(loadId)).thenReturn(Optional.of(testLoad));

        // When
        Load result = loadService.getLoadEntityById(loadId);

        // Then
        assertNotNull(result);
        assertEquals(testLoad.getId(), result.getId());
        verify(loadRepository).findById(loadId);
    }
}