package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing class to test the changeState method in PlantLogic, extends plant logic in order to make it easier to
 * use methods.
 *
 * IMPORTANT!!!
 * HOW TO USE TESTS: Tests will not work correctly unless all called methods within changeState method are commented out.
 * Once they are commented out the program will test the method without error.
 */
class PlantLogicTest extends PlantLogic {


    /**Tests if the correct state of WET/3 is set on the corner case
     * where the sensor value is 659.
     *
     *
     * @throws IOException IOException Indicates an issue with board communication: See ArduinoManager class.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    @Test
    void changeStateWET() throws Exception {

        int value = setSensorValue(659);
        changeState(value);
        int result = getCurrentState();
        System.out.println(result);
        Assertions.assertEquals(3, result);
        terminationControl();
        }

    /**Tests if the correct state of DRY/1 is set on the corner case
     * where the sensor value is 690.
     *
     * @throws IOException IOException Indicates an issue with board communication: See ArduinoManager class.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    @Test
    void changeStateDRY() throws Exception {

        int value = setSensorValue(690);
        changeState(value);
        int result = getCurrentState();
        System.out.println(result);
        Assertions.assertEquals(1, result);
    }

    /**Tests if the correct state of moderateDry/2 is set on the corner case
     * where the sensor value is 660.
     *
     * @throws IOException IOException Indicates an issue with board communication: See ArduinoManager class.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    @Test
    void changeStateMOD() throws Exception {

        int value = setSensorValue(660);
        changeState(value);
        int result = getCurrentState();
        System.out.println(result);
        Assertions.assertEquals(2, result);
    }

}