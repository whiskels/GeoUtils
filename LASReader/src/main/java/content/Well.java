package main.java.content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Well class
 * Stores well name and well parameters in maps
 */

public class Well extends WellObject {
    private HashMap<String, WellHeader> headers;
    private LinkedHashMap<String, WellLog> logs;
    private HashMap<String, WellParameter> parameters;

    public Well(String name) {
        super(name);
        this.logs = new LinkedHashMap<>();
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
    }

    /**
     * Adds well parameter
     *
     * @param object well parameter
     * @param <T> generic type - object must extend WellObject class
     */
    public <T extends WellObject> void addParameter(T object) {
        if (object != null) {
            WellObjectType thisType = object.getType();
            switch (thisType) {
                case PARAMETER:
                    parameters.put(object.getName(), (WellParameter) object);
                    break;
                case LOG:
                    logs.put(object.getName(), (WellLog) object);
                    break;
                case HEADER:
                    headers.put(object.getName(), (WellHeader) object);
                    break;
            }
        }
    }

    /**
     * Adds log values
     *
     * @param values List of log values. Size of list must be equal to the size of map
     * */
    public void addLogValues(List<String> values) {
        if (values.size() == logs.size()) {
            int i = 0;
            for (Map.Entry<String, WellLog> entry : logs.entrySet()) {
                entry.getValue().addValue(values.get(0),values.get(i));
                i++;
            }
        }
    }

    public int getLogsSize() {
        if (this.logs.size() != 0) {
            Map.Entry<String, WellLog> entry = logs.entrySet().iterator().next();
            return entry.getValue().getValues().size();
        } else {
            return 0;
        }
    }

    public String toString() {
        return String.format("Well name: %s\n\t" +
                        "Headers: %s\n\t" +
                        "Logs: %s\n\t" +
                        "Total log lines count: %d\n\t" +
                        "Parameters: %s",
                getName(), headers.keySet().toString(), logs.keySet().toString(), getLogsSize(), parameters.keySet().toString());
    }

    public HashMap<String, WellLog> getLogs() {
        return logs;
    }

    public HashMap<String, WellHeader> getHeaders() {
        return headers;
    }

    public HashMap<String, WellParameter> getParameters() {
        return parameters;
    }

    public void addLog(WellLog log) {
        logs.put(log.getName(), log);
    }

    public void addHeader(WellHeader header) {
        headers.put(header.getName(), header);
    }
}
