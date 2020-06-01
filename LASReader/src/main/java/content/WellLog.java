package main.java.content;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;


public class WellLog extends WellParameter {
    public static final Logger logger = LoggerFactory.getLogger(WellLog.class);
    private LinkedHashMap<Double, Double> values;

    public WellLog(String name) {
        super(name);

    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.LOG;
    }

    public LinkedHashMap<Double,?> getValues() {
        return values;
    }

    public void addValue(String depth, String value) {
        if (this.values == null) {
            initializeMap(value);
        }

        values.put(Double.parseDouble(depth),Double.parseDouble(value));
    }

    public void initializeMap(String value) {
        try {
            double a = Double.parseDouble(value);
            this.values = new LinkedHashMap<Double,Double>();
        } catch (NumberFormatException e) {
            logger.error("Caught {} while trying to initialize log values for log {}", e, getName());
        }
    }
}
