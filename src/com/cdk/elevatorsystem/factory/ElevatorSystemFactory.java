package com.cdk.elevatorsystem.factory;

import com.cdk.elevatorsystem.common.Constants;
import com.cdk.elevatorsystem.common.ElevatorSystemException;
import com.cdk.elevatorsystem.factory.bean.Elevator;
import com.cdk.elevatorsystem.factory.bean.Level;

/**
 * Created by msinha on 5/13/17.
 */
public enum  ElevatorSystemFactory {
    INSTANCE;
    public Elevator createElevator(final Integer elevatorNum) throws ElevatorSystemException {
        if(elevatorNum == null || elevatorNum < 0) {
            throw new ElevatorSystemException(Constants.INVALID_ELEVATOR_NUMBER);
        }
        return new ElevatorImpl(elevatorNum);
    }


    public Level createLevel(final Integer levelNum)  throws ElevatorSystemException {
        if(levelNum == null) {
            throw new ElevatorSystemException(Constants.INVALID_LEVEL_NUMBER);
        }
        return new LevelImpl(levelNum);
    }
}
