package content;

import java.util.HashMap;
import java.util.Map;


public class Well {
    private String name;
    private HashMap<String, WellHeader> headers;
    private HashMap<String, WellLog> logs;
    private HashMap<String, WellParameter> parameters;


    public Well(String name) {
        this.name = name;
        this.logs = new HashMap<String,WellLog>();
        this.headers = new HashMap<String,WellHeader>();
        this.parameters = new HashMap<String,WellParameter>();
    }

    public String getName() {
        return name;
    }

    public HashMap<String, WellLog> getLogs() {
        return logs;
    }

    public HashMap<String, WellHeader> getHeaders() {
        return headers;
    }

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

    public String toString() {
        return String.format("Well name: %s\n" +
                "Well headers: %s\n" +
                "Well logs: %s\n" +
                "Total log lines count: %d",
                this.name, this.headers.keySet().toString(), this.logs.keySet().toString(), getLogsSize());
    }
}
