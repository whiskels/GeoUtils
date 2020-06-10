package main.java.content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Well class
 * Stores well name and well parameters in maps
 */

public final class Well extends WellObject {
    private HashMap<String, WellHeader> headers;
    private LinkedHashMap<String, WellLog> logs;
    private HashMap<String, WellParameter> parameters;

    public Well(String name) {
        super(name);
        this.logs = new LinkedHashMap<>();
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
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

    public void addHeaders(HashMap<String, WellHeader> headers) {
        this.headers.putAll(headers);
    }

    public void addParameters(HashMap<String, WellParameter> parameters) {
        this.parameters.putAll(parameters);
    }

    public void addLogs(LinkedHashMap<String, WellLog> logs) {
        this.logs.putAll(logs);
    }
}