package content;

public class WellParameterFactory {
    public WellParameter createParameter (WellObjectType type, String name) {
        WellParameter object = null;

        switch (type) {
            case LOG:
                object = new WellLog(name);
                break;
            case HEADER:
                object = new WellHeader(name);
                break;
            case PARAMETER:
                object = new WellParameter(name);
                break;
        }
        return object;
    }
}
