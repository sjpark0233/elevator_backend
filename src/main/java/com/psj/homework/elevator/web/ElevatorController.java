package com.psj.homework.elevator.web;

import com.psj.homework.elevator.dto.ReservationDto;
import com.psj.homework.elevator.dto.StatusDto;
import com.psj.homework.elevator.service.ElevatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.psj.homework.elevator.domain.elevator.Elevator.DEFAULT_ELEVATOR_ID;

@RequiredArgsConstructor
@RestController
public class ElevatorController {

    @Autowired
    private final ElevatorService elevatorService;

    /**
     * 가동 API
     */
    @PostMapping("/elevator/operate")
    public void operate(Long elevatorId) {

        if (elevatorId == null) {
            elevatorId = DEFAULT_ELEVATOR_ID;
        }

        elevatorService.operateElevator(elevatorId);
    }

    /**
     * 탑승예약 API
     *
     * @param reservationDto 예약정보
     * @return 예약정보 id
     */
    @PostMapping("/elevator/reservation")
    public Long reservation(@RequestBody ReservationDto reservationDto) {
        return elevatorService.setReservation(reservationDto);
    }

    /**
     * 상태확인 API
     *
     * @param elevatorId 엘리베이터 id
     * @return 엘리베이터 상태정보
     */
    @GetMapping(value = {"/elevator/status/{elevatorId}", "/elevator/status"})
    public StatusDto status(@PathVariable(required = false) Long elevatorId) {

        if (elevatorId == null) {
            elevatorId = DEFAULT_ELEVATOR_ID;
        }

        return elevatorService.getElevatorStatus(elevatorId);
    }

    /**
     * 비상정지 API
     *
     * @param elevatorId 엘리베이터 id
     */
    @GetMapping(value = {"/elevator/emergencyStop/{elevatorId}", "/elevator/emergencyStop"})
    public void emergencyStop(@PathVariable(required = false) Long elevatorId) {

        if (elevatorId == null) {
            elevatorId = DEFAULT_ELEVATOR_ID;
        }

        elevatorService.setEmergencyStop(elevatorId);
    }
}
