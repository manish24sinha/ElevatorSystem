package com.cdk.elevatorsystem.observer;

import com.cdk.elevatorsystem.common.ElevatorState;

/**
 * Created by msinha on 5/15/17.
 */
public interface ElevatorObserver {
    void notifyStateChange(int elevatorId, ElevatorState elevatorState);
}
