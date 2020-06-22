package content.builder;

import content.Well;

/**
 * Well Builder is used to create well objects by various input parameters;
 */

public interface WellBuilder {
    void reset();
    void addHeaders();
    void addLogs();
    void addParameters();
    Well getResult();
}
