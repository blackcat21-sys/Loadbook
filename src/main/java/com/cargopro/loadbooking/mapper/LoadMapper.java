package com.cargopro.loadbooking.mapper;

import com.cargopro.loadbooking.dto.FacilityDto;
import com.cargopro.loadbooking.dto.LoadRequestDto;
import com.cargopro.loadbooking.dto.LoadResponseDto;
import com.cargopro.loadbooking.entity.Facility;
import com.cargopro.loadbooking.entity.Load;
import org.springframework.stereotype.Component;

@Component
public class LoadMapper {
    
    public Load toEntity(LoadRequestDto dto) {
        Load load = new Load();
        load.setShipperId(dto.getShipperId());
        load.setFacility(toFacilityEntity(dto.getFacility()));
        load.setProductType(dto.getProductType());
        load.setTruckType(dto.getTruckType());
        load.setNoOfTrucks(dto.getNoOfTrucks());
        load.setWeight(dto.getWeight());
        load.setComment(dto.getComment());
        return load;
    }
    
    public LoadResponseDto toResponseDto(Load load) {
        return new LoadResponseDto(
            load.getId(),
            load.getShipperId(),
            toFacilityDto(load.getFacility()),
            load.getProductType(),
            load.getTruckType(),
            load.getNoOfTrucks(),
            load.getWeight(),
            load.getComment(),
            load.getDatePosted(),
            load.getStatus()
        );
    }
    
    public void updateEntity(Load load, LoadRequestDto dto) {
        load.setShipperId(dto.getShipperId());
        load.setFacility(toFacilityEntity(dto.getFacility()));
        load.setProductType(dto.getProductType());
        load.setTruckType(dto.getTruckType());
        load.setNoOfTrucks(dto.getNoOfTrucks());
        load.setWeight(dto.getWeight());
        load.setComment(dto.getComment());
    }
    
    private Facility toFacilityEntity(FacilityDto dto) {
        return new Facility(
            dto.getLoadingPoint(),
            dto.getUnloadingPoint(),
            dto.getLoadingDate(),
            dto.getUnloadingDate()
        );
    }
    
    private FacilityDto toFacilityDto(Facility facility) {
        return new FacilityDto(
            facility.getLoadingPoint(),
            facility.getUnloadingPoint(),
            facility.getLoadingDate(),
            facility.getUnloadingDate()
        );
    }
}