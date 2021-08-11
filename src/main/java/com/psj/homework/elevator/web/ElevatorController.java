package com.psj.homework.elevator.web;

import com.psj.homework.elevator.dto.ReservationDto;
import com.psj.homework.elevator.dto.ResponseDto;
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
    @GetMapping(value = {"/elevator/operate/{elevatorId}","/elevator/operate"})
    public StatusDto operate(@PathVariable(required = false) Long elevatorId) {

        if (elevatorId == null) {
            elevatorId = DEFAULT_ELEVATOR_ID;
        }

        return elevatorService.operateElevator(elevatorId);
    }

    /**
     * 탑승예약 API
     *
     * @param reservationDto 예약정보
     * @return 응답메시지
     */
    @PostMapping("/elevator/reservation")
    public ResponseDto reservation(@RequestBody ReservationDto reservationDto) {
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
     * @return 응답메시지
     */
    @GetMapping(value = {"/elevator/emergencyStop/{elevatorId}", "/elevator/emergencyStop"})
    public ResponseDto emergencyStop(@PathVariable(required = false) Long elevatorId) {

        if (elevatorId == null) {
            elevatorId = DEFAULT_ELEVATOR_ID;
        }

        return elevatorService.setEmergencyStop(elevatorId);
    }
}
