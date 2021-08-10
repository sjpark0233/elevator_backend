package com.psj.homework.elevator.domain.elevator;

import com.psj.homework.elevator.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Elevator extends BaseTimeEntity {

    public static final Long DEFAULT_ELEVATOR_ID = 1L;
    public static final int INIT_TOP_FLOOR = 15;
    public static final int INIT_BOTTOM_FLOOR = 1;
    public static final int INIT_CURRENT_FLOOR = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int topFloor;

    @Column
    private int bottomFloor;

    @Column
    private int currentFloor;

    @Builder
    public Elevator(int topFloor, int bottomFloor, int currentFloor) {
        this.topFloor = topFloor;
        this.bottomFloor = bottomFloor;
        this.currentFloor = currentFloor;
    }
}
