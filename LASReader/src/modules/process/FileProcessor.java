package modules.process;

import main.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FileProcessor extends Processor {
    private final String fileExtension;
    private boolean isProcessable, isFolder;

    public FileProcessor(String line, String fileExtension) {
        super(new File(line).toPath());
        this.fileExtension = fileExtension;
        checkDataType();
    }

    private void checkDataType() {
        this.isProcessable = checkIfProcessable();
        this.isFolder = checkIfFolder();
    }

    private boolean checkIfProcessable() {
        if (this.getInput() != null && Files.exists(this.getInput())) {
            return true;
        } else return false;
    }

    private boolean checkIfFolder() {
        if (isProcessable) {
            if (Files.isDirectory(this.getInput())) {
                return true;
            } else return false;
        } else return false;
    }

    public List<Path> processFiles() {
        List<Path> files = new ArrayList<>();
        if (isProcessable) {
            if (isFolder) {
                files = findFiles(this.fileExtension);
            } else {
                files.add(this.getInput());
            }
        }
        return isEmpty(files);
    }

    // Find files in given directory
    private List<Path> findFiles(String fileExtension) {
        Main.LOGGER.info("Searching for files with " + fileExtension);
        List<Path> foundFiles = null;
        if (isFolder) {
            try {
                // Recursively find all files with given extension
                foundFiles = Files.walk(this.getInput())
                        .filter(s -> containsIgnoreCase(s.toString(), fileExtension))
                        .map(Path::toAbsolutePath).sorted().collect(Collectors.toList());
            } catch (IOException e) {
                Main.LOGGER.severe("    " + e);
            }
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

