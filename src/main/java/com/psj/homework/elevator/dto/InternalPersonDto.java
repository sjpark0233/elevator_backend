package com.psj.homework.elevator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class InternalPersonDto {

    private final String name;
    private final int waitingCount;
    private final int destinationFloor;
}
