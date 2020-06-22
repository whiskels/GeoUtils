package modules;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * File Finder class
 * Finds files with designated extension
 */

public class FileFinder extends FileHandler {
    private boolean isValid, isFolder;

    public FileFinder(String line) {
        super(new File(line).toPath());
        logger.debug("Created fileProcessor\n\tInput string: {}", line);
    }

    /** Check if parameters given in constructor are valid */
    private void checkDataType() {
        isValid = isValid();
        isFolder = isFolder();
        logger.debug("Checking input:\n\t- is valid? {}\n\t- is folder? {}", this.isValid, this.isFolder);
    }

    /** Check if input exists */
    private boolean isValid() {
        return input != null && Files.exists(input);
    }

    /** Check if input is folder */
    private boolean isFolder() {
        if (isValid) {
            return Files.isDirectory(this.getInput());
        } else {
            return false;
        }
    }

    /**
     * Finds all matching files for an extension
     */
    public List<Path> findFiles(String fileExtension) {
        checkDataType();

        List<Path> foundFiles = new ArrayList<>();

        if (isValid) {
            if (isFolder) {
                foundFiles = findFilesInDirectory(fileExtension); // If input is folder - find all files with fileExtension
            } else if (containsIgnoreCase(input.toString(), fileExtension)) {
                foundFiles.add(input); // If input is a single file - add it to the list of found files
            }
        }

        return isEmpty(foundFiles);
    }

    /** Finds all files in the directory with given file extension */
    private List<Path> findFilesInDirectory(String fileExtension) {
        logger.debug("Searching for files with {}", fileExtension);

        List<Path> foundFiles = null;

        if (isFolder) {
            try {
                // Recursively find all files with given extension
                foundFiles = Files.walk(input)
                                  .filter(s -> containsIgnoreCase(s.toString(), fileExtension))
                                  .map(Path::toAbsolutePath)
                                  .sorted()
                                  .collect(Collectors.toList());
            } catch (IOException e) {
                logger.error("\t{} {}", e, e.getMessage());
            }
        }

        if (foundFiles != null) {
            logger.debug("\tFound {} files.", foundFiles.size());
        } else {
            logger.error("\tError. No files found.");
        }

        return isEmpty(foundFiles);
    }

    /** Checks if one string contains another using regionMatches
     *
     * @param inputString main string
     * @param containsString string that might be part of inputString
     * @return boolean contains ? true : false
     */
    private boolean containsIgnoreCase(String inputString, String containsString) {
        final int length = containsString.length();

        if (length == 0) {        // If containsString.length is 0 - always true;
            return true;
        }

        for (int i = inputString.length() - length; i >= 0; i--) {
            if (inputString.regionMatches(true, i, containsString, 0, length)) {
                return true;
            }
        }
        return false;
    }
}