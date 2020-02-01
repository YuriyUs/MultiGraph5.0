/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg;

import static java.lang.Math.abs;
import java.util.Map;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author us
 */

public class MultiGraphCustomView extends BorderPane {
    MarkerChart chartPane;
    private final LineChart lineChart;
    private       LineChart hooverChart;
    Rectangle NewChartBounds;
    private       Map<LineChart, GrProperty> chartPropertyMap;
    
    public MultiGraphCustomView(MarkerChart chartPane){
        super();
        this.chartPane = chartPane;
        lineChart = chartPane.getMultiChart().get(0);
        chartPropertyMap = chartPane.getPropertyMap();
        //Node contentBackground = baseChart.lookup(".chart-content");//.lookup(".chart-plot-background");
        Node contentBackground = lineChart.lookup(".chart-plot-background");
        
        double left = contentBackground.getLayoutX() + lineChart.getXAxis().getWidth();
        double right = contentBackground.getLayoutX()+ contentBackground.getLayoutBounds().getWidth();
        double top   = contentBackground.getLayoutY()+ contentBackground.getLayoutBounds().getHeight();
        double bottom = contentBackground.getLayoutY() + lineChart.getYAxis().getWidth();
        
        this.setMargin(this, new Insets(top, right, bottom, left));
        this.setAlignment(this, Pos.CENTER);
        this.setStyle("-fx-border-color: red");
        this.setMouseTransparent(true);
 
        
    }
    
    public void customize(LineChart chart){
        //chartPane.setMouseTransparent(true);
        hooverChart = chart;
        //setMouseTransparent(true);
        System.out.println("customize->hooverChart="+(XYChart.Series)(hooverChart.getData().get(0)));
        this.bindMouseEvents(hooverChart);
    }
    
    private void bindMouseEvents(LineChart hooverChart){
        final NumberAxis xAxis = (NumberAxis) hooverChart.getXAxis();
        final NumberAxis yAxis = (NumberAxis) hooverChart.getYAxis();
        System.out.println("bindMouseEvent->hooverChart="+(XYChart.Series)(hooverChart.getData().get(0)));
        System.out.println("xAxis="+xAxis+" yAxis="+yAxis);
        final Node mBChart = lineChart.lookup(".chart-plot-background");
        for (Node n: mBChart.getParent().getChildrenUnmodifiable()) {
            if (n != mBChart && n != xAxis && n != yAxis) {
                n.setMouseTransparent(true);
            }
        }
//        final Node mChart = hooverChart.lookup(".chart-plot-background");
//        System.out.println("hover mChart="+ mChart);
//        for (Node n: mChart.getParent().getChildrenUnmodifiable()) {
//            if (n != mChart && n != xAxis && n != yAxis) {
//                n.setMouseTransparent(true);
//            }
//        }
        System.out.println("HOOVER Content of mChart -------------");
        mBChart.lookupAll("*").forEach(N -> {
            System.out.println(N);
        });
        System.out.println("HOOVER end of mChart -------------");
                
        //mChart.setCursor(Cursor.NONE);
        mBChart.setCursor(Cursor.HAND);
        //mChart.setMouseTransparent(false);
        mBChart.setOnMouseDragged((MouseEvent event) -> {
            //System.out.println("setOnMouseDragged->hooverChart="+(XYChart.Series)(hooverChart.getData().get(0)));
            
            //if (chartPropertyMap.get(hooverChart).getChartHoover() == true){

                Long xValueLong = Math.round((double)hooverChart.getXAxis().getValueForDisplay(event.getX()));
                Long yValueLong = Math.round((double)hooverChart.getYAxis().getValueForDisplay(event.getY()));

//                /*final NumberAxis*/ xAxis = (NumberAxis)hooverChart.getXAxis();
//                /*final NumberAxis*/ yAxis = (NumberAxis)hooverChart.getYAxis();
                //double xOffset = xAxis.localToScene();
//                double upperBound = yAxis.getUpperBound();
//                double lowerBound = yAxis.getLowerBound();
//                if (abs(upperBound) > abs(lowerBound)){
//                    yAxis.setAutoRanging(false);
//                    yAxis.setUpperBound(upperBound-10);
//                    yAxis.setLowerBound(lowerBound-10);
//                } else{
//                    yAxis.setAutoRanging(false);
//                    yAxis.setUpperBound(abs(upperBound)+10);
//                    yAxis.setLowerBound(abs(lowerBound)+10);
//                }

                System.out.println("X="+xValueLong+" Y="+yValueLong+ "yAxis="+yAxis.getLabel());
                //System.out.println("x="+x+" y="+y+" top="+top+" left="+left+" bottom="+bottom+" right="+right);
                //chartPane.setMargin(this, new Insets(15,15,15,15));
            //} else System.out.println("lineChart="+hooverChart);
        });
    }
    
    
}