package org.example;
import org.firmata4j.I2CDevice;
import org.firmata4j.ssd1306.SSD1306;
import org.firmata4j.firmata.*;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import java.io.IOException;

/**This Class is for managing arduino connections to analog pin 0, digital pin 2, and OLED display functions.
 *
 */
public class ArduinoManager {
    /// Defining arduino board IODevice object.
    private final IODevice myGroveBoard;
    /// Defining an SSD1306 OLED object.
    private SSD1306 theOledObject;
    /// The raw voltage output of the moisture sensor.
    private long moistureSensorValue = 0;

    /** Initializing the board and the OLED display.
     * Code taken from in class labs for this method.
     *
     * @param Port Takes in the COM port that the board is connected to.
     */
    protected ArduinoManager(String Port) {
        myGroveBoard = new FirmataDevice(Port);

        try {
            myGroveBoard.start();
            myGroveBoard.ensureInitializationIsDone();
            System.out.println("Board started.");

            I2CDevice i2cObject = this.myGroveBoard.getI2CDevice((byte) 0x3C); // Use 0x3C for the Grove OLED
            this.theOledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515
            this.theOledObject.init();
        } catch (Exception ignored) {
        }

    }

    /**Method to get the pin A0 (Connected to moisture sensor) and read the raw input values.
     *
     * @throws IOException Indicates an issue with board communication: Thrown by setMode()
     */
    protected void getAnalogPins() throws IOException {
        //Using A0
        var moistureSensor = myGroveBoard.getPin(14);
        moistureSensor.setMode(Pin.Mode.ANALOG);

        ///Obtains raw voltage value
        moistureSensorValue = moistureSensor.getValue();

    }

    /** Method to get the pin D2 (Connected to water pump), method turns on pump Waits 2 seconds and then turns off pump
     *
     * @throws IOException Indicates an issue with board communication: Thrown by setMode() and setValue()
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    public void getDigitalPins() throws InterruptedException, IOException {
        //Using D2
        var waterPump = myGroveBoard.getPin(2);
        waterPump.setMode(Pin.Mode.OUTPUT);
        waterPump.setValue(1);
        Thread.sleep(2000);
        waterPump.setValue(0);
    }


    /**Getter method to return sensor values obtained by getAnalogPins().
     *
     * @return The raw voltage value obtained by the moisture sensor.
     */
    public long getMoistureSensorValue() {
        return moistureSensorValue;
    }


    /** Method to update the display and output a message.
     *
     * @param message String message that can be manually inputted when method is called.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    protected synchronized void updateDisplay(String message) throws InterruptedException {
        theOledObject.getCanvas().clear(); // Clear the display
        theOledObject.getCanvas().setTextsize(2); //Font size 2
        int width = theOledObject.getCanvas().getWidth();
        int height = theOledObject.getCanvas().getHeight();
        /// Setting the coordinates of where message will be displayed on OLED.
        int X = width/8;
        int Y = height/2;
        theOledObject.getCanvas().setCursor(X, Y);
        /// Writing and displaying message then waiting 2 seconds.
        theOledObject.getCanvas().write(message);
        theOledObject.display();
        Thread.sleep(2000);
    }

    /**Method to stop the display if the OLED object exists. Used in the class PlantLogic
     * in the method terminationControl.
     *
     * @throws IOException Indicates an issue with board communication: Thrown by .stop()
     */
    public void stopDisplay() throws IOException {
        if (theOledObject != null) {
            theOledObject.turnOff();
        }
        myGroveBoard.stop();
    }

    /**Main in ArduinoManager, used for testing hardware separate from Main.java
     * Here there is code that was used to directly test the pump for functionality
     *
     * @param args Command line arguments. Native to main.
     * @throws IOException Indicates an issue with board communication: Thrown by setMode() and setValue()
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ArduinoManager arduinoManager = new ArduinoManager("COM4");
        arduinoManager.getDigitalPins();
    }
}

