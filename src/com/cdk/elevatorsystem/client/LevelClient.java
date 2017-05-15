package com.cdk.elevatorsystem.client;

import com.cdk.elevatorsystem.common.ElevatorState;
import com.cdk.elevatorsystem.common.ElevatorSystemException;
import com.cdk.elevatorsystem.processor.LevelProcessor;
import com.cdk.elevatorsystem.system.SystemClient;

import java.util.List;

/**
 * Created by msinha on 5/13/17.
 */
public enum LevelClient {
    INSTANCE;
    public final void pressLevelUp(Integer currentLevel) throws ElevatorSystemException{
        SystemClient.pressLevelUp(currentLevel);
    }

    public final void pressLevelDown(Integer currentLevel) {
        SystemClient.pressLevelDown(currentLevel);
    }

    public final void createLevel(Integer levelId) {
        try {
            SystemClient.createLevel(levelId);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public final void createLevels(List<Integer> levelIds) {
        try {
            for(Integer levelId : levelIds) {
                SystemClient.createLevel(levelId);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public final void createLevelsInRange(Integer levelIdRange) {
        try {
            for(int levelId = 0;levelId <= levelIdRange; levelId++) {
                SystemClient.createLevel(levelId);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
