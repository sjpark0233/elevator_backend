package com.psj.homework.elevator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExternalPersonDto {

    private final String name;
    private final int waitingCount;
    private final int currentFloor;
    private final int destinationFloor;
}
