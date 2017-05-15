package com.cdk.elevatorsystem.processor;

import com.cdk.elevatorsystem.observer.ElevatorObserver;
import com.cdk.elevatorsystem.common.ElevatorState;
import com.cdk.elevatorsystem.factory.ElevatorImpl;
import com.cdk.elevatorsystem.factory.bean.Elevator;
import com.cdk.elevatorsystem.system.SystemManager;

import java.util.Scanner;
import java.util.Set;

/**
 * Created by msinha on 5/14/17.
 */
public enum ElevatorProcessor implements ElevatorObserver {
    INSTANCE;

    public void addLevelToStop(Elevator elevator, Integer levelId) {
        ElevatorImpl elevatorImpl = (ElevatorImpl)elevator;
        elevatorImpl.addLevelToStop(levelId);
    }


    /*public void pauseProcessing() {
        Collection<Elevator> elevators = SystemManager.getElevators();
        for(Elevator elevator : elevators) {
           //pauseProcessing(elevator);
        }
    }


    public void resumeProcessing() {
        Collection<Elevator> elevators = SystemManager.getElevators();
        for(Elevator elevator : elevators) {
            //resumeProcessing(elevator);
        }
    }


    public void pauseProcessing(Elevator elevator) {
        ElevatorImpl elevatorImpl = (ElevatorImpl)elevator;
        elevatorImpl.pause();
    }


    public void resumeProcessing(Elevator elevator) {
        ElevatorImpl elevatorImpl = (ElevatorImpl)elevator;
        elevatorImpl.resume();
    }*/


    public void notifyStateChange(int elevatorId, ElevatorState newState) {
        ElevatorImpl elevator = (ElevatorImpl) SystemManager.getElevator(elevatorId);

        if(newState == ElevatorState.WAITING) {
            processWait(elevator);
        }else if(newState == ElevatorState.STOPPED) {
            processStopped(elevator);
        }else if(newState == ElevatorState.CLOSED) {
            processClosed(elevator);
        }else if(newState == ElevatorState.OPENED) {
            processOpened(elevator);
        }else if(newState == ElevatorState.MOVING) {
            processMoving(elevator);
        }
    }


    private void processWait(ElevatorImpl elevator) {
        //make elevator wait
        System.out.println("Elevator " + elevator.getId() + " is waiting on level " + elevator.getLastStoppedLevel());
        elevator.doWait();
    }


    private void processStopped(ElevatorImpl elevator) {
        System.out.println("\nElevator " + elevator.getId() + " is stopped on level " + elevator.getLastStoppedLevel());
        elevator.open();
    }


    private void processClosed(ElevatorImpl elevator) {
        elevator.move();
    }


    private void processOpened(ElevatorImpl elevator) {
        Set<Integer> levelsToStop = elevator.getLevelsToStop();

        System.out.println("Do you want to enter the elevator " + elevator.getId() + " (y/n) ? ");
        String istAns = readInput();
        if(istAns != null && istAns.equalsIgnoreCase("y")) {
            while(levelsToStop.isEmpty()) {
                System.out.println("Please press the floor no: ");
                String strLevel = readInput();
                int level = 0;
                try {
                    level = Integer.parseInt(strLevel);
                    if (level > SystemManager.getHighestLevel() || level < SystemManager.getLowestLevel()) {
                        System.out.println("Level does not exist. Try again!");
                    } else {
                        elevator.addLevelToStop(level);
                    }
                } catch (Exception ex) {
                    System.out.println("Incorrect floor no entered. Try again!");
                }
                levelsToStop = elevator.getLevelsToStop();
            }
        }
        elevator.close();
    }


    private void processMoving(ElevatorImpl elevator) {
        int reachedLevel = elevator.getReachedLevel();
        Set<Integer> levelsToStop = elevator.getLevelsToStop();
        if(levelsToStop.contains(reachedLevel)) {
            elevator.stop(reachedLevel);
        }else if(levelsToStop.contains(elevator.getLastStoppedLevel())) {
            elevator.stop(elevator.getLastStoppedLevel());
        }else {
            System.out.println("Elevator " + elevator.getId() + " is passing level " + reachedLevel);
            elevator.move();
        }
    }


    private String readInput() {
        Scanner scanner = new Scanner(System.in);
        String strLevel = scanner.nextLine();
        return strLevel;
    }
}
