package org.example;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.util.List;


/** Class to display a graph of all the collected data
 * after 60 seconds of the program running.
 * Graph will display the relative moisture percentage
 * as a function of time. This Class Inherits uses the Javafx library
 * to achieve a functioning UI.
 *
 */
public class GraphManager extends Application {
    /// PlantLogic object; Named list because this object will only be used to define a list.
    private static PlantLogic list;

    /**
     * Sets the currently existing PlantLogic instance to be used in this class: Used in the MAIN class.
     *
     * @param currentList Defines an existing instance of PlantLogic, holding data.
     */
    public static void setPlantLogic(PlantLogic  currentList) {
        list = currentList;
    }

    /**Abstract method native to javafx. This method when called
     * is used to carry out the function of the UI. Specifically, it initializes
     * UI components.
     *
     * @param stage defines a stage or an instance of the UI itself.
     */
    @Override
    public void start(Stage stage) {
        ///Calling the existing array list from the PlantObject, adding it to a new list.
        List <Double> sample = list.getList();

        ///Initializing X and Y axis.
        NumberAxis X = new NumberAxis();
        NumberAxis Y = new NumberAxis();
        X.setLabel("Time (2sec/interval)");
        Y.setLabel("Moisture Level (0-100%)");

        /// Defining a line chart. With a name.
        LineChart<Number, Number> lineChart = new LineChart<>(X, Y);
        lineChart.setTitle("Moisture Level Over Time");

        /// Series to hold moisture data
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Moisture Data");

        /// Adding random values into the graph to mimic how my actual array list would be added.
        //List<Double> sample = new ArrayList<>();
       // for(int i = 0; i < 10; i++) {
        // sample.add(Math.random()* (100) + 100);
       // }
       // for(int j = 0; j < sample.size(); j++) {
           // series.getData().add(new XYChart.Data<>(j, sample.get(j)));
        //}

        /// Adding my own list into the graph.
        for(int j = 0; j < sample.size(); j++) {
           series.getData().add(new XYChart.Data<>(j, sample.get(j)));
        }

        ///Adds series to the graph.
        lineChart.getData().add(series);

        /// Setting up the scene for the lineChart: Defining the bounds, scene, stage title, and calling the graph to display.
        Scene scene = new Scene(lineChart, 600, 600);
        stage.setScene(scene);
        /// The name of the UI window itself
        stage.setTitle("Moisture Level Over Time");
        stage.show();


    }

    /** Main class to launch the graph after the program has run for 60 seconds,
     * the specific instance of the UI is called in the MAIN class.
     *
     * @param args Command line arguments. Native to main.
     * @throws InterruptedException Thrown if thread.sleep() is interrupted while sleeping
     */
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(20000);
        launch(args);
    }
}