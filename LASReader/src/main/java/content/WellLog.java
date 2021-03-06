package content;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Well Log class
 * Stores log curve values in "depth - values" linked hash map
 */

public final class WellLog extends WellParameter {
    private static final Logger logger = LoggerFactory.getLogger(WellLog.class);
    private Map<Double, Double> values;

    public WellLog(String name) {
        super(name);
    }

    @Override
    public WellObjectType getType() {
        return WellObjectType.LOG;
    }

    /**
     * Adds log values to map
     *
     * @param depth value of depth log
     * @param value value of curve
     */
    public void addValue(String depth, String value) {
        if (values == null) {
            initializeMap();
        }
        try {
            values.put(Double.parseDouble(depth), Double.parseDouble(value));
        } catch (NumberFormatException e) {
            logger.error("Caught {} while trying to add log values for log {}", e, getName());
        }
    }

    /**
     * Initializes map to store values.
     * In case there are no values in las map is not initialized in constructor
     */
    private void initializeMap() {
        this.values = new LinkedHashMap<>();
    }

    public Map<Double,?> getValues() {
        return values;
    }
}
