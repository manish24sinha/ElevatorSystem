package com.cdk.elevatorsystem.system;

import com.cdk.elevatorsystem.common.ElevatorState;
import com.cdk.elevatorsystem.common.ElevatorSystemException;
import com.cdk.elevatorsystem.common.LevelState;
import com.cdk.elevatorsystem.factory.ElevatorImpl;
import com.cdk.elevatorsystem.factory.ElevatorSystemFactory;
import com.cdk.elevatorsystem.factory.LevelImpl;
import com.cdk.elevatorsystem.processor.ElevatorProcessor;
import com.cdk.elevatorsystem.processor.LevelProcessor;

/**
 * Created by msinha on 5/15/17.
 */
public final class SystemClient {
    private SystemClient(){}
    public static synchronized void createElevator(int noOfElevators) throws ElevatorSystemException {
        int elevatorId = SystemManager.getElevators().size();;
        for(int cnt=0; cnt < noOfElevators; cnt++) {
            elevatorId = elevatorId + 1;
            ElevatorImpl elevator = (ElevatorImpl) ElevatorSystemFactory.INSTANCE.createElevator(elevatorId);
            //add observer
            elevator.addObserver(ElevatorProcessor.INSTANCE);
            elevator.addObserver(ElevatorProcessor.INSTANCE);
            SystemManager.addNewElevator(elevator);
        }
    }


    public static synchronized void createLevel(Integer levelId) throws ElevatorSystemException {
        LevelImpl level = (LevelImpl) ElevatorSystemFactory.INSTANCE.createLevel(levelId);
        SystemManager.addNewLevel(level);
    }


    public static synchronized void pressLevelUp(Integer currentLevel) throws ElevatorSystemException {
        if(currentLevel > SystemManager.getHighestLevel() || currentLevel < SystemManager.getLowestLevel()) {
            throw new ElevatorSystemException("Level does not exist!");
        }
        LevelProcessor.INSTANCE.processRequest(currentLevel, LevelState.PRESSED_UP);
    }


    public static synchronized void pressLevelDown(Integer currentLevel) {
        LevelProcessor.INSTANCE.processRequest(currentLevel, LevelState.PRESSED_DOWN);
    }
}
