package com.psj.homework.elevator.domain.elevator;

import com.psj.homework.elevator.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Elevator extends BaseTimeEntity {

    public static final Long DEFAULT_ELEVATOR_ID = 1L;  // 엘리베이터 기본 ID

    public static final int INIT_TOP_FLOOR = 15;        // 초기 최상층
    public static final int INIT_BOTTOM_FLOOR = 1;      // 초기 최하층
    public static final int INIT_CURRENT_FLOOR = 10;    // 초기 현재층
    public static final int INIT_ELEVATOR_MAX_PEOPLE = 10;  // 엘리베이터 최대 인원

    public static final char ELEVATOR_DIRECTION_STOP = 'S';     // 엘리베이터 방향 STOP
    public static final char ELEVATOR_DIRECTION_UP = 'U';       // 엘리베이터 방향 UP
    public static final char ELEVATOR_DIRECTION_DOWN = 'D';     // 엘리베이터 방향 DOWN

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int topFloor;

    @Column
    private int bottomFloor;

    @Column
    private int currentFloor;

    @Column
    private char direction;

    @Column
    private int maxPeople;

    @Builder
    public Elevator(int topFloor, int bottomFloor, int currentFloor, int maxPeople) {
        this.topFloor = topFloor;
        this.bottomFloor = bottomFloor;
        this.currentFloor = currentFloor;
        this.maxPeople = maxPeople;

        this.direction = ELEVATOR_DIRECTION_STOP;
    }
}
