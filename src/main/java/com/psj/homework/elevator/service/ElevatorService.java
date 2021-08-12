package com.psj.homework.elevator.service;

import com.psj.homework.elevator.domain.elevator.Elevator;
import com.psj.homework.elevator.domain.elevator.ElevatorRepository;
import com.psj.homework.elevator.domain.reservation.Reservation;
import com.psj.homework.elevator.domain.reservation.ReservationRepository;
import com.psj.homework.elevator.dto.*;
import lombok.RequiredArgsConstructor;
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
     * 애플리케이션 시작 시 호출 용도
     *
     * @return 엘리베이터 id
     */
    @Transactional
    public Long initElevator() {
        ElevatorDto elevatorDto = ElevatorDto.builder()
                .topFloor(INIT_TOP_FLOOR)
                .bottomFloor(INIT_BOTTOM_FLOOR)
                .currentFloor(INIT_CURRENT_FLOOR)
                .maxPeople(INIT_ELEVATOR_MAX_PEOPLE)
                .build();

        return elevatorRepository.save(elevatorDto.toEntity()).getId();
    }

    /**
     * 가동 서비스
     *
     * @param elevatorId 엘리베이터 id
     * @return 엘리베이터 상태정보
     */
    @Transactional
    public StatusDto operateElevator(Long elevatorId) {

        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 엘리베이터입니다!"));
        List<Reservation> waitingResvs = reservationRepository.findAllWaiting(elevatorId);      // 예약정보 (외부대기자)
        List<Reservation> boardingResvs = reservationRepository.findAllOnBoard(elevatorId);     // 예약정보 (탑승자)

        int currentElevatorFloor = elevator.getCurrentFloor();          // 현재 엘리베이터층

        // 대기자 or 탑승자가 있는 경우 동작함
        if ((waitingResvs != null && waitingResvs.size() > 0) || (boardingResvs != null && boardingResvs.size() > 0)) {

            Integer topFloorDown = reservationRepository.findTopFloorDown(elevatorId);      // 대기자&탑승자 중 가장 높은 층
            Integer bottomFloorUp = reservationRepository.findBottomFloorUp(elevatorId);    // 대기자&탑승자 중 가장 낮은 층

            List<Reservation> currentFloorWaitingResvs = new ArrayList<>(); // 출발층이 현재층인 외부대기자 중, 엘리베이터 방향이 맞는 자
            for (Reservation reservation : waitingResvs) {
                if (reservation.getDepartureFloor() == currentElevatorFloor
                        && (
                                elevator.getDirection() ==  reservation.getDirection()
                                || elevator.getDirection() == ELEVATOR_DIRECTION_STOP
                                || (topFloorDown != null && currentElevatorFloor == topFloorDown)
                                || (bottomFloorUp != null && currentElevatorFloor == bottomFloorUp)
                        )
                ) {
                    currentFloorWaitingResvs.add(reservation);
                }
            }

            // 현재 층에서 승차할 사람이 있는 경우 이동하지 않고 승차함 (최대인원 미만 시)
            // 이동하지 않았으므로 하차할 사람은 없음
            if (currentFloorWaitingResvs.size() > 0 && boardingResvs.size() < elevator.getMaxPeople()) {

                // 기 탑승자 대기횟수 + 1
                if (boardingResvs != null && boardingResvs.size() > 0) {
                    for (Reservation reservation : boardingResvs) {
                        reservation.setWaitingInternalCount(reservation.getWaitingInternalCount() + 1);
                    }
                    reservationRepository.saveAll(boardingResvs);
                }

                // 현재층 대기자 탑승 처리
                this.setStatusOnBoarding(elevatorId, currentFloorWaitingResvs);

                // 엘리베이터 방향 전환
                if (elevator.getDirection() == ELEVATOR_DIRECTION_STOP) {
                    if (currentElevatorFloor < currentFloorWaitingResvs.get(0).getDestinationFloor()) {
                        elevator.setDirection(ELEVATOR_DIRECTION_UP);
                    } else if (currentElevatorFloor > currentFloorWaitingResvs.get(0).getDestinationFloor()) {
                        elevator.setDirection(ELEVATOR_DIRECTION_DOWN);
                    }
                }
                elevatorRepository.save(elevator);

                // 출발층이 다른층인 외부대기자는 대기횟수 +1
                List<Reservation> otherFloorWaitingResvs = new ArrayList<>();
                for (Reservation reservation : waitingResvs) {
                    if (reservation.getDepartureFloor() != currentElevatorFloor) {
                        reservation.setWaitingExternalCount(reservation.getWaitingExternalCount() + 1);
                        otherFloorWaitingResvs.add(reservation);
                    }
                }
                if (otherFloorWaitingResvs.size() > 0) {
                    reservationRepository.saveAll(otherFloorWaitingResvs);
                }

            } else {

                // 엘리베이터 중지 상태이면 방향 전환
                if (elevator.getDirection() == ELEVATOR_DIRECTION_STOP) {
                    if (currentElevatorFloor == elevator.getBottomFloor() || currentElevatorFloor < waitingResvs.get(0).getDepartureFloor()) {
                        elevator.setDirection(ELEVATOR_DIRECTION_UP);
                    } else if (currentElevatorFloor == elevator.getTopFloor() || currentElevatorFloor > waitingResvs.get(0).getDepartureFloor()) {
                        elevator.setDirection(ELEVATOR_DIRECTION_DOWN);
                    }
                }
                elevatorRepository.save(elevator);


                // 엘리베이터 이동
                if (elevator.getDirection() == ELEVATOR_DIRECTION_UP) {
                    currentElevatorFloor += 1;
                } else if (elevator.getDirection() == ELEVATOR_DIRECTION_DOWN) {
                    currentElevatorFloor -= 1;
                }
                elevator.setCurrentFloor(currentElevatorFloor);
                elevatorRepository.save(elevator);

                // 이동 후 탑승자 하차
                List<Reservation> currentFloorGetOffResvs = new ArrayList<>();  // 도착층이 현재층인 탑승자
                for (Reservation reservation : boardingResvs) {
                    if (reservation.getDestinationFloor() == currentElevatorFloor) {
                        currentFloorGetOffResvs.add(reservation);
                    }

                    // 탑승자 모두 내부대기횟수 + 1
                    reservation.setWaitingInternalCount(reservation.getWaitingInternalCount() + 1);
                }
                if (boardingResvs.size() > 0) {
                    reservationRepository.saveAll(boardingResvs);
                }
                this.setStatusGetOff(currentFloorGetOffResvs);

                // 이동 후 승차대기자 승차
                topFloorDown = reservationRepository.findTopFloorDown(elevatorId);      // 대기자&탑승자 중 가장 높은 층
                bottomFloorUp = reservationRepository.findBottomFloorUp(elevatorId);    // 대기자&탑승자 중 가장 낮은 층
                currentFloorWaitingResvs.clear();   // 출발층이 현재층인 외부대기자 중, 엘리베이터 방향이 맞는 자
                for (Reservation reservation : waitingResvs) {
                    if (reservation.getDepartureFloor() == currentElevatorFloor
                        && (
                            elevator.getDirection() == reservation.getDirection()
                            || elevator.getDirection() == ELEVATOR_DIRECTION_STOP
                            || (topFloorDown != null && currentElevatorFloor == topFloorDown)
                            || (bottomFloorUp != null && currentElevatorFloor == bottomFloorUp)
                        )
                    ) {
                        currentFloorWaitingResvs.add(reservation);
                    }

                    // 승차대기자 모두 외부대기횟수 + 1
                    reservation.setWaitingExternalCount(reservation.getWaitingExternalCount() + 1);
                }
                if (waitingResvs.size() > 0) {
                    reservationRepository.saveAll(waitingResvs);
                }
                this.setStatusOnBoarding(elevatorId, currentFloorWaitingResvs);

                // 승하차가 끝난 후 엘리베이터 방향 전환
                List<Reservation> restResvs = reservationRepository.findAllExceptGetOff(elevatorId);    // 외부대기자 and 탑승자

                if (restResvs == null || restResvs.size() < 1) {

                    elevator.setDirection(ELEVATOR_DIRECTION_STOP);

                } else if (elevator.getDirection() == ELEVATOR_DIRECTION_UP) {

                    if (currentElevatorFloor >= elevator.getTopFloor()) {
                        elevator.setDirection(ELEVATOR_DIRECTION_DOWN);
                    } else {
                        boolean flag = false;

                        for (Reservation reservation : restResvs) {
                            if (reservation.getBoardingStatus() == BOARDING_STATUS_WAITING && currentElevatorFloor < reservation.getDepartureFloor()) {
                                flag = true;
                                break;
                            }
                            if (reservation.getBoardingStatus() == BOARDING_STATUS_ON_BOARD && currentElevatorFloor < reservation.getDestinationFloor()) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            elevator.setDirection(ELEVATOR_DIRECTION_DOWN);
                        }
                    }

                } else if (elevator.getDirection() == ELEVATOR_DIRECTION_DOWN) {

                    if (currentElevatorFloor <= elevator.getBottomFloor()) {
                        elevator.setDirection(ELEVATOR_DIRECTION_UP);
                    } else {
                        boolean flag = false;

                        for (Reservation reservation : restResvs) {
                            if (reservation.getBoardingStatus() == BOARDING_STATUS_WAITING && currentElevatorFloor > reservation.getDepartureFloor()) {
                                flag = true;
                                break;
                            }
                            if (reservation.getBoardingStatus() == BOARDING_STATUS_ON_BOARD && currentElevatorFloor > reservation.getDestinationFloor()) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            elevator.setDirection(ELEVATOR_DIRECTION_UP);
                        }
                    }
                }
                elevatorRepository.save(elevator);
            }
        } else {
            elevator.setDirection(ELEVATOR_DIRECTION_STOP);
            elevatorRepository.save(elevator);
        }

        return getElevatorStatus(elevatorId);
    }

    /**
     * 탑승예약 서비스
     *
     * @param reservationDto 예약정보
     * @return 예약정보 id
     */
    public ResponseDto setReservation(ReservationDto reservationDto) {

        System.out.println(reservationDto.getName() + " " + reservationDto.getElevatorId() + reservationDto.getDestinationFloor());

        try {
            Long elevatorId = reservationDto.getElevatorId();
            Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 엘리베이터입니다!"));
            int departureFloor = reservationDto.getDepartureFloor();
            int destinationFloor = reservationDto.getDestinationFloor();

            if (departureFloor == destinationFloor) {
                throw new Exception("출발층과 도착층이 같을 수 없습니다.");
            }

            if (departureFloor < elevator.getBottomFloor() || departureFloor > elevator.getTopFloor()) {
                throw new Exception("출발 층수 범위가 벗어났습니다.");
            }

            if (destinationFloor < elevator.getBottomFloor() || destinationFloor > elevator.getTopFloor()) {
                throw new Exception("도착 층수 범위가 벗어났습니다.");
           }

            reservationRepository.save(reservationDto.toEntity()).getId();
        } catch (Exception e) {
            return new ResponseDto(false, e.getMessage());
        }

        return new ResponseDto(true, "");
    }

    /**
     * 상태확인 서비스
     *
     * @param elevatorId 엘리베이터 id
     * @return 엘리베이터 상태정보
     */
    @Transactional
    public StatusDto getElevatorStatus(Long elevatorId) {

        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 엘리베이터입니다!"));
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
                externalPersons.add(new ExternalPersonDto(name, waitingExternalCount, departureFloor, destinationFloor));
            } else if (reservation.getBoardingStatus() == BOARDING_STATUS_ON_BOARD) {
                internalPersons.add(new InternalPersonDto(name, waitingInternalCount, destinationFloor));
            }
        }

        return new StatusDto(currentFloor, internalPersons, externalPersons);
    }

    /**
     * 비상정지 서비스
     *
     * @param elevatorId 엘리베이터 id
     * @return 응답메시지
     */
    @Transactional
    public ResponseDto setEmergencyStop(Long elevatorId) {

        try {
            List<Reservation> onBoardResvs = reservationRepository.findAllOnBoard(elevatorId);  // 탑승자
            List<Reservation> waitingResvs = reservationRepository.findAllWaiting(elevatorId);  // 외부대기자

            // 탑승자 전원 하차
            if (onBoardResvs != null && onBoardResvs.size() > 0) {
                for (Reservation reservation : onBoardResvs) {
                    reservation.setBoardingStatus(BOARDING_STATUS_GET_OFF);
                }
                reservationRepository.saveAll(onBoardResvs);
            }

            // 외부대기자 대기횟수 + 1
            if (waitingResvs != null && waitingResvs.size() > 0) {
                for (Reservation reservation : waitingResvs) {
                    reservation.setWaitingExternalCount(reservation.getWaitingExternalCount() + 1);
                }
                reservationRepository.saveAll(waitingResvs);
            }

            // 엘리베이터 정지
            Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 엘리베이터입니다!"));

            elevator.setDirection(ELEVATOR_DIRECTION_STOP);
            elevatorRepository.save(elevator);
        } catch (Exception e) {
            return new ResponseDto(false, e.getMessage());
        }

        return new ResponseDto(true, "");
    }

    /**
     * 탑승 처리
     * 최대 인원이 넘지 않게 탑승
     *
     * @param elevatorId    엘리베이터 id
     * @param targetResvs   탑승대상 예약정보
     */
    private void setStatusOnBoarding(Long elevatorId, List<Reservation> targetResvs) {

        Elevator elevator = elevatorRepository.findById(elevatorId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 엘리베이터입니다!"));
        int maxPeople = elevator.getMaxPeople();
        int currentCount = reservationRepository.findAllOnBoard(elevatorId).size(); // 현재 탑승 인원

        if (targetResvs != null) {
            for (Reservation reservation : targetResvs) {
                if (currentCount < maxPeople) {
                    reservation.setBoardingStatus(BOARDING_STATUS_ON_BOARD);
                    currentCount++;
                } else {
                    reservation.setWaitingExternalCount(reservation.getWaitingExternalCount() + 1); // 탑승하지 못하였으므로 외부대기횟수 + 1
                    break;
                }
            }

            reservationRepository.saveAll(targetResvs);
        }
    }


    /**
     * 하차 처리
     *
     * @param targetResvs   하차대상 예약정보
     */
    private void setStatusGetOff(List<Reservation> targetResvs) {

        if (targetResvs != null) {
            for (Reservation reservation : targetResvs) {
                reservation.setBoardingStatus(BOARDING_STATUS_GET_OFF);
            }

            reservationRepository.saveAll(targetResvs);
        }
    }
}
