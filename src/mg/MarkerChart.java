package mg;

//import com.sun.glass.ui.Cursor;
import static java.lang.Math.abs;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

public class MarkerChart extends StackPane {

    // markerModeChart is the selected chart in MARKER MODE. it can be only one chart selected to change its markers
    // baseChart is the chart is the main chart. It is the first chat from the list
    private       LineChart markerModeChart; // is the selected chart in MARKER MODE
    private       LineChart baseChart; // is the first from the follwing list of charts
    private       LineChart hooverChart = null; // is the chart under cursor
    private final ObservableList<LineChart> multiChart = FXCollections.observableArrayList();
    private final Map<LineChart, Color> chartColorMap = new HashMap<>();
    private       ChartLegend legend;
    private       Side legendSide= Side.BOTTOM;
    private final double yAxisWidth = 60;
    private final AnchorPane detailsWindow;
    private MultiGraphCustomView custLib;

    private final double yAxisSeparation = 20;
    private double strokeWidthThin = 0.1;
    private double strokeWidthNorm = 0.3;
    private double strokeWidthBold = 1.1;
    //private       LineChart hooveredChart; // Chart under cursor
    private       Map<LineChart, Boolean> chartHooverMap = new HashMap<>(); // Chart under cursor
    private       Map<LineChart, GrProperty> chartPropertyMap = new HashMap<>(); // Chart under cursor
    // chart can work in two modes. LineChart and Marker, where user can define markers for line.
    private enum ChartModes {
                   MARKER, // marker mode. works only with markers
                   CHART/*,  // works with chart as a whole.
                   CUSTOMIZATION*/}; // customization mode to manipulate wit chart properties
    
    private ChartModes chartMode; // to keep mode of chart
    private Menu menuTools;
    private Menu menuView;
    
    private int seriesCounter=0; // used in method addSeries
    private boolean stackedChartView = true;
    private boolean zeroChartView = false;
    private boolean customChartView = false;
    //private boolean customizationChart = false;
    private boolean stackedMarkerView = false;
    private boolean singleMarkerView = true;
    
    // my constructor
    public MarkerChart(NumberAxis xAxis, NumberAxis yAxis){
        chartMode = ChartModes.CHART;
        this.baseChart = new LineChart(xAxis, yAxis);
        multiChart.add(0, baseChart);
        legend = new ChartLegend(this);
        styleChart(baseChart);
        setFixedAxisWidth(baseChart);
        setAlignment(Pos.CENTER_LEFT);
        multiChart.addListener((Observable observable) -> rebuildChart());
        detailsWindow = new AnchorPane();
        
        bindMouseEvents(baseChart, this.strokeWidthNorm);

        rebuildChart();
    }



    private void bindMouseEvents(LineChart chart, Double strokeWidthNorm) {
        final DetailsPopup detailsPopup = new DetailsPopup();
        getChildren().remove(detailsWindow);
        getChildren().add(detailsWindow);
        //if (baseChart.equals(chart)) getChildren().add(detailsWindow);
        detailsWindow.getChildren().add(detailsPopup);
        detailsWindow.prefHeightProperty().bind(heightProperty());
        detailsWindow.prefWidthProperty().bind(widthProperty());
        detailsWindow.setMouseTransparent(true);

        setOnMouseMoved(null);
        setMouseTransparent(false);

        final Axis xAxis = chart.getXAxis();
        final Axis yAxis = chart.getYAxis();
        //


        final Line xLine = new Line();
        final Line yLine = new Line();
        yLine.setFill(Color.GRAY);
        xLine.setFill(Color.GRAY);
        yLine.setStrokeWidth(strokeWidthNorm/2);
        xLine.setStrokeWidth(strokeWidthNorm/2);
        xLine.setVisible(false);
        yLine.setVisible(false);
        

        final Node mChart = chart.lookup(".chart-plot-background");
        for (Node n: mChart.getParent().getChildrenUnmodifiable()) {
            if (n != mChart && n != xAxis && n != yAxis) {
                n.setMouseTransparent(true);
            }
        }
        mChart.setCursor(Cursor.NONE);
        mChart.setOnMousePressed((MouseEvent event) -> {

            detailsPopup.setVisible(false);
            CheckBox cb = selectedCheckBox();
            if (chartMode == ChartModes.MARKER && cb != null){
                Long xValueLong = Math.round((double)markerModeChart.getXAxis().getValueForDisplay(event.getX()));
                Long yValueLong = Math.round((double)markerModeChart.getYAxis().getValueForDisplay(event.getY()));

                markerModeChart = (LineChart)((ChartLegend.UserData)cb.getUserData()).chart;
                markerModeChart.getData().add(1, new LineChart.Series<>());
                XYChart.Series<Long,Long> series = (XYChart.Series<Long,Long>) markerModeChart.getData().get(1);
                series.getData().add(new XYChart.Data<>(xValueLong,yValueLong));
                series.getData().add(new XYChart.Data<>(xValueLong++,yValueLong++)); //end point of line
                styleChartLine(markerModeChart, chartColorMap.get(markerModeChart));

            }
         
 
        });
        mChart.setOnMouseReleased((event) -> {
            mChart.getOnMouseMoved().handle(event);
            detailsPopup.setVisible(true);
            if (chartMode == ChartModes.MARKER && selectedCheckBox() != null){
                Long xValueLong = Math.round((double)markerModeChart.getXAxis().getValueForDisplay(event.getX()));
                Long yValueLong = Math.round((double)markerModeChart.getYAxis().getValueForDisplay(event.getY()));

                XYChart.Series<Long,Long> series = (XYChart.Series<Long,Long>) markerModeChart.getData().get(1);
                series.getData().add(new XYChart.Data<>(xValueLong,yValueLong));
            }
        });
        mChart.setOnMouseDragged((event) -> {
            mChart.getOnMouseMoved().handle(event);
            if (chartMode == ChartModes.MARKER && selectedCheckBox() != null){
                detailsPopup.setVisible(false);
                Long xValueLong = Math.round((double)markerModeChart.getXAxis().getValueForDisplay(event.getX()));
                Long yValueLong = Math.round((double)markerModeChart.getYAxis().getValueForDisplay(event.getY()));

                XYChart.Series<Long,Long> series = (XYChart.Series<Long,Long>) markerModeChart.getData().get(1);
                int ind= series.getData().size()-1;
                //System.out.println("Series size="+ ind);
                series.getData().remove(ind);
                series.getData().add(new XYChart.Data<>(xValueLong,yValueLong));
                //styleChartLine(markerModeChart, chartColorMap.get(markerModeChart));
            }
    
        });
        mChart.setOnMouseEntered((event) -> {
            mChart.getOnMouseMoved().handle(event);
            detailsPopup.setVisible(true);
            xLine.setVisible(true);
            yLine.setVisible(true);
            detailsWindow.getChildren().addAll(xLine, yLine);
        });
        mChart.setOnMouseExited((event) -> {
            detailsPopup.setVisible(false);
            xLine.setVisible(false);
            yLine.setVisible(false);
            detailsWindow.getChildren().removeAll(xLine, yLine);
        });
        mChart.setOnMouseMoved(event -> {
            double x = event.getX() + mChart.getLayoutX();
            double y = event.getY() + mChart.getLayoutY();

            xLine.setStartX(10);
            xLine.setEndX(detailsWindow.getWidth()-10);
            xLine.setStartY(y+5);
            xLine.setEndY(y+5);

            yLine.setStartX(x+5);
            yLine.setEndX(x+5);
            yLine.setStartY(5);
            yLine.setEndY(detailsWindow.getHeight()-10);

            detailsPopup.showChartDescrpition(event);

            if (y + detailsPopup.getHeight() + 10 < getHeight()) {
                AnchorPane.setTopAnchor(detailsPopup, y+10);
            } else {
                AnchorPane.setTopAnchor(detailsPopup, y-10-detailsPopup.getHeight());
            }

            if (x + detailsPopup.getWidth() + 10 < getWidth()) {
                AnchorPane.setLeftAnchor(detailsPopup, x+10);
            } else {
                AnchorPane.setLeftAnchor(detailsPopup, x-10-detailsPopup.getWidth());
            }
        });
    }
    
    
    
    

    private void setFixedAxisWidth(LineChart chart) {
        chart.getYAxis().setPrefWidth(yAxisWidth);
        chart.getYAxis().setMaxWidth(yAxisWidth);
    }

    private void rebuildChart() {
        getChildren().clear();

        //getChildren().add(resizeBaseChart(baseChart));
        for (LineChart lineChart : multiChart) {
            if (lineChart.equals(baseChart)) getChildren().add(resizeBaseChart(baseChart));
            else getChildren().add(resizeBackgroundChart(lineChart));
        }
        getChildren().add(detailsWindow);
    }

    private Node resizeBaseChart(LineChart lineChart) {
        HBox hBox = new HBox(lineChart);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.prefHeightProperty().bind(heightProperty());
        hBox.prefWidthProperty().bind(widthProperty());
        
        if (customChartView){
            lineChart.getXAxis().setVisible(false);
            lineChart.getYAxis().setVisible(false);

            lineChart.minWidthProperty().bind(widthProperty());
            lineChart.prefWidthProperty().bind(widthProperty());
            lineChart.maxWidthProperty().bind(widthProperty());
        } else {
            lineChart.minWidthProperty().bind(widthProperty().subtract((yAxisWidth+yAxisSeparation)*multiChart.size()));
            lineChart.prefWidthProperty().bind(widthProperty().subtract((yAxisWidth+yAxisSeparation)*multiChart.size()));
            lineChart.maxWidthProperty().bind(widthProperty().subtract((yAxisWidth+yAxisSeparation)*multiChart.size()));
        }
            

        return lineChart;
    }

    private Node resizeBackgroundChart(LineChart lineChart) {
        HBox hBox = new HBox(lineChart);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.prefHeightProperty().bind(heightProperty());
        hBox.prefWidthProperty().bind(widthProperty());
        hBox.setMouseTransparent(true);

        if (customChartView){
            lineChart.minWidthProperty().bind(widthProperty());
            lineChart.prefWidthProperty().bind(widthProperty());
            lineChart.maxWidthProperty().bind(widthProperty());

            //lineChart.translateXProperty().bind(baseChart.getYAxis().widthProperty());
            //lineChart.getYAxis().setTranslateX((yAxisWidth + yAxisSeparation) * multiChart.indexOf(lineChart));
        } else {
            lineChart.minWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * multiChart.size()));
            lineChart.prefWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * multiChart.size()));
            lineChart.maxWidthProperty().bind(widthProperty().subtract((yAxisWidth + yAxisSeparation) * multiChart.size()));

            lineChart.translateXProperty().bind(baseChart.getYAxis().widthProperty());
            lineChart.getYAxis().setTranslateX((yAxisWidth + yAxisSeparation) * multiChart.indexOf(lineChart));            
        }

        return hBox;
    }

    public void addSeries(XYChart.Series series, Color lineColor) {
        //for the first seeries chart is already added into bacgroundCharts via 
        //constructor. for the rest of series new linecharts have to be created.
        LineChart lineChart;
        seriesCounter++;
        NumberAxis yAxis = new NumberAxis();
        NumberAxis xAxis = new NumberAxis();

        // style x-axis
        xAxis.setAutoRanging(false);
        xAxis.setVisible(false);
        xAxis.setOpacity(0.0); // somehow the upper setVisible does not work
        xAxis.lowerBoundProperty().bind(((NumberAxis) baseChart.getXAxis()).lowerBoundProperty());
        xAxis.upperBoundProperty().bind(((NumberAxis) baseChart.getXAxis()).upperBoundProperty());
        xAxis.tickUnitProperty().bind(((NumberAxis) baseChart.getXAxis()).tickUnitProperty());

        if (seriesCounter > 1){
            // style y-axis
            yAxis.setSide(Side.RIGHT);
            yAxis.setLabel(series.getName());

            // create chart
            lineChart = new LineChart(xAxis, yAxis);

        } else {
            lineChart=baseChart;
//            yAxis.setSide(Side.LEFT);
//            yAxis.setLabel(series.getName());
        }
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.setLegendSide(legendSide);
        lineChart.getData().add(series);
//        series.getNode().setOnMouseEntered(onMouseEnteredSeriesListener);// does not work
//        series.getNode().setOnMouseExited(onMouseExitedSeriesListener);// does not work
        //System.out.println("NodeID="+series..getName());
        styleChart(lineChart, lineColor);
        setFixedAxisWidth(lineChart);

        //chartHooverMap.put(lineChart, false);
        //chartColorMap.put(lineChart, lineColor);
        String color= new String(lineColor.toString());
        GrProperty prop = new GrProperty();
        prop.setChartColor(color);
        prop.setChartName(series.getName());
        chartPropertyMap.put(lineChart, prop);
        if (seriesCounter > 1) {
            multiChart.add(seriesCounter-1,lineChart);
        }

    }
    
//    EventHandler<MouseEvent> onMouseEnteredSeriesListener = 
//        (MouseEvent event) -> {
//            System.out.println("onMouseEnteredSeriesListener");
//            ((Node)(event.getSource())).setMouseTransparent(false);
//            ((Node)(event.getSource())).setCursor(Cursor.HAND);
//    };
//    
//    EventHandler<MouseEvent> onMouseExitedSeriesListener = 
//        (MouseEvent event) -> {
//            System.out.println("onMouseExitedSeriesListener");
//            ((Node)(event.getSource())).setCursor(Cursor.DEFAULT);
//            ((Node)(event.getSource())).setMouseTransparent(true);            
//    };

    /**
     * Styling initial chart before lines were added
     * @param baseChart 
     */
    private void styleChart(LineChart lineChart) {
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getXAxis().setAnimated(false);
        lineChart.getYAxis().setAnimated(false);
    }
    
    /**
     * Styling chart which has lines
     * @param lineChart
     * @param lineColor 
     */
    private void styleChart(LineChart lineChart, Color lineColor) {
        styleChartLine(lineChart, lineColor);

        Node contentBackground = lineChart.lookup(".chart-content").lookup(".chart-plot-background");
        contentBackground.setStyle("-fx-background-color: transparent;");

        lineChart.setVerticalZeroLineVisible(false);
        lineChart.setHorizontalZeroLineVisible(false);
        lineChart.setVerticalGridLinesVisible(false);
        lineChart.setHorizontalGridLinesVisible(false);
        lineChart.setCreateSymbols(false);
    }

    public String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void styleChartLine(LineChart chart, Color lineColor) {
        chart.getYAxis().lookup(".axis-label").setStyle("-fx-text-fill: " + toRGBCode(lineColor) + "; -fx-font-weight: bold;");
        // Set style for Markers
        chart.lookupAll(".chart-series-line").forEach(line->{
            line.setStyle("-fx-stroke: " + toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidthThin + ";");
           
        });
        // Set style for Chart line.
        Series<Number, Number> s = (Series<Number, Number>) chart.getData().get(0);
        s.nodeProperty().get().setStyle("-fx-stroke: " + toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidthNorm + ";");
        //hooveredChart = null;
        //System.out.println("hooveredChart = null ->"+hooveredChart+" chart="+chart);
    }
    
    private void styleChartBoldLine(LineChart chart, Color lineColor) {
        chart.getYAxis().lookup(".axis-label").setStyle("-fx-text-fill: " + toRGBCode(lineColor) + "; -fx-font-weight: bold;");
        chart.lookupAll(".chart-series-line").forEach(line->{
            line.setStyle("-fx-stroke: " + toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidthNorm + ";");
        });
        // Set style for Chart line.
        Series<Number, Number> s = (Series<Number, Number>) chart.getData().get(0);
        s.nodeProperty().get().setStyle("-fx-stroke: " + toRGBCode(lineColor) + "; -fx-stroke-width: " + strokeWidthBold + ";");
        //hooveredChart = chart;
        //System.out.println("hooveredChart = chart ->"+hooveredChart+" chart="+chart);
    }
    
    // H A N D L E R S
    /**
     * handler for change property
     */
    public void handleChartProperty(LineChart chart){
        System.out.println("proprty of chart"+ chart.getYAxis().getLabel());
        
    }
    
    public void handleDeleteChart(LineChart chart){
        
    }
    
    public void handleLegendChange(CheckBox checkBox){
        
        int ind = multiChart.indexOf((LineChart)((ChartLegend.UserData)checkBox.getUserData()).chart);
        final List<XYChart.Series<?,?>> s = multiChart.get(ind).getData();
        for (int i=0; i < s.size(); i++){
            s.get(i).getNode().setVisible(checkBox.isSelected());
        }
        //System.out.println("chartMode = "+chartMode);
        switch (chartMode) {
            case CHART: //  Chart mode
            {
                //System.out.println("Mode=CHART. clicked on "+checkBox);
                break;
            } 
            case MARKER: // Marker mode
            default: 
            {
                for (CheckBox cb: legend.getCheckBoxes()){
                    if (checkBox.equals(cb)) {
                        if (checkBox.isSelected()){
                            //System.out.println("Mode=MARKER. clicked on "+cb);
                            // remember the selected chart
                            markerModeChart = (LineChart)((ChartLegend.UserData)checkBox.getUserData()).chart;
                            //bindMouseEvents(markerModeChart, strokeWidthNorm);
                        }
                    }else{
                        if (checkBox.isSelected() && cb.isSelected()) {
                            //System.out.println("Mode=MARKER. remove selection from "+cb);
                            cb.setSelected(false);

                        }
                    }
                }
                break;
            }

        }
    }
    
    // G E T T E R S  /  S E T T E R S

    public LineChart  getHooverChart(){
        return hooverChart;
    }
//    public Map<LineChart, Color> getColorMap(){
//        return chartColorMap;
//    }
    
    public Map<LineChart, GrProperty> getPropertyMap(){
        return chartPropertyMap;
    }
    
    public Side getLegendSide(){
        return legendSide;
    }
    
    public void setLegendSide(Side side){
        this.legendSide = side;
    }
    
    public Node getLegend() {
        for(LineChart chart: multiChart){
            legend.add(chart);
        }
        return legend;
    }
    
    public ObservableList<LineChart> getMultiChart(){
        return multiChart;
    }
    
        /**
     * looking up for the first selected checkbox 
     * in MARKER mode only one selected checkbox is allowed
     * @return checkbox or null
     */
    private CheckBox selectedCheckBox(){
        for (CheckBox cb: legend.getCheckBoxes()){
            if (cb.isSelected()) return cb;
        }
        //System.out.println("No checkbox is selected in chartmode="+chartMode);
        return null;
    }
    
    /**
     * Switch mode of chart
     * available modes are :
     *      Chart - when user can see all lines
     *      Marker - where user can define new or edit exiting markers for individual line
     * 
     */
    private void toggleMode(){

        //System.out.println("ToggleMode Start chartMode="+chartMode);
        switch (chartMode) {
            case CHART: // toggle CHART -> MARKER
                        chartMode = ChartModes.MARKER;

                        for (CheckBox cb: legend.getCheckBoxes()){
                            ChartLegend.UserData userData = (ChartLegend.UserData)cb.getUserData();
                            userData.chartSelected = cb.isSelected();
                            cb.setUserData(userData); //keep status of checkbox to userdata
                            cb.setSelected((boolean)((ChartLegend.UserData)cb.getUserData()).markerSelected);
                            // stop getting mouse event on base chart
                            setMouseTransparent(false);
                        }
                        
                        
                        break;
            case MARKER: // TOGGLE MARKET -> CHART
            default:    chartMode = ChartModes.CHART;

                        for (CheckBox cb: legend.getCheckBoxes()){
                            ChartLegend.UserData userData = (ChartLegend.UserData)cb.getUserData();
                            userData.markerSelected = cb.isSelected();
                            cb.setSelected((boolean)((ChartLegend.UserData)cb.getUserData()).chartSelected); // restore status of checkbox from userdata
                        }
                        // restore getting mouse event on base chart
                        setMouseTransparent(false);
                        break;
        }
        //System.out.println("handleModeButton" + b.getUserData());
        //System.out.println("ToggleMode End chartMode="+chartMode);
        if (menuView != null && menuView.getItems().size() >0) menuView.getItems().clear();
        getMenuView();
        if (menuTools != null && menuTools.getItems().size() >0) menuTools.getItems().clear();
        getMenuTools();
    }
    
    
    private void handleEdit() {
        System.out.println("handleEdit");

    }
    
    private void handleDelete() {
        System.out.println("handleDelete");

    }
    
    
    
    // M E N U items supported by this class
    
    
    public Menu getMenuTools(){
        
        boolean MARKER;  //control what to diasable in menu
        if (chartMode == ChartModes.MARKER){
            MARKER=false;
        } else {
            MARKER=true;
        }
        if (menuTools == null)  menuTools = new Menu("Tools");
        MenuItem menuItem;
        menuTools.getItems().add(new SeparatorMenuItem());
        CustomMenuItem customMenuItem2 = new CustomMenuItem(new Label("<Marker mode>"));
        customMenuItem2.setHideOnClick(false);
        customMenuItem2.setDisable(MARKER);

        menuItem = new MenuItem("Add Marker");
        menuItem.setDisable(MARKER);
        menuItem.setOnAction(e->addMarkerToChart());
        menuTools.getItems().addAll(customMenuItem2, menuItem);
        
        menuItem = new MenuItem("Edit Marker");
        menuItem.setDisable(MARKER);
        menuItem.setOnAction(e->editMarker());
        menuTools.getItems().add(menuItem);
        
        menuItem = new MenuItem("Delete Marker");
        menuItem.setDisable(MARKER);
        menuItem.setOnAction(e->deleteMarker());
        menuTools.getItems().add(menuItem);
        
        menuTools.getItems().add(new SeparatorMenuItem());
        CustomMenuItem customMenuItem3 = new CustomMenuItem(new Label("<Customize>"));
        customMenuItem3.setHideOnClick(!MARKER);
        customMenuItem3.setDisable(!MARKER);
        
        menuItem = new MenuItem("Customize Chart");
        menuItem.setDisable(!MARKER);
        menuItem.setOnAction(e->chartCustomize());
        menuTools.getItems().addAll(customMenuItem3, menuItem);
        return menuTools;
    }
    
    /**
     * View menu
     * @return 
     */
    public Menu getMenuView(){
        ToggleGroup toggleGroupMode = new ToggleGroup();
        RadioMenuItem radioItem;

        boolean modeChart, modeMarker;
        if (chartMode == ChartModes.CHART){
            modeChart  = true;
            modeMarker = false;
        } else {
            modeChart  = false;
            modeMarker = true;
        }
        if (menuView == null)  menuView = new Menu("View");
        
        radioItem = new RadioMenuItem("CHART");
        radioItem.setOnAction(e->toggleMode());
        radioItem.setSelected(modeChart);
        
        radioItem.setToggleGroup(toggleGroupMode);
        menuView.getItems().add(radioItem);
        
        radioItem = new RadioMenuItem("MARKER");
        radioItem.setOnAction(e->toggleMode());
        radioItem.setSelected(modeMarker);
        
        radioItem.setToggleGroup(toggleGroupMode);
        menuView.getItems().add(radioItem);
        
        
        
        // to trigger disabling of menu items depending on mode
        boolean CHART;
        boolean MARKER;
        if (chartMode == ChartModes.MARKER){
            CHART=true;
            MARKER=false;
        } else {
            CHART=false;
            MARKER=true;
        }
        
        ToggleGroup toggleGroupChartView = new ToggleGroup();
        menuView.getItems().add(new SeparatorMenuItem());
        CustomMenuItem customMenuItem1 = new CustomMenuItem(new Label("<Chart View>"));
        customMenuItem1.setHideOnClick(false);
        customMenuItem1.setDisable(CHART);
        
        radioItem = new RadioMenuItem("Stacked");
        radioItem.setDisable(CHART);
        if (stackedChartView)radioItem.setSelected(true);
        radioItem.setOnAction(e->stackedChartView());
        radioItem.setToggleGroup(toggleGroupChartView);
        menuView.getItems().addAll(customMenuItem1, radioItem);
    
        radioItem = new RadioMenuItem("Zero Alligned");
        radioItem.setDisable(CHART);
        if (zeroChartView)radioItem.setSelected(true);
        radioItem.setOnAction(e->zeroChartView());
        radioItem.setToggleGroup(toggleGroupChartView);
        menuView.getItems().add(radioItem);
        
        radioItem = new RadioMenuItem("Custom Alligned");
        radioItem.setDisable(CHART);
        if (customChartView)radioItem.setSelected(true);
        radioItem.setOnAction(e->chartCustomAllignedView());
        radioItem.setToggleGroup(toggleGroupChartView);
        menuView.getItems().add(radioItem);
        
        
        
        menuView.getItems().add(new SeparatorMenuItem());
        ToggleGroup toggleGroupMarkerView = new ToggleGroup();
        CustomMenuItem customMenuItem3 = new CustomMenuItem(new Label("<Marker view>"));
        customMenuItem3.setHideOnClick(false);
        customMenuItem3.setDisable(MARKER);
        
        radioItem = new RadioMenuItem("Stacked");
        radioItem.setDisable(MARKER);
        if (stackedMarkerView)radioItem.setSelected(true);
        radioItem.setOnAction(e->stackedMarkerView());
        radioItem.setToggleGroup(toggleGroupMarkerView);
        menuView.getItems().addAll(customMenuItem3, radioItem);
        
        radioItem = new RadioMenuItem("Single Chart");
        radioItem.setDisable(MARKER);
        if (singleMarkerView)radioItem.setSelected(true);
        radioItem.setOnAction(e->singleMarkerView());
        radioItem.setToggleGroup(toggleGroupMarkerView);
        menuView.getItems().add(radioItem);
        

        return menuView;
    }    

    
    
    // Menu related methods
 
    final void deleteLineFromChart(){
        
    }
    final void stackedChartView(){
        exitCustomChartView();
        stackedChartView = true;
        zeroChartView = false;
        customChartView = false;
        baseChart.setHorizontalZeroLineVisible(false);
        for (LineChart lineChart : multiChart) {
            final NumberAxis yAxis = (NumberAxis)lineChart.getYAxis();
            yAxis.setAutoRanging(true);
        }
        
    }
    final void zeroChartView(){
        exitCustomChartView();
        stackedChartView = false;
        zeroChartView = true;
        customChartView = false;
        baseChart.setHorizontalZeroLineVisible(true);
        //-fx-horizontal-zero-line-visible

        for (LineChart lineChart : multiChart) {
            //LineChart.Series<?,?> series = (LineChart.Series<?,?>)lineChart.getData().get(0);
            final NumberAxis xAxis = (NumberAxis)lineChart.getXAxis();
            final NumberAxis yAxis = (NumberAxis)lineChart.getYAxis();
            //double xOffset = xAxis.localToScene();
            double upperBound = yAxis.getUpperBound();
            double lowerBound = yAxis.getLowerBound();
            if (abs(upperBound) > abs(lowerBound)){
                yAxis.setAutoRanging(false);
                yAxis.setUpperBound(upperBound);
                yAxis.setLowerBound(-upperBound);
            } else{
                yAxis.setAutoRanging(false);
                yAxis.setUpperBound(abs(lowerBound));
                yAxis.setLowerBound(-abs(lowerBound));
            }
        }
        
    }
    
    /** 
     * adds BorderPane for manipulating parameters of multiChart
     */
    final void chartCustomAllignedView(){
        stackedChartView = false;
        zeroChartView = false;
        customChartView = true;
        rebuildChart();
        getChildren().add(new MultiGraphCustomView(this));
        
    }
    
    /**
     * removes BorderPane added for Custom view
     */
    final void exitCustomChartView(){
        
        if (customChartView) {
            //getChildren().remove(legend.getCheckBoxes().size()+1);
            customChartView = false;
            rebuildChart();
        }
    }
   
    final void chartCustomize(){
        stackedChartView = false;
        zeroChartView = false;
        customChartView = true;
        //chartMode = ChartModes.CUSTOMIZATION;
        rebuildChart();
        custLib = new MultiGraphCustomView(this);
        getChildren().add(custLib);
    }
    
    final void addMarkerToChart(){
        
    }
    final void editMarker(){
        
    }
    final void deleteMarker(){
        
    }
    final void stackedMarkerView(){
        stackedMarkerView = true;
        singleMarkerView = false;
        
    }
    final void singleMarkerView(){
        stackedMarkerView = false;
        singleMarkerView = true;
        
    }

    //=======================
    
    private class DetailsPopup extends VBox {

        private DetailsPopup() {
            setStyle("-fx-border-width: 1px; -fx-padding: 5 5 5 5px; -fx-border-color: gray; -fx-background-color: whitesmoke;");
            setVisible(false);
        }

        public void showChartDescrpition(MouseEvent event) {
            getChildren().clear();

            Long xValueLong = Math.round((double)baseChart.getXAxis().getValueForDisplay(event.getX()));

            for (LineChart lineChart : multiChart) {
                HBox popupRow = buildPopupRow(event, xValueLong, lineChart);
                if (popupRow == null) continue;

                getChildren().add(popupRow);
                chartGlow(event, xValueLong, lineChart);
            }
        }

        private HBox buildPopupRow(MouseEvent event, Long xValueLong, LineChart lineChart) {
            Label seriesName = new Label(lineChart.getYAxis().getLabel());
            seriesName.setTextFill(chartColorMap.get(lineChart));

            Number yValueForChart = getYValueForX(lineChart, xValueLong.intValue());
            if (yValueForChart == null) {
                return null;
            }
            Number yValueLower = Math.round(normalizeYValue(lineChart, event.getY() - 10));
            Number yValueUpper = Math.round(normalizeYValue(lineChart, event.getY() + 10));
            Number yValueUnderMouse = Math.round((double) lineChart.getYAxis().getValueForDisplay(event.getY()));

            // make series name bold when mouse is near given chart's line
            if (isMouseNearLine(yValueForChart, yValueUnderMouse, Math.abs(yValueLower.doubleValue()-yValueUpper.doubleValue()))) {
                seriesName.setStyle("-fx-font-weight: bold");
            }

            HBox popupRow = new HBox(10, seriesName, new Label("[Y="+yValueForChart+"]"));
            return popupRow;
        }

        private double normalizeYValue(LineChart lineChart, double value) {
            Double val = (Double) lineChart.getYAxis().getValueForDisplay(value);
            if (val == null) {
                return 0;
            } else {
                return val;
            }
        }
        private double normalizeXValue(LineChart lineChart, double value) {
            Double val = (Double) lineChart.getXAxis().getValueForDisplay(value);
            if (val == null) {
                return 0;
            } else {
                return val;
            }
        }

        private boolean isMouseNearLine(Number realYValue, Number yValueUnderMouse, Double tolerance) {
            return (Math.abs(yValueUnderMouse.doubleValue() - realYValue.doubleValue()) < tolerance);
        }

        public Number getYValueForX(LineChart chart, Number xValue) {
            List<XYChart.Data> dataList = ((List<XYChart.Data>)((XYChart.Series)chart.getData().get(0)).getData());
            for (XYChart.Data data : dataList) {
                if (data.getXValue().equals(xValue)) {
                    return (Number)data.getYValue();
                }
            }
            return null;
        }
        private void chartGlow(MouseEvent event, Long xValueLong, LineChart lineChart) {
            Label seriesName = new Label(lineChart.getYAxis().getLabel());
            //seriesName.setTextFill(chartColorMap.get(lineChart));
            seriesName.setTextFill(Color.web(chartPropertyMap.get(lineChart).getChartColor()));
            
            

            Number yValueForChart = getYValueForX(lineChart, xValueLong.intValue());
            if (yValueForChart == null) {
                return ;
            }
            double tolerance;
            if (customChartView) {
                tolerance = 1;
            } else {
                tolerance = 10;
            }
            Number yValueLower = Math.round(normalizeYValue(lineChart, event.getY() - tolerance));
            Number yValueUpper = Math.round(normalizeYValue(lineChart, event.getY() + tolerance));
            Number yValueUnderMouse = Math.round((double) lineChart.getYAxis().getValueForDisplay(event.getY()));

            // make series line bold when mouse is near given chart's line
            if (isMouseNearLine(yValueForChart, yValueUnderMouse, Math.abs(yValueLower.doubleValue()-yValueUpper.doubleValue()))) {
                //if (chartHooverMap.get(lineChart) == false) {    // to avoid redrawing if chart was not hoovered yet but is hoovered now
                //if (chartPropertyMap.get(lineChart).getChartHoover()) {   // to avoid redrawing if chart was not hoovered yet but is hoovered now
                    //styleChartBoldLine(lineChart, chartColorMap.get(lineChart));
                    styleChartBoldLine(lineChart, Color.web(chartPropertyMap.get(lineChart).getChartColor()));
                    chartPropertyMap.get(lineChart).setChartHoover(true);
                    //chartHooverMap.put(lineChart, true);
                    if (customChartView){
                        custLib.customize(lineChart);
                        System.out.println("hooveredChart = chart ->"+chartPropertyMap.get(lineChart).getChartHoover());
                    }
                    
                //}
            } else {
                //if (chartHooverMap.get(lineChart)) {    // to avoid redrawingif chart was  hoovered before and not hovered now
                //if (chartPropertyMap.get(lineChart).getChartHoover()) {   // to avoid redrawing if chart was not hoovered yet but is hoovered now
                    //styleChartLine(lineChart, chartColorMap.get(lineChart));
                    styleChartLine(lineChart, Color.web(chartPropertyMap.get(lineChart).getChartColor()));
                    //chartHooverMap.put(lineChart, false);
                    chartPropertyMap.get(lineChart).setChartHoover(false);
                    hooverChart = null;
                //}
            }
            //System.out.println("hooveredChart = chart ->"+chartPropertyMap.get(lineChart).getChartHoover());
           
        }
    }
    
}

