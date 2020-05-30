package main.java.content;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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

    public HashMap<String, WellLog> getLogs() {
        return logs;
    }

    public HashMap<String, WellHeader> getHeaders() {
        return headers;
    }

    public HashMap<String, WellParameter> getParameters() {return parameters;}

    public void addLog(WellLog log) {
        this.logs.put(log.getName(),log);
    }

    public void addHeader(WellHeader header) {
        this.headers.put(header.getName(),header);
    }

    public int getLogsSize() {
        if (this.logs.size() != 0) {
            Map.Entry<String, WellLog> entry = logs.entrySet().iterator().next();
            return entry.getValue().getValues().size();
        } else {
            return 0;
        }
    }

    public <T extends WellObject> void addParameter(T object) {
        if (object != null) {
            WellObjectType thisType = object.getType();
            switch (thisType) {
                case PARAMETER:
                    this.parameters.put(object.getName(), (WellParameter) object);
                    break;
                case LOG:
                    this.logs.put(object.getName(), (WellLog) object);
                    break;
                case HEADER:
                    this.headers.put(object.getName(), (WellHeader) object);
                    break;
            }
        }
    }

    public void addLogValues(List<String> values) {
        if (values.size() == this.logs.size()) {
            int i = 0;
            for (Map.Entry<String, WellLog> entry : this.logs.entrySet()) {
                entry.getValue().addValue(Double.parseDouble(values.get(i)));
                i++;
            }
        }
    }

    public String toString() {
        return String.format("Well name: %s\n\t" +
                "Headers: %s\n\t" +
                "Logs: %s\n\t" +
                "Total log lines count: %d\n\t" +
                "Parameters: %s",
                this.getName(), this.headers.keySet().toString(), this.logs.keySet().toString(), this.getLogsSize(), this.getParameters().size());
    }
}