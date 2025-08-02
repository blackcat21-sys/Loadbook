package com.cargopro.loadbooking.repository;

import com.cargopro.loadbooking.entity.Load;
import com.cargopro.loadbooking.entity.LoadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID> {
    
    @Query("SELECT l FROM Load l WHERE " +
           "(:shipperId IS NULL OR l.shipperId = :shipperId) AND " +
           "(:truckType IS NULL OR l.truckType = :truckType) AND " +
           "(:status IS NULL OR l.status = :status)")
    Page<Load> findLoadsWithFilters(@Param("shipperId") String shipperId,
                                   @Param("truckType") String truckType,
                                   @Param("status") LoadStatus status,
                                   Pageable pageable);
    
    List<Load> findByShipperId(String shipperId);
    
    List<Load> findByStatus(LoadStatus status);
    
    List<Load> findByTruckType(String truckType);
    
    @Query("SELECT l FROM Load l WHERE l.status = :status ORDER BY l.datePosted DESC")
    List<Load> findByStatusOrderByDatePostedDesc(@Param("status") LoadStatus status);
    
    @Query("SELECT COUNT(l) FROM Load l WHERE l.shipperId = :shipperId AND l.status = :status")
    long countByShipperIdAndStatus(@Param("shipperId") String shipperId, @Param("status") LoadStatus status);
}