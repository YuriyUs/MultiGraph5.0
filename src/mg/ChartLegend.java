/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg;

import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 * @author us
 */
public class ChartLegend extends GridPane{
    
    MarkerChart mChart;
    
    private       ObservableList<CheckBox> checkBoxes = FXCollections.observableArrayList();;
    private final Map<LineChart, Color> chartColorMap;
    
    // variables to control grid in the legend
    int colIndex=0;  // currently filling column    
    int rowIndex=0;  // currently filling row
    int colSpan = 1; //the number of columns the child's layout area spans horizontally
    int rowSpan =10; //the number of rows the child's layout area spans vertically.

        
    public ChartLegend(MarkerChart chart) {
        //legend  = new GridPane();
        this.mChart = chart;
        this.setPadding(new Insets(2));
        this.chartColorMap = chart.getColorMap();
        if (chart.getLegendSide() == Side.LEFT || chart.getLegendSide() == Side.RIGHT){
            colSpan=0;  // legend is a vertical type: 1 column and multiple rows
            this.setVgap(5);
            this.add(new Label("Legend:"),0,0);
            rowIndex++;
        } else {
            // legend of horizontal type: 5 columns and multiple rows
            colSpan=5;  // todo: find how to calculate column number depending on sze on the screen
            this.setVgap(5);
            this.setHgap(15);
            this.setAlignment(Pos.CENTER);
        }
    }
    
    /**
     * Add custom control to the bottom of legend. For example some button
     * @param node 
     */
    public void add (Node node){
        this.add(node, 1,rowIndex++,colSpan, 1);
        if (colIndex < colSpan){
                colIndex++;
        }
        else {
            colIndex=0;
            rowIndex++;
        }
    }
    
    /**
     * Add check boxes for every chart to the legend
     * @param chart 
     */
    public void add (LineChart chart){
        
        // Adding item to Legend
        CheckBox checkBox = new CheckBox(chart.getYAxis().getLabel());
        checkBoxes.add(checkBox); // to keep all checkboxes in one place
        
        UserData userData = new UserData();
        userData.chartSelected = true;
        userData.markerSelected= false;
        userData.chart = chart;
        checkBox.setUserData(userData);
        checkBox.setSelected(true);
        checkBox.setStyle("-fx-text-fill: " + mChart.toRGBCode(chartColorMap.get(chart)) + "; -fx-font-weight: bold");
        this.add(checkBox, colIndex,rowIndex);
        if (colIndex < colSpan){
                colIndex++;
        }
        else {
            colIndex=0;
            rowIndex++;
        }

        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                 if (oldValue != newValue){
                    //Check box on legend has changed
                    mChart.handleLegendChange(checkBox);

                        
                }
            }
        });        
        
        
        //Attach dropdown menu to every checknox 
        final ContextMenu contextMenu = new ContextMenu();
        //contextMenu.setUserData(checkBox);

        MenuItem property = new MenuItem("Property");
        MenuItem delete = new MenuItem("Delete");
        contextMenu.getItems().addAll( property, delete);

        property.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("Property...");
                //mChart.handleChartProperty((UserData)(((CheckBox)e.getSource()).getUserData())); //(LineChart)((UserData)checkBox.getUserData()).chart
                mChart.handleChartProperty((LineChart)((UserData)checkBox.getUserData()).chart);
            }
        });
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("Delete...");
                mChart.handleDeleteChart((LineChart)((UserData)checkBox.getUserData()).chart);
            }
        });
        checkBox.setContextMenu(contextMenu);
        checkBox.setOnMouseEntered(me -> {
            BorderStroke borderStroke = new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1,2,2,0));
            checkBox.setBorder(new Border(borderStroke));
            //cb.setBackground(new Background(new BackgroundFill(Color.BLANCHEDALMOND   , new CornerRadii(2), new Insets(2))));

        });
        checkBox.setOnMouseExited(me -> {
            BorderStroke borderStroke = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1,2,2,0));
            checkBox.setBorder(new Border(borderStroke));
            //cb.setBackground(Background.EMPTY);
        });

        // transparent border is needed to avoid moving and resizing of controls when you hover the mouse over it.
        BorderStroke borderStroke = new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1,2,2,0));
        checkBox.setBorder(new Border(borderStroke));
 
    }
    
    public ObservableList<CheckBox> getCheckBoxes(){
        return this.checkBoxes;
    }
    
    
    /**
     * object to keep data in the legend's check box UerData
     */

    public class UserData{
        public Boolean chartSelected;
        public Boolean markerSelected;
        public XYChart chart;
    }
    
}
