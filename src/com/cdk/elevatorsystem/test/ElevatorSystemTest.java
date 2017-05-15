package com.cdk.elevatorsystem.test;

import com.cdk.elevatorsystem.ElevatorSystem;
import com.sun.java_cup.internal.runtime.Scanner;

/**
 * Created by msinha on 5/15/17.
 */
public class ElevatorSystemTest {
    private static int NO_OF_LEVELS = -1;
    private static int NO_OF_ELEVATORS = -1;
    private static int CURRENT_LEVEL = -1;
    private static int LEVEL_UP = -1;

    public static void main(String args[]) {
        try {
            readInput();
            //get instance of ElevatorSystem
            ElevatorSystem elevatorSystem = ElevatorSystem.getInstance();

            //create levels = 0,1,2,3,4,5
            elevatorSystem.getLevelClient().createLevelsInRange(NO_OF_LEVELS);

            //create 3 elevators = 1,2,3
            elevatorSystem.getElevatorClient().createElevators(NO_OF_ELEVATORS);

            // request for moving UP/DOWN the level when your current Level is CURRENT_LEVEL
            if(LEVEL_UP == 1) {
                elevatorSystem.getLevelClient().pressLevelUp(CURRENT_LEVEL);
            }else {
                elevatorSystem.getLevelClient().pressLevelDown(CURRENT_LEVEL);
            }
        }catch (Exception ex) {
            System.out.println(ex.getMessage() + " Terminating application.");
            System.exit(0);
        }
    }


    private static void readInput() {
        System.out.println("Please enter number of levels: ");
        NO_OF_LEVELS = read();

        System.out.println("Please enter number of elevators: ");
        NO_OF_ELEVATORS = read();

        System.out.println("Please enter your current level: ");
        CURRENT_LEVEL = read();

        System.out.println("Press Level UP/Down Button ( 1 for UP, 0 for down ): ");
        LEVEL_UP = readLevel();
    }


    private static int read() {
        boolean valid = false;
        int value = -1;
        while (!valid) {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String strLevel = scanner.nextLine();
            try{
                value = Integer.parseInt(strLevel);
                valid = true;
            }catch (Exception ex) {
                System.out.println("Invalid Input. Kindly enter the value again: ");
            }
        }
        return value;
    }


    private static int readLevel() {
        int value = read();
        while(value != 0 && value != 1) {
            System.out.println("Invalid Input for Level Up/Down. Kindly enter one of 0 (DOWN) or 1 (UP): ");
            value = read();
        }
        return value;
    }
}
