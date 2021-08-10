package com.psj.homework.elevator.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT p FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus <> 2")
    List<Reservation> findAllExceptGetOff(Long elevatorId);

    @Query("SELECT p FROM Reservation p WHERE elevatorId = ?1 AND boardingStatus = 1")
    List<Reservation> findAllOnBoard(Long elevatorId);
}