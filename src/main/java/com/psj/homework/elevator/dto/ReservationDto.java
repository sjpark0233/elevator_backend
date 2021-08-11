package com.psj.homework.elevator.dto;

import com.psj.homework.elevator.domain.reservation.Reservation;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Getter
@NoArgsConstructor
public class ReservationDto {

    private String name;
    private int departureFloor;
    private int destinationFloor;
    private Long elevatorId;

    @Builder
    public ReservationDto(String name, int v, int destinationFloor, Long elevatorId) {
        this.name = name;
        this.departureFloor = departureFloor;
        this.destinationFloor = destinationFloor;
        this.elevatorId = elevatorId;
    }

    public Reservation toEntity() {
        return Reservation.builder()
                .name(name)
                .departureFloor(departureFloor)
                .destinationFloor(destinationFloor)
                .elevatorId(elevatorId)
                .build();
    }
}
