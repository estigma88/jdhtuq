package co.edu.uniquindio.dhash.resource.checksum;

import co.edu.uniquindio.storage.resource.Resource;

/**
 * Calculate a checksum
 */
public interface ChecksumCalculator {
    /**
     * Calculate a string as a checksum
     *
     * @param resource resource
     * @return checksum
     */
    String calculate(Resource resource);
}
