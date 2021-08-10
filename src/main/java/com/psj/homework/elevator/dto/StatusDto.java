package com.psj.homework.elevator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class StatusDto {

    private final int currentFloor;
    private final List<InternalPersonDto> internalPersons;
    private final List<ExternalPersonDto> externalPersons;

}
