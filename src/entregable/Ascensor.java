/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entregable;

/**
 *
 * @author tincho
 */
public class Ascensor extends Thread{
    
    private final int MAX_FLOOR;
    private int currentFloor;
    private boolean[] upRequests;
    private boolean[] downRequests;
    
    public Ascensor(int maxFloor, int initialFloor){
    
        this.MAX_FLOOR = maxFloor;
        this.currentFloor = initialFloor;
        this.upRequests = new boolean[maxFloor];
        this.downRequests = new boolean[maxFloor];
    }
   
    public synchronized void requestUp(int floor) {
        if (floor >= 0 && floor < MAX_FLOOR) {
            upRequests[floor] = true;
            notifyAll();
        }
    }

    public synchronized void requestDown(int floor) {
        if (floor >= 0 && floor < MAX_FLOOR) {
            downRequests[floor] = true;
            notifyAll();
        }
    }

        public synchronized void run() {
        while (true) {
            boolean done = false;
            for (int i = 0; i < MAX_FLOOR; i++) {
                if (upRequests[i]) {
                    goToFloor(i);
                    upRequests[i] = false;
                    done = true;
                }
            }
            for (int i = MAX_FLOOR - 1; i >= 0; i--) {
                if (downRequests[i]) {
                    goToFloor(i);
                    downRequests[i] = false;
                    done = true;
                }
            }
            if (!done) {
                try {
                    System.out.println("Elevator is waiting...");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void goToFloor(int floor) {
        if (currentFloor < floor) {
            for (int i = currentFloor; i <= floor; i++) {
                System.out.println("Elevator on floor: " + i);
            }
        } else if (currentFloor > floor) {
            for (int i = currentFloor; i >= floor; i--) {
                System.out.println("Elevator on floor: " + i);
            }
        } else {
            System.out.println("Elevator is already on floor: " + currentFloor);
        }
        currentFloor = floor;
    }
}
