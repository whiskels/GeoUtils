package main.java.content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

/**
 * Well Log class
 * Stores log curve values in "depth - values" linked hash map
 */


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

    /**
     * Initializes map to store values.
     * In case there are no values in las map is not initialized in constructor
     */
    public void initializeMap() {
        this.values = new LinkedHashMap<Double,Double>();
    }

    /**
     * Adds log values to map
     *
     * @param depth value of depth log
     * @param value value of curve
     */
    public void addValue(String depth, String value) {
        if (this.values == null) {
            initializeMap();
        }
        try {
            values.put(Double.parseDouble(depth), Double.parseDouble(value));
        } catch (NumberFormatException e) {
            logger.error("Caught {} while trying to add log values for log {}", e, getName());
        }
    }

    public LinkedHashMap<Double,?> getValues() {
        return values;
    }
}
