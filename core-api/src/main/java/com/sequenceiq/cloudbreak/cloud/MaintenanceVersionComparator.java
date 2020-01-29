package com.sequenceiq.cloudbreak.cloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This comparator supports only the three-part version number format like 1.2.3
 */
@Component
public class MaintenanceVersionComparator {

    public static final String SPLIT_PATTERN = "[.\\-]";

    private static final Logger LOGGER = LoggerFactory.getLogger(MaintenanceVersionComparator.class);

    public boolean isGreaterVersion(String version1, String version2) {
        try {
            if (version1 != null && version2 != null) {
                LOGGER.debug(String.format("Comparing %s and %s", version1, version2));
                return compareVersions(version1, version2);
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.warn("Comparison failed.", e);
            return false;
        }
    }

    private boolean equals(String v1, String v2) {
        return Integer.parseInt(v1) == Integer.parseInt(v2);
    }

    private boolean compareVersions(String version1, String version2) {
        String[] v1Parts = version1.split(SPLIT_PATTERN);
        String[] v2Parts = version2.split(SPLIT_PATTERN);
        return equals(v1Parts[0], v2Parts[0]) && equals(v1Parts[1], v2Parts[1]) && isMaintenanceVersionGreater(v1Parts[2], v2Parts[2]);
    }

    private boolean isMaintenanceVersionGreater(String v1, String v2) {
        return Integer.parseInt(v1) < Integer.parseInt(v2);
    }
}
