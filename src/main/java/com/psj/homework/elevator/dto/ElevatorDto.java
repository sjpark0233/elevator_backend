package com.psj.homework.elevator.dto;

import com.psj.homework.elevator.domain.elevator.Elevator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ElevatorDto {

    private int topFloor;
    private int bottomFloor;
    private int currentFloor;
    private int maxPeople;

    @Builder
    public ElevatorDto(int topFloor, int bottomFloor, int currentFloor, int maxPeople) {
        this.topFloor = topFloor;
        this.bottomFloor = bottomFloor;
        this.currentFloor = currentFloor;
        this.maxPeople = maxPeople;
    }

    public Elevator toEntity() {
        return Elevator.builder()
                .topFloor(topFloor)
                .bottomFloor(bottomFloor)
                .currentFloor(currentFloor)
                .maxPeople(maxPeople)
                .build();
    }
}
