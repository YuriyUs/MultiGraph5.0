package mg;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.function.Function;
import javafx.geometry.Side;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MultipleGraphLineChartMain extends Application {

    public static final int X_DATA_COUNT = 3600;

    @Override
    public void start(Stage primaryStage) throws Exception{
        NumberAxis xAxis = new NumberAxis(0, X_DATA_COUNT, 200);
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Series 1");
        MarkerChart chart = new MarkerChart(xAxis, yAxis);
        //LineChart baseChart = new LineChart(xAxis, yAxis);
        chart.setLegendSide(Side.BOTTOM);
        //chart.setLegendSide(Side.LEFT);
        //baseChart.getData().add(prepareSeries("Series 1", (x) -> (double)x));
        chart.addSeries(prepareSeries("Series 1", (x) -> (double)x), Color.RED);

        //MarkerChart chart = new MarkerChart(baseChart, Color.RED);
        chart.addSeries(prepareSeries("Series 2", (x) -> (double)x*x),Color.BLUE);
        chart.addSeries(prepareSeries("Series 3", (x) -> (double)-x*x),Color.GREEN);
        chart.addSeries(prepareSeries("Series 4", (x) -> ((double) (x-250))*x),Color.DARKCYAN);
        chart.addSeries(prepareSeries("Series 5", (x) -> ((double)(x+100)*(x-200))),Color.BROWN);

        primaryStage.setTitle("MarkerChart");
        
        Menu menu;
        Menu subMenu;
        MenuBar menuBar = new MenuBar();
        MenuItem menuItem;
        
        menu = new Menu("File");
        menuItem = new MenuItem("New Project");
        menuItem.setOnAction(e->createNewChart()); // new chart
        menu.getItems().add( menuItem);
        
        menuItem = new MenuItem("Add Chart");
        menuItem.setOnAction(e->addNewChart());  // add line to existing chart
        menu.getItems().add( menuItem);
        
        menuItem = new MenuItem("Save Projet");
        menuItem.setOnAction(e->saveChart());
        menu.getItems().add( menuItem);
        
        menuItem = new MenuItem("Load Project");
        menuItem.setOnAction(e->loadChart());
        menu.getItems().add( menuItem);
        
       
                
        menuBar.getMenus().add(menu);
        menuBar.getMenus().add(chart.getMenuView());
        menuBar.getMenus().add(chart.getMenuTools());

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setCenter(chart);
        borderPane.setBottom(chart.getLegend());
        //borderPane.setLeft(chart.getLegend());

        Scene scene = new Scene(borderPane, 1024, 600);
        //scene.getStylesheets().add(getClass().getResource("mgLineChart.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void createNewChart(){
        
    }
    
    private void addNewChart(){
        
    }
    
    private void saveChart(){
        
    }
    
    private void loadChart(){
        
    }
    

    private XYChart.Series<Number, Number> prepareSeries(String name, Function<Integer, Double> function) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        for (int i = 0; i < X_DATA_COUNT; i++) {
            series.getData().add(new XYChart.Data<>(i, function.apply(i)));
        }
        return series;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

