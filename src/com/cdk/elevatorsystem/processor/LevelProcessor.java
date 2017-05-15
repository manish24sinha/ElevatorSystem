package com.cdk.elevatorsystem.processor;

import com.cdk.elevatorsystem.observer.ElevatorObserver;
import com.cdk.elevatorsystem.common.ElevatorState;
import com.cdk.elevatorsystem.common.LevelState;
import com.cdk.elevatorsystem.factory.ElevatorImpl;
import com.cdk.elevatorsystem.factory.LevelImpl;
import com.cdk.elevatorsystem.system.SystemManager;

/**
 * Created by msinha on 5/14/17.
 */
public enum  LevelProcessor implements ElevatorObserver {
    INSTANCE;

    public void processRequest(int currentLevel, LevelState requestLevelState) {
        if(!shouldProcessRequest(currentLevel, requestLevelState)) {
            return;
        }
        processLevelRequest(currentLevel, requestLevelState);
    }


    private boolean shouldProcessRequest(int currentLevel, LevelState requestedState) {
        boolean processRequest = false;
        LevelImpl level = (LevelImpl) SystemManager.getLevel(currentLevel);
        LevelState currentState = level.getCurrentState();
        if(currentState == LevelState.IDLE || currentState != requestedState || currentState != LevelState.PRESSED_UP_DOWN) {
            processRequest = true;
            markCurrentLevelState(level, currentState);
        }
        return processRequest;
    }

    private void markCurrentLevelState(LevelImpl level, LevelState state) {
        level.setCurrentState(state);
    }


    private void processLevelRequest(int currentLevel, LevelState requestedState) {
        SystemManager.processLevelRequest(currentLevel, requestedState);
    }



    public void notifyStateChange(int elevatorId, ElevatorState newState) {
        ElevatorImpl elevator = (ElevatorImpl) SystemManager.getElevator(elevatorId);
        int currentLevel = elevator.getLastStoppedLevel();

        if(newState == ElevatorState.STOPPED) {
            processElevatorStoppedOnLevel(currentLevel);
        }else if(newState == ElevatorState.OPENED) {
            processElevatorOpenedOnLevel(currentLevel);
        }
    }

    public void processElevatorStoppedOnLevel(int currentLevel) {
        LevelImpl level = (LevelImpl) SystemManager.getLevel(currentLevel);
        level.doorOpen();
        markCurrentLevelState(level, LevelState.IDLE);
    }


    public void processElevatorOpenedOnLevel(int currentLevel) {
        LevelImpl level = (LevelImpl) SystemManager.getLevel(currentLevel);
        level.doorClose();
    }

}
