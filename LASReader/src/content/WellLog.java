package content;

import java.util.ArrayList;


public class WellLog extends WellParameter {
    private ArrayList<Double> values;

    public WellLog(String name) {
        super(name);
        this.values = new ArrayList<Double>();
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.LOG;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void addValue(double value) {
        this.values.add(value);
    }
}
