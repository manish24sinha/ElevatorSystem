package com.cdk.elevatorsystem.system;

import com.cdk.elevatorsystem.common.ElevatorDirection;
import com.cdk.elevatorsystem.common.ElevatorState;
import com.cdk.elevatorsystem.common.LevelState;
import com.cdk.elevatorsystem.factory.ElevatorImpl;
import com.cdk.elevatorsystem.factory.LevelImpl;
import com.cdk.elevatorsystem.factory.bean.Elevator;
import com.cdk.elevatorsystem.factory.bean.Level;
import com.cdk.elevatorsystem.processor.ElevatorProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by msinha on 5/13/17.
 */
public final class SystemManager {
    private SystemManager() {
    }
    private static final Map<Integer, Elevator> elevators = new ConcurrentHashMap<>();
    private static final Map<Integer, Level> levels = new ConcurrentHashMap<>();
    private static final Map<Integer, List<Integer>> levelsClosestElevatorsForMoveUp = new HashMap<>();
    private static final Map<Integer, List<Integer>> levelsClosestElevatorsForMoveDown = new HashMap<>();
    private static final ElevatorProcessor elevatorProcessor = ElevatorProcessor.INSTANCE;
    private static int lowestLevel = 0;
    private static int highestLevel = 0;



    public static synchronized void processLevelRequest(Integer currentLevel, LevelState levelState) {
        ElevatorImpl elevator = getClosestElevator(currentLevel, levelState);
        //elevatorProcessor.pauseProcessing();
        if((elevator.getLastStoppedLevel() != currentLevel) ||
                (elevator.getCurrentElevatorState() != ElevatorState.WAITING && elevator.isRunning())) {
            elevatorProcessor.addLevelToStop(elevator, currentLevel);
            //re-evaluate closest elevator logic
            evaluateClosestElevators(currentLevel, levelState);
        }
        //elevatorProcessor.resumeProcessing();
    }


    private static synchronized ElevatorImpl getClosestElevator(Integer currentLevel, LevelState levelState) {
        evaluateClosestElevators(currentLevel, levelState);
        ElevatorImpl elevator = null;
        if(levelState == LevelState.PRESSED_UP) {
            //first get the closest elevator
            Integer elevatorId = levelsClosestElevatorsForMoveUp.get(currentLevel).remove(0);
            elevator = (ElevatorImpl) elevators.get(elevatorId);
        }else if(levelState == LevelState.PRESSED_DOWN) {
            Integer elevatorId = levelsClosestElevatorsForMoveDown.get(currentLevel).remove(0);
            elevator = (ElevatorImpl)elevators.get(elevatorId);
        }
        return elevator;
    }



    public static synchronized void evaluateClosestElevators(Integer currentLevel, LevelState levelState) {
        if(levelState == LevelState.PRESSED_UP) {
            evaluateClosestElevatorsForMoveUp(currentLevel);
        }else {
            evaluateClosestElevatorsForMoveDown(currentLevel);
        }
    }



    private static synchronized void evaluateClosestElevatorsForMoveUp(Integer currentLevel) {
        evaluateClosestElevatorsForAll(levelsClosestElevatorsForMoveUp, currentLevel);
    }

    private static synchronized void evaluateClosestElevatorsForMoveDown(Integer currentLevel) {
        evaluateClosestElevatorsForAll(levelsClosestElevatorsForMoveDown, currentLevel);
    }


    private static synchronized void evaluateClosestElevatorsForAll(Map<Integer, List<Integer>> lowestClosestElevators,
                                                                    Integer thisLevelId) {
        LevelImpl thisLevel = (LevelImpl) levels.get(thisLevelId);
        Collection<Elevator> allElevators = elevators.values();
        List<Integer> closestElevators = new ArrayList<>();

        int leastDistance = levels.size() - 1;
        for (Elevator elevator : allElevators) {
            ElevatorImpl thisElevator = (ElevatorImpl) elevator;
            int lastElevatorLevelId = thisElevator.getLastStoppedLevel();
            int thisDistance = levels.size() - 1;
            if (lastElevatorLevelId == thisLevelId) {
                thisDistance = findDistanceForSameLevels();
            } else if (lastElevatorLevelId < thisLevelId) {
                thisDistance = findDistanceWhenElevatorLastLevelIsBelowThisLevel(thisLevelId, thisElevator);
            } else {
                //lastElevatorLevelId > thisLevelId
                thisDistance = findDistanceWhenElevatorLastLevelIsAboveThisLevel(thisLevelId, thisElevator);
            }

            if (thisDistance < leastDistance) {
                leastDistance = thisDistance;
                closestElevators = new ArrayList<>();
                closestElevators.add(thisElevator.getId());
            } else if (thisDistance == leastDistance) {
                closestElevators.add(thisElevator.getId());
            }
        }

        thisLevel.setDistanceFromClosestElevators(leastDistance);
        lowestClosestElevators.put(thisLevelId, closestElevators);
    }



    private static int findDistanceForSameLevels() {
        return 0;
    }



    private static int findDistanceWhenElevatorLastLevelIsBelowThisLevel(int thisLevelId, ElevatorImpl thisElevator) {
        int thisDistance = levels.size() - 1;
        int lastElevatorLevelId = thisElevator.getLastStoppedLevel();
        if(thisElevator.getCurrentElevatorState() == ElevatorState.WAITING ||
                thisElevator.getCurrentElevatorDirection() == ElevatorDirection.MOVING_UP) {
            thisDistance = Math.abs(thisLevelId - lastElevatorLevelId);
        }else if(thisElevator.getCurrentElevatorDirection() == ElevatorDirection.MOVING_DOWN){
            //moving down
            Set<Integer> levelsToStop = thisElevator.getLevelsToStop();
            int lowestDestinedLevel = getLowestDestinedLevel(levelsToStop);
            thisDistance = Math.abs((lastElevatorLevelId - lowestDestinedLevel) + (thisLevelId - lowestDestinedLevel));
        }
        return thisDistance;
    }


    private static int findDistanceWhenElevatorLastLevelIsAboveThisLevel(int thisLevelId, ElevatorImpl thisElevator) {
        int thisDistance = levels.size() - 1;
        int lastElevatorLevelId = thisElevator.getLastStoppedLevel();
        if(thisElevator.getCurrentElevatorState() == ElevatorState.WAITING ||
                thisElevator.getCurrentElevatorDirection() == ElevatorDirection.MOVING_DOWN) {
            thisDistance = Math.abs(lastElevatorLevelId - thisLevelId);
        }
        else if(thisElevator.getCurrentElevatorDirection() == ElevatorDirection.MOVING_UP){
            //moving up
            Set<Integer> levelsToStop = thisElevator.getLevelsToStop();
            int highestDestinedLevel = getHighestDestinedLevel(levelsToStop);
            thisDistance = Math.abs((highestDestinedLevel - lastElevatorLevelId) + (highestDestinedLevel - thisLevelId));
        }
        return thisDistance;
    }


    public static synchronized Level getLevel(int levelId) {
        return levels.get(levelId);
    }


    public static synchronized void addNewLevel(Level level) {
        LevelImpl levelImpl = (LevelImpl)level;
        int levelId = levelImpl.getId();
        levels.put(levelId, levelImpl);
        if(levelId < lowestLevel) {
            lowestLevel = levelId;
        }
        if(levelId > highestLevel) {
            highestLevel = levelId;
        }

    }

    public static Elevator getElevator(int elevatorId) {
        return elevators.get(elevatorId);
    }

    public static synchronized void addNewElevator(Elevator elevator) {
        ElevatorImpl elevatorImpl = (ElevatorImpl)elevator;
        elevators.put(elevatorImpl.getId(), elevator);
        if(levelsClosestElevatorsForMoveUp.get(0) != null) {
            levelsClosestElevatorsForMoveUp.get(0).add(elevatorImpl.getId());
        }else {
            List<Integer> closestElevators = new ArrayList<>();
            closestElevators.add(elevatorImpl.getId());
            levelsClosestElevatorsForMoveUp.put(0, closestElevators);
        }

        if(levelsClosestElevatorsForMoveDown.get(0) != null) {
            levelsClosestElevatorsForMoveDown.get(0).add(elevatorImpl.getId());
        }else {
            List<Integer> closestElevators = new ArrayList<>();
            closestElevators.add(elevatorImpl.getId());
            levelsClosestElevatorsForMoveDown.put(0, closestElevators);
        }
    }



    private static synchronized int getLowestDestinedLevel(Set<Integer> destinedLevels) {
        if(destinedLevels.iterator().hasNext()) {
            return destinedLevels.iterator().next();
        }
        return 0;
    }


    private static synchronized int getHighestDestinedLevel(Set<Integer> destinedLevels) {
        Integer lastLevel = 0;
        Iterator<Integer> itr = destinedLevels.iterator();
        while(itr.hasNext()) {
            lastLevel = itr.next();
        }
        return lastLevel;
    }


    public static Collection<Elevator> getElevators() {
        return Collections.unmodifiableCollection(elevators.values());
    }

    public static Collection<Level> getLevels() {
        return Collections.unmodifiableCollection(levels.values());
    }


    public static int getLowestLevel() {
        return lowestLevel;
    }

    public static int getHighestLevel() {
        return highestLevel;
    }
}
