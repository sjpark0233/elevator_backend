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

    @Builder
    public ElevatorDto(int topFloor, int bottomFloor, int currentFloor) {
        this.topFloor = topFloor;
        this.bottomFloor = bottomFloor;
        this.currentFloor = currentFloor;
    }

    public Elevator toEntity() {
        return Elevator.builder()
                .topFloor(topFloor)
                .bottomFloor(bottomFloor)
                .currentFloor(currentFloor)
                .build();
    }
}
