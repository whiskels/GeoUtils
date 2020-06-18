package main.java.content;

/**
 * Well Object Type ENUM
 * Used to create corresponding well parameters
 */

public enum WellObjectType {
    HEADER {
        @Override
        public WellParameter getInstance(String name) {
            return new WellHeader(name);
        }
    },

    LOG {
        @Override
        public WellParameter getInstance(String name) {
            return new WellLog(name);
        }
    },

    PARAMETER {
        @Override
        public WellParameter getInstance(String name) {
            return new WellParameter(name);
        }
    };

    public abstract WellParameter getInstance(String name);
}
