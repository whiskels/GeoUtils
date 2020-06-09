package main.java.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

public abstract class Processor {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Path input;

    public Processor(Path input) {
        this.input = input;
    }

    public final Path getInput() {
        return input;
    }

    /** Checks if input list is empty and if true - returns null */
    public final <T> List<T> isEmpty(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        } else return list;
    }

    public final boolean isEmpty(String line) {
        return line == null || line.length() == 0;
    }
}
