/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package entregable;

/**
 *
 * @author tincho
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        Ascensor elevator = new Ascensor(10,0);
        elevator.start();

        elevator.requestUp(5);
        
    }
}
    

