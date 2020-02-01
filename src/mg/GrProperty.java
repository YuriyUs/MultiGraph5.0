/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mg;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author us
 */
public class GrProperty {
    //private  IntegerProperty legendNum;   // number on the Legend list
    private  StringProperty chartDataFile;  // SCV file name with data
    private  StringProperty chartName;      // Tag chart on Legend. Default is dataFile
    private  BooleanProperty chartCB;       // Checkbox Hide/Show chart on the graph. Checked to Show
    private  DoubleProperty xShift;         // Shift curve along X axis.
    private  DoubleProperty xScale;         // Scale along X axis.
    private  DoubleProperty yShift;         // Shift curve along Y axis.
    private  DoubleProperty yScale;         // Scale along Y axis.
    private  StringProperty chartColor;     // char Color on the graph
    private  IntegerProperty xUnits;        // index of Unit on from ObservableList of xUnits
    private  StringProperty xUnit;          // value of xUnit
    private  StringProperty yUnit;          // value for yUnit
    private  StringProperty dateFormat;     // date time string format. used to decode data timestamp
    private  BooleanProperty chartHoover;    // true is chart under cursor
    
    public GrProperty(){
        initialization();
    }
    private void initialization(){

        this.chartDataFile = new SimpleStringProperty("");
        this.chartName = new SimpleStringProperty("");
        this.chartCB = new SimpleBooleanProperty(true); // not checked = to hide the curve
        this.xShift = new SimpleDoubleProperty(0.0);
        this.xScale = new SimpleDoubleProperty(1.0);
        this.yShift = new SimpleDoubleProperty(0.0);
        this.yScale = new SimpleDoubleProperty(1.0);
        this.chartColor = new SimpleStringProperty("RED");
        this.xUnits = new SimpleIntegerProperty(-1);     // value not set
        this.xUnit = new SimpleStringProperty("");      // value not set
        this.yUnit = new SimpleStringProperty("");      // value not set
        this.dateFormat = new SimpleStringProperty("yyyy.MM.dd HH:mm");
        this.chartHoover = new SimpleBooleanProperty(false); 

    }
    
    //  =======   setters / getters  =============

    public final String getChartDataFile() {
        return chartDataFile.get();
    }

    public final void setChartDataFile(String value) {
        chartDataFile.set(value);
    }

    public StringProperty chartDataFileProperty() {
        return chartDataFile;
    }

    public final String getChartName() {
        return chartName.get();
    }

    public final void setChartName(String value) {
        chartName.set(value);
    }

    public StringProperty chartNameProperty() {
        return chartName;
    }

    public final boolean isChartCB() {
        return chartCB.get();
    }

    public final void setChartCB(boolean value) {
        chartCB.set(value);
    }

    public BooleanProperty chartCBProperty() {
        return chartCB;
    }

    public final double getXShift() {
        return xShift.get();
    }

    public final void setXShift(double value) {
        xShift.set(value);
    }

    public DoubleProperty xShiftProperty() {
        return xShift;
    }

    public final double getXScale() {
        return xScale.get();
    }

    public final void setXScale(double value) {
        xScale.set(value);
    }

    public DoubleProperty xScaleProperty() {
        return xScale;
    }

    public final double getYShift() {
        return yShift.get();
    }

    public final void setYShift(double value) {
        yShift.set(value);
    }

    public DoubleProperty yShiftProperty() {
        return yShift;
    }

    public final double getYScale() {
        return yScale.get();
    }

    public final void setYScale(double value) {
        yScale.set(value);
    }

    public DoubleProperty yScaleProperty() {
        return yScale;
    }

    public final String getChartColor() {
        return chartColor.get();
    }

    public final void setChartColor(String value) {
        chartColor.set(value);
    }

    public StringProperty chartColorProperty() {
        return chartColor;
    }

    public final int getXUnits() {
        return xUnits.get();
    }

    public final void setXUnits(int value) {
        xUnits.set(value);
    }

    public IntegerProperty xUnitsProperty() {
        return xUnits;
    }

    public final String getXUnit() {
        return xUnit.get();
    }

    public final void setXUnit(String value) {
        xUnit.set(value);
    }

    public StringProperty xUnitProperty() {
        return xUnit;
    }

    public final String getYUnit() {
        return yUnit.get();
    }

    public final void setYUnit(String value) {
        yUnit.set(value);
    }

    public StringProperty yUnitProperty() {
        return yUnit;
    }

    public final String getDateFormat() {
        return dateFormat.get();
    }

    public final void setDateFormat(String value) {
        dateFormat.set(value);
    }

    public StringProperty dateFormatProperty() {
        return dateFormat;
    }
    
    public final boolean getChartHoover() {
        return chartHoover.get();
    }

    public final void setChartHoover(boolean value) {
        chartHoover.set(value);
    }

    public BooleanProperty chartHooverProperty() {
        return chartHoover;
    }
    
   
    
    /**
     * Set Listeners to track any changes in the MG chart
     */
    public void setListener(){
        // Checkbox in Legend changed
        this.chartCB.addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (oldValue != newValue)  {
                    setProjectNotSaved("chartCB"); // Change to the Setups are not saved
                }
            }
        });
        
        // Name of CSV file with timeline data changed
        this.chartName.addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                 if (oldValue != newValue)  {
                    setProjectNotSaved("chartName = "+newValue); // Change to the Setups are not saved
                }
            }
        });
        
        this.xShift.addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                 if (oldVal != newVal)  {
                    setProjectNotSaved("xShift = "+newVal); // Change to the Setups are not saved
                }
            }
        });
        this.xScale.addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                 if (oldVal != newVal)  {
                    setProjectNotSaved("xScale = "+newVal); // Change to the Setups are not saved
                }
            }
        });
        this.yShift.addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                setProjectNotSaved("yShift = "+newVal); // Change to the Setups are not saved
            }
        });
        this.yScale.addListener(new ChangeListener(){
            @Override
            public void changed(ObservableValue o, Object oldVal, Object newVal) {
                if (oldVal != newVal)  {
                    setProjectNotSaved("yScale = "+newVal); // Change to the Setups are not saved
                }
            }
        });
        
        this.chartColor.addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldVal, String newVal) {
                 if (oldVal != newVal)  {
                    setProjectNotSaved("chartColor = "+newVal); // Change to the Setups are not saved
                }
            }
        });
        

    }
    
    void setProjectNotSaved(String property){
        System.out.println("Changed property "+property);
    }

}
