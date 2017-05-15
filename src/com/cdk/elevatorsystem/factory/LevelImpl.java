package com.cdk.elevatorsystem.factory;

import com.cdk.elevatorsystem.common.LevelState;
import com.cdk.elevatorsystem.factory.bean.Level;

/**
 * Created by msinha on 5/13/17.
 */
public class LevelImpl implements Level {
    private int levelId = -1;
    private LevelState currentState = LevelState.IDLE;
    int distanceFromClosestElevators = 0;

    LevelImpl(final int levelId) {
        this.levelId = levelId;
    }

    public int getId() {
        return levelId;
    }

    public void requestForLevelUp() {

    }

    public void requestForLevelDown() {

    }


    public void setCurrentState(LevelState state) {
        this.currentState = state;
    }


    public LevelState getCurrentState() {
        return currentState;
    }

    public int getDistanceFromClosestElevators() {
        return distanceFromClosestElevators;
    }

    public void setDistanceFromClosestElevators(int distanceFromClosestElevators) {
        this.distanceFromClosestElevators = distanceFromClosestElevators;
    }


    public void doorOpen() {
        System.out.print("Level " + levelId + " Door Opened");
    }

    public void doorClose() {
        System.out.print("Level " + levelId + " Door Closed");
    }
}
