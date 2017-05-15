package com.cdk.elevatorsystem.factory;

import com.cdk.elevatorsystem.common.ElevatorDirection;
import com.cdk.elevatorsystem.observer.ElevatorObserver;
import com.cdk.elevatorsystem.common.ElevatorState;
import com.cdk.elevatorsystem.factory.bean.Elevator;
import com.cdk.elevatorsystem.system.SystemManager;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by msinha on 5/13/17.
 */
public final class ElevatorImpl implements Elevator, Runnable {
    private int elevatorId = -1;
    private int lastLevelStopped = 0;
    final Set<Integer> levelsToStop = new ConcurrentSkipListSet<>();
    ElevatorState currentElevatorState = ElevatorState.WAITING;
    ElevatorDirection currentElevatorDirection = ElevatorDirection.NOT_SET;
    private boolean running = false;
    private int reachedLevel = 0;
    private int upcomingLevel = 0;
    private List<ElevatorObserver> elevatorObservers = new ArrayList<>();

    ElevatorImpl(final int elevatorId) {
        this.elevatorId = elevatorId;
        new Thread(this).start();
    }

    public int getId() {
        return elevatorId;
    }

    public synchronized void open() {
        doorOpen();
        running = false;
        currentElevatorState = ElevatorState.OPENED;
        notifyObservers(currentElevatorState);
    }

    public synchronized void close() {
        doorClose();
        running = false;
        currentElevatorState = ElevatorState.CLOSED;
        notifyObservers(currentElevatorState);
    }

    public void alarm() {

    }

    public synchronized void doWait() {
        try {
            this.wait();
        }catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        processAfterNotified();
    }


    public synchronized void processAfterNotified() {
        if(levelsToStop.isEmpty()) {
            doWait();
        }

        if(levelsToStop.contains(lastLevelStopped)) {
            open();
        }else {
            int destinedLevel = levelsToStop.iterator().next();
            if (destinedLevel < lastLevelStopped) {
                currentElevatorDirection = ElevatorDirection.MOVING_DOWN;
            }else {
                currentElevatorDirection = ElevatorDirection.MOVING_UP;
            }
            move();
        }
    }

    private void doorOpen() {
        System.out.println("Elevator " + elevatorId + " door opened on level " + lastLevelStopped);
    }

    private void doorClose() {
        System.out.println("Elevator " + elevatorId + " door closed on level " + lastLevelStopped);
    }


    public synchronized void stop(int level) {
        running = false;
        lastLevelStopped = level;
        levelsToStop.remove(level);
        currentElevatorState = ElevatorState.STOPPED;
        notifyObservers(currentElevatorState);
    }

    public synchronized void move() {
        if(currentElevatorState == ElevatorState.MOVING) {
            reachedLevel = upcomingLevel;
        }

        if(levelsToStop.isEmpty()) {
            currentElevatorState = ElevatorState.WAITING;
            currentElevatorDirection = ElevatorDirection.NOT_SET;
            reachedLevel = lastLevelStopped;
            notifyObservers(ElevatorState.WAITING);
            return;
        }

        if(currentElevatorDirection == ElevatorDirection.MOVING_UP) {
            if(!hasAnyLevelGreaterThanCurrent(reachedLevel)) {
                currentElevatorDirection = ElevatorDirection.MOVING_DOWN;
                upcomingLevel = reachedLevel - 1;
            }else {
                upcomingLevel = reachedLevel + 1;
            }
        }else if(currentElevatorDirection == ElevatorDirection.MOVING_DOWN) {
            if(!hasAnyLevelLessThanCurrent(lastLevelStopped)) {
                currentElevatorDirection = ElevatorDirection.MOVING_UP;
                upcomingLevel = reachedLevel + 1;
            }else {
                upcomingLevel = reachedLevel - 1;
            }
        }

        running = true;
        currentElevatorState = ElevatorState.MOVING;
        notifyObservers(ElevatorState.MOVING);
    }




    private synchronized boolean hasAnyLevelGreaterThanCurrent(int currentLevelId) {
        boolean result = false;
        for(Integer level : levelsToStop) {
            if(level > currentLevelId) {
                result = true;
                break;
            }
        }
        return result;
    }


    private synchronized boolean hasAnyLevelLessThanCurrent(int currentLevelId) {
        boolean result = false;
        for(Integer level : levelsToStop) {
            if(level < currentLevelId) {
                result = true;
                break;
            }
        }
        return result;
    }


    @Override
    public void run() {
        startServing();
    }


    public void startServing() {
        notifyObservers(ElevatorState.WAITING);
    }


    private void notifyObservers(ElevatorState newState) {
        for(ElevatorObserver observer : elevatorObservers) {
            observer.notifyStateChange(elevatorId, newState);
        }
    }

    public int getUpcomingLevel() {
        return upcomingLevel;
    }

    public void setUpcomingLevel(int upcomingLevel) {
        this.upcomingLevel = upcomingLevel;
    }

    public int getReachedLevel() {
        return reachedLevel;
    }

    public void setReachedLevel(int reachedLevel) {
        this.reachedLevel = reachedLevel;
    }

    public synchronized void addLevelToStop(int levelId) {
        levelsToStop.add(levelId);
        this.notifyAll();
    }


    public void setCurrentElevatorState(ElevatorState elevatorState) {
        this.currentElevatorState = elevatorState;
    }


    public synchronized ElevatorState getCurrentElevatorState() {
        return currentElevatorState;
    }


    public ElevatorDirection getCurrentElevatorDirection() {
        return currentElevatorDirection;
    }

    public void setCurrentElevatorDirection(ElevatorDirection currentElevatorDirection) {
        this.currentElevatorDirection = currentElevatorDirection;
    }

    public synchronized int getLastStoppedLevel() {
        return lastLevelStopped;
    }

    public synchronized Set<Integer> getLevelsToStop() {
        return Collections.unmodifiableSet(levelsToStop);
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }


    public void addObserver(ElevatorObserver elevatorObserver) {
        elevatorObservers.add(elevatorObserver);
    }

    public void removeObserver(ElevatorObserver elevatorObserver) {
        elevatorObservers.remove(elevatorObserver);
    }
}
