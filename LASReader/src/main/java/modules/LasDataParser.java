package main.java.modules;


import main.java.content.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LasDataParser {
    private static final Logger logger = LoggerFactory.getLogger(LasDataParser.class); // Create instance of logger
    private LasReader reader;
    private final String BLOCK_SEPARATOR = "~", INFO_SEPARATOR = "#"; // Separators are constant
    private Section currentSection = null; // Current section of .las file
    private WellObjectType currentType = null; // Current type of created parameter
    private int currentLine = 0; // Current file line
    private boolean isWrapped = false; // Is values section wrapped
    private enum Section {V, W, C, A, P, O} // .las file sections as enum
    private String lasVersion;
    private HashMap<String, WellHeader> headers = new HashMap<>();
    private LinkedHashMap<String, WellLog> logs = new LinkedHashMap<>();
    private HashMap<String, WellParameter> parameters = new HashMap<>();

    public LasDataParser(LasReader reader) {
        this.reader = reader;
    }

    /**
     * Parse line
     *
     * @param line from LasReader
     */
    public void parseLine(String line) {
        currentLine++;
        if (line.startsWith(BLOCK_SEPARATOR)) { // If line is another block
            setCurrentSectionAndType(line); // Get current block's section
            logger.debug("\tSwitched section to: {}", currentSection);
        } else if (!line.startsWith(INFO_SEPARATOR) && currentSection != null) { // If line is not info and currentSection is defined
            switch (currentSection) {
                case V: // VERSION - get las version and check if LAS is wrapped (Multiple lines per depth step in A section)
                    int pointIndex = line.indexOf(".");
                    int colonIndex = line.indexOf(":");
                    String value = line.substring(pointIndex, colonIndex).replaceAll("\\s+", "");
                    if (line.startsWith("VERS")) {
                        lasVersion = value;
                        logger.debug("\tLAS Version: {}", lasVersion);
                    } else if (line.startsWith("WRAP") && value.equals("YES")) {
                        isWrapped = true;
                    }
                    break;
                case W: // WELL - merged with P
                case C: // CURVE - merged with P
                case P:
                    if (currentType != null) {
                        createParameter(line);
                    }
                    break;
                case A:
                    List<String> values = new ArrayList<>();
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
                            logger.error("\t{} while trying to parse line {}\n\tline is: {}\n\t{}", e, currentLine, line, e.getMessage());
                            break;
                        }
                    }
                    addLogValues(values);
                    break;
                case O:
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
        }
    }

    /**
     * General method to add Well parameters from line
     */
    private void createParameter(String line) {
        String name = null, unit = null, value = null, desc = null;
        WellParameter parameter = null;
        Pattern pattern = Pattern.compile("\\.?([^.]*)\\.([^\\s]*)(.*):(.*)");
        Matcher matcher = pattern.matcher(line);

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

    public HashMap<String, WellHeader> getHeaders() {
        return headers;
    }

    public LinkedHashMap<String, WellLog> getLogs() {
        return logs;
    }

    public HashMap<String, WellParameter> getParameters() {
        return parameters;
    }
}
