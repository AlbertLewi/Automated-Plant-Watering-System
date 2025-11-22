package org.example;
import java.io.IOException;

/**The primary goal of this class is to call all the methods from other classes.
 *
 */
public class Main {
    /**
     *
     * @param args Command line arguments. Native to main.
     * @throws IOException Indicates an issue with board communication.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        PlantLogic system = new PlantLogic();
        /// Runs task timer from PlantLogic
        system.TaskForTimer();
        /// Ensuring that the OLED display shuts off upon termination
        System.out.println("Program running.");
        system.terminationControl();

        /// We see the parameter(system), defining the current instance of PlantLogic
        /// being run. Puts that same instance in the GraphManager class.
        GraphManager.setPlantLogic(system);

        /// Calling the GraphManager main to display UI
        GraphManager.main(args);

    }
}