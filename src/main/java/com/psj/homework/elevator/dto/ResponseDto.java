package com.psj.homework.elevator.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseDto {

    private final boolean success;
    private final String message;
}
