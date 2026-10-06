package com.parkingservice.parking.infrastructure;

/**
 * Signals that an external parking provider could not return usable data.
 */
public class UpstreamServiceException extends RuntimeException {

    /**
     * Creates an upstream failure without a lower-level cause.
     *
     * @param message internal diagnostic message
     */
    public UpstreamServiceException(String message) {
        super(message);
    }

    /**
     * Creates an upstream failure while preserving its lower-level cause.
     *
     * @param message internal diagnostic message
     * @param cause underlying client or deserialization failure
     */
    public UpstreamServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
