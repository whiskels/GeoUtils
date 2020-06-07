package main.java.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File Processor class
 * Finds files with designated extension
 */

public class FileProcessor extends Processor {
    public static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    private boolean isValid, isFolder;

    public FileProcessor(String line) {
        super(new File(line).toPath());
        logger.debug("Created fileProcessor\n\tInput string: {}", line);
        checkDataType();
    }

    /** Check if parameters given in constructor are valid */
    private void checkDataType() {
        isValid = checkIfValid();
        isFolder = checkIfFolder();
        logger.debug("Checking input:\n\t- is valid? {}\n\t- is folder? {}", this.isValid, this.isFolder);
    }

    /** Check if input exists */
    private boolean checkIfValid() {
        return this.getInput() != null && Files.exists(this.getInput());
    }

    /** Check if input is folder */
    private boolean checkIfFolder() {
        if (isValid) {
            return Files.isDirectory(this.getInput());
        } else return false;
    }

    /**
     * Finds all matching files
     *
     * @return
     */
    public List<Path> processFiles(String fileExtension) {
        List<Path> files = new ArrayList<>();
        if (isValid) {
            if (isFolder) {
                files = findFiles(fileExtension); // If input is folder - find all files with fileExtension
            } else {
                if (containsIgnoreCase(this.getInput().toString(), fileExtension)) {
                    files.add(this.getInput()); // If input is a single file - add it to the list of found files
                }
            }
        }
        return isEmpty(files);
    }

    /** Finds all files in the directory with given file extension */
    private List<Path> findFiles(String fileExtension) {
        logger.debug("Searching for files with {}", fileExtension);
        List<Path> foundFiles = null;
        if (isFolder) {
            try {
                // Recursively find all files with given extension
                foundFiles = Files.walk(this.getInput())
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