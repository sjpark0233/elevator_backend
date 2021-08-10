package com.psj.homework.elevator.domain.elevator;

import com.psj.homework.elevator.domain.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ElevatorRepository extends JpaRepository<Elevator, Long> {

}
