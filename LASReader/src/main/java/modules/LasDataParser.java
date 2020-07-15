package modules;

import content.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LasDataParser {
    private static final Logger logger = LoggerFactory.getLogger(LasDataParser.class); // Create instance of logger
    private final String BLOCK_SEPARATOR = "~";
    private final String INFO_SEPARATOR = "#"; // Separators are constant
    private final Map<String, WellHeader> headers = new HashMap<>();
    private final Map<String, WellLog> logs = new LinkedHashMap<>();
    private final Map<String, WellParameter> parameters = new HashMap<>();
    private LasReader reader;
    private Section currentSection; // Current section of .las file
    private WellObjectType currentType; // Current type of created parameter
    private int currentLine; // Current file line
    private String lasVersion;
    // private boolean isWrapped; // Is values section wrapped

    public LasDataParser(LasReader reader) {
        this.reader = reader;
    }

    /**
     * Parse line
     *
     * @param string from LasReader
     */
    public final void parseLine(String string) {
        String line = string;
        currentLine++;

        if (line.startsWith(BLOCK_SEPARATOR)) { // If line is another block
            setCurrentSectionAndType(line); // Get current block's section

            logger.debug("\tSwitched section to: {}", currentSection);
        } else if (!line.startsWith(INFO_SEPARATOR) && currentSection != null) {
            switch (currentSection) {
                case V: // VERSION - get las version and check if LAS is wrapped
                    final int pointIndex = line.indexOf(".");
                    final int colonIndex = line.indexOf(":");
                    final String value = line.substring(pointIndex, colonIndex)
                            .replaceAll("\\s+", "");

                    if (line.startsWith("VERS")) {
                        lasVersion = value;
                        logger.debug("\tLAS Version: {}", lasVersion);
                    }
                    /* else if (line.startsWith("WRAP") && value.equals("YES")) {
                        isWrapped = true;
                    } */
                    break;
                case W: // WELL - drop through
                case C: // CURVE - drop through
                case P:
                    if (currentType != null) {
                        createParameter(line);
                    }
                    break;
                case A:
                    final List<String> values = new ArrayList<>();

                    while (values.size() != logs.size()) { // Accumulate data until values size equals log curves size
                        try {
                            if (Character.isWhitespace(line.charAt(0))) {
                                line = line.replaceFirst("\\s+", "");
                            }

                            line = line.replaceAll("\\s+", " ");
                            values.addAll(Arrays.asList(line.split(" ")));

                            if (values.size() != logs.size()) {
                                line = reader.readLine();
                                currentLine++;
                            }
                        } catch (Exception e) {
                            logger.error("\t{} while trying to parse line {}\n\tline is: {}\n\t{}",
                                    e, currentLine, line, e.getMessage());
                            break;
                        }
                    }

                    addLogValues(values);
                    break;
                case O:
                default:
                    break;
            }
        }
    }

    /**
     * Sets current section and type from line that starts with BLOCK_SEPARATOR
     */
    private void setCurrentSectionAndType(String line) {
        switch (line.charAt(1)) {
            case 'V':
                currentSection = Section.V;
                currentType = null;
                break;
            case 'W':
                currentSection = Section.W;
                currentType = WellObjectType.HEADER;
                break;
            case 'C':
                currentSection = Section.C;
                currentType = WellObjectType.LOG;
                break;
            case 'P':
                currentSection = Section.P;
                currentType = WellObjectType.PARAMETER;
                break;
            case 'O':
                currentSection = Section.O;
                currentType = null;
                break;
            case 'A':
                currentSection = Section.A;
                currentType = null;
                break;
            default:
                logger.error("Undefined section");
                break;
        }
    }

    /**
     * General method to add Well parameters from line
     */
    private void createParameter(String line) {
        String name = null;
        String unit = null;
        String value = null;
        String desc = null;
        WellParameter parameter = null;
        final Pattern pattern = Pattern.compile("\\.?([^.]*)\\.([^\\s]*)(.*):(.*)");
        final Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            name = matcher.group(1).replaceAll(" ", "");
            unit = matcher.group(2).replaceAll(" ", "");
            value = matcher.group(3).replaceAll(" ", "");
            desc = matcher.group(4);
        }

        if (name != null && name.length() != 0) {
            parameter = currentType.getInstance(name);
            parameter.setNonEmptyFields(unit, value, desc);
        }

        addParameter(parameter);
    }

    /**
     * Adds log values
     *
     * @param values List of log values. Size of list must be equal to the size of map
     */
    public final void addLogValues(List<String> values) {
        if (values.size() == logs.size()) {
            int i = 0;
            for (Map.Entry<String, WellLog> entry : logs.entrySet()) {
                entry.getValue().addValue(values.get(0), values.get(i));
                i++;
            }
        }
    }

    /**
     * Adds well parameter
     *
     * @param object well parameter
     * @param <T>    generic type - object must extend WellObject class
     */
    public final <T extends WellObject> void addParameter(T object) {
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
                default:
                    logger.error("Undefined parameter");
                    break;
            }
        }
    }

    public Map<String, WellHeader> getHeaders() {
        return headers;
    }

    public Map<String, WellLog> getLogs() {
        return logs;
    }

    public Map<String, WellParameter> getParameters() {
        return parameters;
    }

    private enum Section {V, W, C, A, P, O} // .las file sections as enum
}
