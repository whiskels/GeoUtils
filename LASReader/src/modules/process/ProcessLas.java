package modules.process;

import java.io.BufferedReader;
import java.io.IOException;
import main.Main;
import content.Well;
import content.WellObjectType;
import content.WellParameter;
import content.WellParameterFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ProcessLas {
    private static String BLOCK_SEPARATOR = "~";
    private static String INFO_SEPARATOR = "#";

    private enum Section {V, W, C, A, P, O}

    public static void go(List<Path> input) {
        ArrayList<Well> result = new ArrayList<Well>();
        for (Path path : input) {
            Main.getWells().add(go(path));
        }
    }

    public static Well go(Path input) {
        // Create well (file name without extension)
        Well well = new Well(input.getFileName().toString().replaceFirst("[.][^.]+$", ""));
        Main.LOGGER.info("Processing well " + well.getName());
        readLasContent(well, input);
        return well;
    }

    private static void readLasContent(Well well, Path input) {
        // Create BufferedReader to read file line by line
        try (BufferedReader bf = Files.newBufferedReader(input, StandardCharsets.UTF_8)) {

            // Read las
            Section currentSection = null;
            String line;
            boolean isWrapped;
            while (bf.ready()) {
                line = bf.readLine();
                if (line.startsWith(BLOCK_SEPARATOR)) { // If line is another block
                    currentSection = getCurrentSection(line); // Get current block's section
                } else if (!line.startsWith(INFO_SEPARATOR) && currentSection != null) { // If line is not info and currentSection is defined
                    switch (currentSection) {
                        case V: // VERSION - get las version and check if LAS is wrapped (Multiple lines per depth step in A section)
                            /**
                             * Example of VERSION section
                             * ~Version Information
                             * #----------------------------------------------------------------
                             * VERS.             1.20: CWLS log ASCII Standard - VERSION 1.20
                             * WRAP.             YES : Multiple lines per depth step
                             */
                            int pointIndex = line.indexOf(".");
                            int colonIndex = line.indexOf(":");
                            String value = line.substring(pointIndex, colonIndex).replaceAll("\\s+", "");
                            if (line.startsWith("VERS")) {
                                String lasVersion = value;
                            } else if (line.startsWith("WRAP") && value.equals("YES")) {
                                isWrapped = true;
                            }
                            break;
                        case W: // WELL - get well information and create well Headers
                            getWellParameter(well, line, WellObjectType.HEADER);
                            break;
                        case C:
                            getWellParameter(well, line, WellObjectType.LOG);
                            break;
                        case A:
                            // TODO parse this section
                            break;
                        case P:
                            getWellParameter(well, line, WellObjectType.PARAMETER);
                            break;
                        case O:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            Main.LOGGER.severe(e + " " + e.getMessage());
        }
    }

    public static Section getCurrentSection(String line) {
        Section cs = null;
        switch (line.charAt(1)) {
            case 'V' :
                cs = Section.V;
                break;
            case 'W' :
                cs = Section.W;
                break;
            case 'C' :
                cs = Section.C;
                break;
            case 'P' :
                cs = Section.P;
                break;
            case 'A' :
                cs = Section.A;
        }
        return cs;
    }

    private static void getWellParameter(Well well, String line, WellObjectType type) {
        int pointIndex = line.indexOf(".");
        int colonIndex = line.indexOf(":");
        String name = line.substring(0, pointIndex++).replaceAll("\\s+", "");

        WellParameterFactory factory = new WellParameterFactory(); // Create parameter using factory method
        WellParameter parameter = factory.createParameter(type,name);

        if (!Character.isWhitespace(line.charAt(pointIndex))) // Check if unit is defined
            parameter.setUnit(line.substring(pointIndex, 9).replaceAll("\\s+", ""));

        String data = line.substring(10, colonIndex++).replaceAll("\\s+", "");
        if (data.length() != 0) {
            parameter.setValue(data);
        }

        String info = line.substring(colonIndex);
        if (info.length() != 0) {
            parameter.setDescription(info);
        }

        well.addParameter(parameter); // Add parameter to corresponding well
    }
}
