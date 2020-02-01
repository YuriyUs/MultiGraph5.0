/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg;

import static java.lang.Math.abs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author us
 */
//public class MultiGraphCustomView extends Rectangle{
//    private final LineChart baseChart;
//    private final ObservableList<LineChart> multiChart;
//    
//    
//    
//    MultiGraphCustomView(MarkerChart chartPane){
//        super(0,0);
//        //Pane pane = new Pane();
//        multiChart = chartPane.getMultiChart();
//        baseChart = multiChart.get(0);  // first chart on the list is the base for others.
//        
//        this.setStroke(Color.HOTPINK);
//        this.setFill(Color.TRANSPARENT);
//        
//        // set rectangle == size of the base chart.
//        this.widthProperty().bind(chartPane.widthProperty().subtract(30));
//        this.heightProperty().bind(chartPane.heightProperty().subtract(30));
//        //this.widthProperty().bind(chartPane.widthProperty().subtract(2));
//        //this.heightProperty().bind(chartPane.heightProperty().subtract(2));
//        
//        
//        // set grabbing points. they are used to grab and resize the chart
//        //Rectangle leftControl = new Rectangle(0,0,10,10);
//        //leftControl.setFill(Color.HOTPINK);
//        //pane.getChildren().add(leftControl);
//        
//        //Rectangle rightControl = new Rectangle(300,0,10,10);
//        //rightControl.setFill(Color.HOTPINK);
//        //pane.getChildren().add(rightControl);
//        
//        //chartPane.getChildren().add(pane);
//        
//    }
//    
//    // do not remove next 3 overrides. It helps resizing to work properly.
//     @Override
//    public boolean isResizable() {
//        return true;
//    }
//
//    @Override
//    public double minWidth(double height) {
//        return 0.0;
//    }
//
//    @Override
//    public double minHeight(double height) {
//        return 0.0;
//    }
//    // do not remove previous 3 overrides. It helps resizing to work properly.
//}


public class MultiGraphCustomView extends BorderPane {
    MarkerChart chartPane;
    private final LineChart baseChart;
    Rectangle NewChartBounds;
    
    public MultiGraphCustomView(MarkerChart chartPane){
        super();
        this.chartPane = chartPane;
        baseChart = chartPane.getMultiChart().get(0);
        //Node contentBackground = baseChart.lookup(".chart-content");//.lookup(".chart-plot-background");
        Node contentBackground = baseChart.lookup(".chart-plot-background");
        
        double left = contentBackground.getLayoutX() + baseChart.getXAxis().getWidth();
        double right = contentBackground.getLayoutX()+ contentBackground.getLayoutBounds().getWidth();
        double top   = contentBackground.getLayoutY()+ contentBackground.getLayoutBounds().getHeight();
        double bottom = contentBackground.getLayoutY() + baseChart.getYAxis().getWidth();
        
        this.setMargin(this, new Insets(top, right, bottom, left));
        this.setAlignment(this, Pos.CENTER);
        this.setStyle("-fx-border-color: red");
        this.setMouseTransparent(true);
 
        
    }
    
    public void customize(LineChart chart){
        
        hooverChart = chart;
        bindMouseEvents(hooverChart);
    }
    
    private void bindMouseEvents(LineChart hooverChart){
       
        hooverChart.setOnMouseDragged((MouseEvent event) -> {
            
            //if (chartPropertyMap.get(hooverChart).getChartHoover() == true){

                Long xValueLong = Math.round((double)hooverChart.getXAxis().getValueForDisplay(event.getX()));
                Long yValueLong = Math.round((double)hooverChart.getYAxis().getValueForDisplay(event.getY()));

                final NumberAxis xAxis = (NumberAxis)hooverChart.getXAxis();
                final NumberAxis yAxis = (NumberAxis)hooverChart.getYAxis();
                //double xOffset = xAxis.localToScene();
                double upperBound = yAxis.getUpperBound();
                double lowerBound = yAxis.getLowerBound();
                if (abs(upperBound) > abs(lowerBound)){
                    yAxis.setAutoRanging(false);
                    yAxis.setUpperBound(upperBound - 10);
                    yAxis.setLowerBound(-upperBound+10);
                } else{
                    yAxis.setAutoRanging(false);
                    yAxis.setUpperBound(abs(lowerBound)-10);
                    yAxis.setLowerBound(-abs(lowerBound)+10);
                }

                System.out.println("X="+xValueLong+" Y="+yValueLong);
                //System.out.println("x="+x+" y="+y+" top="+top+" left="+left+" bottom="+bottom+" right="+right);
                //chartPane.setMargin(this, new Insets(15,15,15,15));
            //} else System.out.println("lineChart="+hooverChart);
        });
    private void bindMouseEvents(){
//        chartPane.setOnMouseDragged((MouseEvent event) -> {
//            
//            if (chartPane.getHooveredChart().get(chartPane) == true){
////              double x = event.getX();
////              double y = event.getY();
////              //Insets ins = NewChartBounds.getMargin(this);
////              double top = NewChartBounds.getHeight();
////               double left = NewChartBounds.getLayoutX();
////              double bottom= NewChartBounds.getLayoutY();
////              double right = NewChartBounds.getWidth();
//                Long xValueLong = Math.round((double)lineChart.getXAxis().getValueForDisplay(event.getX()));
//                Long yValueLong = Math.round((double)lineChart.getYAxis().getValueForDisplay(event.getY()));
//
//                final NumberAxis xAxis = (NumberAxis)lineChart.getXAxis();
//                final NumberAxis yAxis = (NumberAxis)lineChart.getYAxis();
//                //double xOffset = xAxis.localToScene();
//                double upperBound = yAxis.getUpperBound();
//                double lowerBound = yAxis.getLowerBound();
//                if (abs(upperBound) > abs(lowerBound)){
//                    yAxis.setAutoRanging(false);
//                    yAxis.setUpperBound(upperBound - 10);
//                    yAxis.setLowerBound(-upperBound+10);
//                } else{
//                    yAxis.setAutoRanging(false);
//                    yAxis.setUpperBound(abs(lowerBound)-10);
//                    yAxis.setLowerBound(-abs(lowerBound)+10);
//                }
//
//                System.out.println("X="+xValueLong+" Y="+yValueLong);
//                //System.out.println("x="+x+" y="+y+" top="+top+" left="+left+" bottom="+bottom+" right="+right);
//                //chartPane.setMargin(this, new Insets(15,15,15,15));
//            } else System.out.println("lineChart="+lineChart);
//        });
    }
    
    
}
