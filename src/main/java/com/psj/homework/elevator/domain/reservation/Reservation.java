package com.psj.homework.elevator.domain.reservation;

import com.psj.homework.elevator.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Reservation extends BaseTimeEntity {

    public static final int BOARDING_STATUS_WAITING = 0;
    public static final int BOARDING_STATUS_ON_BOARD = 1;
    public static final int BOARDING_STATUS_GET_OFF = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private int departureFloor;

    @Column
    private int destinationFloor;

    @Column
    private Long elevatorId;

    @Column
    private int waitingExternalCount;

    @Column
    private int waitingInternalCount;

    @Column
    private int boardingStatus;

    @Builder
    public Reservation(String name, int departureFloor, int destinationFloor, Long elevatorId) {
        this.name = name;
        this.departureFloor = departureFloor;
        this.destinationFloor = destinationFloor;
        this.elevatorId = elevatorId;

        this.waitingExternalCount = 0;
        this.waitingInternalCount = 0;
        this.boardingStatus = BOARDING_STATUS_WAITING;
    }
}
