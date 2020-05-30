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


public class FileProcessor extends Processor {
    private final String fileExtension;
    private boolean isValid, isFolder;

    // Create Logger
    public static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    public FileProcessor(String line, String fileExtension) {
        super(new File(line).toPath());
        this.fileExtension = fileExtension;
        logger.debug("Created fileProcessor\n\tInput string: {}\n\tExtension: {}",line,fileExtension);
        checkDataType();
    }

    private void checkDataType() {
        this.isValid = checkIfValid();
        this.isFolder = checkIfFolder();
        logger.debug("Checking input:\n\t- is valid? {}\n\t- is folder? {}",this.isValid,this.isFolder);
    }

    private boolean checkIfValid() {
        return this.getInput() != null && Files.exists(this.getInput());
    }

    private boolean checkIfFolder() {
        if (this.isValid) {
            return Files.isDirectory(this.getInput());
        } else return false;
    }

    public List<Path> processFiles() {
        List<Path> files = new ArrayList<>();
        if (this.isValid) {
            if (this.isFolder) {
                files = findFiles(this.fileExtension);
            } else {
                files.add(this.getInput());
            }
        }
        return isEmpty(files);
    }

    // Find files in given directory
    private List<Path> findFiles(String fileExtension) {
        logger.debug("Searching for files with {}", fileExtension);
        List<Path> foundFiles = null;
        if (isFolder) {
            try {
                // Recursively find all files with given extension
                foundFiles = Files.walk(this.getInput())
                        .filter(s -> containsIgnoreCase(s.toString(), fileExtension))
                        .map(Path::toAbsolutePath).sorted().collect(Collectors.toList());
            } catch (IOException e) {
                logger.error("\t{} {}",e,e.getMessage());
            }
        }
        if (foundFiles != null) {
            logger.debug("\tFound {} files.", foundFiles.size());
        } else {
            logger.error("\tError. No files found.");
        }
        return isEmpty(foundFiles);
    }


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

