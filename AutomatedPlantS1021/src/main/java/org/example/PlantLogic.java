package org.example;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**Class for carrying out state changes and system functions.
 *
 */
public class PlantLogic{

    ///Defines the current state the system is in.
    private static final int STARTED = 0;
    private static final int DRY = 1;
    private static final int moderateDRY = 2;
    private static final int WET = 3;

    ///Integer variable to store the current STATE.
    private int currentState;
    ///Integer variable storing raw voltage.
    private int sensorValue;
    /// The arduino Object.
    private final ArduinoManager arduino;
    /// The timer object, used in TaskForTimer method.
    private Timer timer;

    ///Defines an arraylist to hold raw voltage data.
    private final List<Integer> voltageData = new ArrayList<>();
    ///Defines an arraylist to store processed voltage values.
    private final List<Double> updateVoltageList = new ArrayList<Double>();
    /// Defines an integer variable that will store the values added to updateVoltageList, stores the raw voltage data.
    private int sensorValue2;


    /** Initialization of state, sensor value, and arduino object.
     *
     */
    protected PlantLogic() {
        currentState = STARTED;
        sensorValue = 0;
        sensorValue2 = 0;
        String Port = "COM4";
        arduino = new ArduinoManager(Port);
        TaskForTimer();
    }

    /**
     * Setter Method for manually setting teh sensor value
     * used in testing the changeState class.
     *
     * @param value Takes in a directly inputted value.
     * @return The value that is manually inputted.
     */
    public int setSensorValue(int value) {
        this.sensorValue = value;
        return value;
    }

    /**Method to define and update a new value inorder to set a new state
     *
     */
    private void updateSensorValue() {
        try {
            arduino.getAnalogPins();
            sensorValue = (int) arduino.getMoistureSensorValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param originalValue Int parameter taking a value, in this case the raw sensor value
     * @return The processed and rounded value
     */
    private int rationalCalculation(int originalValue) {
        return (int) Math.round((735.0 - (double) originalValue) / (735.0 - 550.0)* 100.0);
    }

    /** Method to change the state and operate the systems main functions.
     *
     * @param value Takes in a raw voltage value from the moisture sensor.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     * @throws IOException Indicates an issue with board communication: See ArduinoManager class.
     * @throws Exception Exception is related to the pump not working: Thrown by bufferPump()
     */
    protected void changeState(int value) throws Exception {
        //Initializing voltage threshold value
        int threshold = 690;
        if(value >= threshold) {
            currentState = DRY;
            arduino.updateDisplay("DRY: " + rationalCalculation(sensorValue) + "%");
            bufferPump();
        }
        else if (value >= 660) {
            currentState = moderateDRY;
            cleanUpMODWET();
            //MOD means moderate dryness
            arduino.updateDisplay("MOD: " + rationalCalculation(sensorValue) + "%");
        }
        else {
            currentState = WET;
            cleanUpMODWET();
            //DO nothing, display state on OLED
            arduino.updateDisplay("WET: " + rationalCalculation(sensorValue) + "%");
        }
    }

    /** Getter method to return the raw sensor value.
     *
     * @return sensorValue
     */
    public int getSensorValue() {
        return sensorValue;
    }

    /**Getter method to return and print the current state.
     *
     * @return currentState
     */
    public int getCurrentState() {
        System.out.println("Current State is: " + currentState);
        return currentState;
    }

    /** Method for updating the list Voltage data and then processing the data into a hash map
     * This method will be important for graphing.
     *
     * @throws IOException Indicates an issue with board communication: See ArduinoManager class
     */
    private synchronized void updateVoltageData() throws IOException {
        ///Add all sensor values into the integer array list.
        sensorValue2 = getSensorValue();
        voltageData.add(sensorValue2);
        ///Process the most recent value in the arrayList and add the processed values to a new array list to graph the data.
        int lastValue = voltageData.get(voltageData.size() - 1);
        /// Don't want to round here
        double rationalValue = ((735.0 - lastValue) / (735.0 - 550.0)) * 100.0;
        updateVoltageList.add(rationalValue);
    }

    /**Getter method to obtain the updates list of processed voltage data.
     * This list returns a new copy of the list inorder to prevent thread errors.
     *
     * @return updateVoltageList; an array list containing doubles.
     */
    public synchronized List<Double> getList() {

        return new ArrayList<>(updateVoltageList);
    }

    /** Buffer Method to ensure that the pump does not fire off twice in a row and over-saturate the plant.
     * Leaves ten seconds for state to change.
     *
     * @throws IOException Indicates an issue with board communication: See ArduinoManager class
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     * @throws Exception If pump does not correctly run due to getDigigtalPins() failing
     */
    private void bufferPump() throws Exception {
        if(currentState == DRY) {
            getCurrentState();
            System.out.println("Sensor Value: " + getSensorValue());
            System.out.println("Pump is running");
            try {
            arduino.getDigitalPins();} catch(Exception e) {
                throw new Exception("Pump Operation unsuccessful.");
            }
            Thread.sleep(6000);
        }
    }

    /** Method for displaying important information regarding the states, WET and moderateDRY
     *
     */
    private void cleanUpMODWET() {
        if (currentState == moderateDRY || currentState == WET) {
            getCurrentState();
            System.out.println("Sensor Value: " + getSensorValue());
            System.out.println("Pump is not running: Plant is watered");
        }
    }

    /**Timer to run everything periodically every 2 seconds.
     *
     */
    public void TaskForTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                updateSensorValue();
                try {
                    changeState(sensorValue);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                try {
                    updateVoltageData();
                    getList();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        timer.scheduleAtFixedRate(task, 2000, 2000);
    }

    /**Method for turning off display upon program termination.
     *
     */
    public void terminationControl() {
        /// Shut down hook runs when termination occurs.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                arduino.stopDisplay();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}

