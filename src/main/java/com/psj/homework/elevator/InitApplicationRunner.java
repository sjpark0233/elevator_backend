package com.psj.homework.elevator;

import com.psj.homework.elevator.service.ElevatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 동작 용도
 */
@Order(1)
@Component
public class InitApplicationRunner implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitApplicationRunner.class);

    @Autowired
    private ElevatorService elevatorService;

    /**
     * 엘리베이터 1개 생성
     *
     * @param args
     */
    @Override
    public void run(ApplicationArguments args) {
        Long elevatorId = elevatorService.initElevator();
        LOGGER.info("Init Elevator Id : " + elevatorId);
    }
}
