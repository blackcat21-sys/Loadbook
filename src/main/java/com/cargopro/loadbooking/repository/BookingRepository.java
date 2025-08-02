package com.cargopro.loadbooking.repository;

import com.cargopro.loadbooking.entity.Booking;
import com.cargopro.loadbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    @Query("SELECT b FROM Booking b WHERE " +
           "(:loadId IS NULL OR b.load.id = :loadId) AND " +
           "(:transporterId IS NULL OR b.transporterId = :transporterId) AND " +
           "(:status IS NULL OR b.status = :status)")
    List<Booking> findBookingsWithFilters(@Param("loadId") UUID loadId,
                                         @Param("transporterId") String transporterId,
                                         @Param("status") BookingStatus status);
    
    List<Booking> findByLoadId(UUID loadId);
    
    List<Booking> findByTransporterId(String transporterId);
    
    List<Booking> findByStatus(BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.load.id = :loadId ORDER BY b.requestedAt DESC")
    List<Booking> findByLoadIdOrderByRequestedAtDesc(@Param("loadId") UUID loadId);
    
    @Query("SELECT b FROM Booking b WHERE b.load.id = :loadId AND b.status = :status")
    List<Booking> findByLoadIdAndStatus(@Param("loadId") UUID loadId, @Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.load.id = :loadId AND b.status = :status")
    long countByLoadIdAndStatus(@Param("loadId") UUID loadId, @Param("status") BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.transporterId = :transporterId AND b.status = :status ORDER BY b.requestedAt DESC")
    List<Booking> findByTransporterIdAndStatusOrderByRequestedAtDesc(@Param("transporterId") String transporterId, 
                                                                    @Param("status") BookingStatus status);
}