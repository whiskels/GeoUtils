package modules.process;

import java.nio.file.Path;
import java.util.List;

public abstract class Processor {
    private final Path input;

    public Processor(Path input) {
        this.input = input;
    }

    public Path getInput() {
        return input;
    }

    public <T> List<T> isEmpty(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        } else return list;
    }

    public boolean isEmpty(String line) {
        if (line == null || line.length() == 0) {
            return true;
        } return false;
    }
}
