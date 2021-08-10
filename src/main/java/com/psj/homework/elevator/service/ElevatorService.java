package com.psj.homework.elevator.service;

import com.psj.homework.elevator.InitApplicationRunner;
import com.psj.homework.elevator.domain.elevator.Elevator;
import com.psj.homework.elevator.domain.elevator.ElevatorRepository;
import com.psj.homework.elevator.domain.reservation.Reservation;
import com.psj.homework.elevator.domain.reservation.ReservationRepository;
import com.psj.homework.elevator.dto.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static com.psj.homework.elevator.domain.elevator.Elevator.*;
import static com.psj.homework.elevator.domain.reservation.Reservation.*;

@RequiredArgsConstructor
@Service
public class ElevatorService {

    @Autowired
    private final ElevatorRepository elevatorRepository;

    @Autowired
    private final ReservationRepository reservationRepository;

    /**
     * 엘리베이터 초기화
     *
     * @return 엘리베이터 id
     */
    @Transactional
    public Long initElevator() {
        ElevatorDto elevatorDto = ElevatorDto.builder()
                .topFloor(INIT_TOP_FLOOR)
                .bottomFloor(INIT_BOTTOM_FLOOR)
                .currentFloor(INIT_CURRENT_FLOOR)
                .build();

        return elevatorRepository.save(elevatorDto.toEntity()).getId();
    }

    public void operateElevator(Long elevatorId) {
        //TODO
    }

    /**
     * 탑승예약 서비스
     *
     * @param reservationDto 예약정보
     * @return 예약정보 id
     */
    public Long setReservation(ReservationDto reservationDto) {
        return reservationRepository.save(reservationDto.toEntity()).getId();
    }

    /**
     * 상태확인 서비스
     *
     * @param elevatorId 엘리베이터 id
     * @return 엘리베이터 상태정보
     */
    @Transactional
    public StatusDto getElevatorStatus(Long elevatorId) {

        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(() -> new IllegalArgumentException("Not exists Elevator!"));
        List<Reservation> reservations = reservationRepository.findAllExceptGetOff(elevatorId);

        int currentFloor = elevator.getCurrentFloor();  // 현재 위치
        List<InternalPersonDto> internalPersons = new ArrayList<>();    // 내부인원
        List<ExternalPersonDto> externalPersons = new ArrayList<>();    // 외부인원

        for (Reservation reservation : reservations) {

            String name = reservation.getName();
            int waitingInternalCount = reservation.getWaitingInternalCount();
            int waitingExternalCount = reservation.getWaitingExternalCount();
            int departureFloor = reservation.getDepartureFloor();
            int destinationFloor = reservation.getDestinationFloor();

            if (reservation.getBoardingStatus() == BOARDING_STATUS_WAITING) {
                internalPersons.add(new InternalPersonDto(name, waitingInternalCount, destinationFloor));
            } else if (reservation.getBoardingStatus() == BOARDING_STATUS_ON_BOARD) {
                externalPersons.add(new ExternalPersonDto(name, waitingExternalCount, departureFloor, destinationFloor));
            }
        }

        return new StatusDto(currentFloor, internalPersons, externalPersons);
    }

    /**
     * 비상정지 서비스
     *
     * @param elevatorId 엘리베이터 id
     */
    @Transactional
    public void setEmergencyStop(Long elevatorId) {
        List<Reservation> reservations = reservationRepository.findAllOnBoard(elevatorId);

        if (reservations != null && reservations.size() > 0) {
            for (Reservation reservation : reservations) {
                reservation.setBoardingStatus(BOARDING_STATUS_GET_OFF);
            }
            reservationRepository.saveAll(reservations);
        }
    }
}
