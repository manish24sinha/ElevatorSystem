package com.cdk.elevatorsystem;

import com.cdk.elevatorsystem.client.ElevatorClient;
import com.cdk.elevatorsystem.client.LevelClient;

/**
 * Created by msinha on 5/15/17.
 */
public enum ElevatorSystem {
    INSTANCE;

    ElevatorSystem() {
        init();
    }

    private void init() {
        //if we need to init something
    }

    public ElevatorClient getElevatorClient() {
        return ElevatorClient.INSTANCE;
    }

    public LevelClient getLevelClient() {
        return LevelClient.INSTANCE;
    }

    public static ElevatorSystem getInstance() {
        return INSTANCE;
    }
}
