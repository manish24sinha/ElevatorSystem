package com.cdk.elevatorsystem.client;

import com.cdk.elevatorsystem.system.SystemClient;

/**
 * Created by msinha on 5/13/17.
 */
public enum  ElevatorClient {
    INSTANCE;
    public final void createElevators(int noOfElevators) {
        try {
            SystemClient.createElevator(noOfElevators);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
