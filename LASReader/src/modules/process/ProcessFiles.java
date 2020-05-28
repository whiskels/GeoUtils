package modules.process;

import main.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;


public class ProcessFiles {
    
    public static void go(String line) {

        Main.LOGGER.info("Input: " + line);
        Path input = new File(line).toPath();

        try {
            if (Files.exists(input)) { // Check if input exists
                if (Files.isDirectory(input)) {
                    Main.LOGGER.info("Input is a directory");
                    List<Path> lasFiles = findFiles(input, ".las");
                    if (lasFiles.size() != 0) {
                        ProcessLas.go(lasFiles);
                    } else {
                        throw new IllegalArgumentException("No files found.");
                    }
                } else {
                    Main.LOGGER.info("Input is a file. Creating well");
                    Main.addWell(ProcessLas.go(input));
                }
            } else {
                throw new IllegalArgumentException("Input doesn't exist.");
            }
        } catch (IllegalArgumentException e) {
            Main.LOGGER.severe(e + ". " + e.getMessage());
        }
    }

    // Find files in given directory
    public static List<Path> findFiles(Path folder, String fileExtension) {
        Main.LOGGER.info("Searching for files with " + fileExtension);
        List<Path> foundFiles = null;
        try {
            if (!Files.isDirectory(folder)) {
                throw new IllegalArgumentException("Not a folder.");
            } else {
                // Recursively find all files with given extension
                foundFiles = Files.walk(folder)
                        .filter(s -> containsIgnoreCase(s.toString(),fileExtension))
                        .map(Path::toAbsolutePath).sorted().collect(Collectors.toList());
            }
        } catch (IllegalArgumentException e) {
            Main.LOGGER.severe("    " + e + ". " + e.getMessage());
        } catch (IOException e) {
            Main.LOGGER.severe("    " + e);
        }

        if (foundFiles.size() != 0) {
            Main.LOGGER.info("   Found files: " + foundFiles.size());
            return foundFiles;
        } else {
            return null;
        }
    }

    private static boolean containsIgnoreCase(String inputString, String containsString) {
        // If containsString.length is 0 - always true;
        final int length = containsString.length();
        if (length == 0)
            return true;
        for (int i = inputString.length() - length; i >= 0; i--) {

            if (inputString.regionMatches(true, i, containsString, 0, length))
                return true;
        }
        return false;
    }
}

