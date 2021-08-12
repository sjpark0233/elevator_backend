package com.psj.homework.elevator.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT p FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus <> 2 ORDER BY id ASC")
    List<Reservation> findAllExceptGetOff(Long elevatorId);

    @Query("SELECT p FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus = 0 ORDER BY id ASC")
    List<Reservation> findAllWaiting(Long elevatorId);

    @Query("SELECT p FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus = 1 ORDER BY id ASC")
    List<Reservation> findAllOnBoard(Long elevatorId);

    @Query("SELECT MAX(departureFloor) FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus <> 2 AND direction = 'D'")
    Integer findTopFloorDown(Long elevatorId);

    @Query("SELECT MIN(departureFloor) FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus <> 2 AND direction = 'U'")
    Integer findBottomFloorUp(Long elevatorId);
}