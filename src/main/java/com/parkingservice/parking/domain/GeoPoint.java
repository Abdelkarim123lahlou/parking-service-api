package com.parkingservice.parking.domain;

/**
 * Geographic position expressed with WGS84 coordinates.
 *
 * @param latitude latitude in decimal degrees, between -90 and 90
 * @param longitude longitude in decimal degrees, between -180 and 180
 */
public record GeoPoint(double latitude, double longitude) {

    /** Mean Earth radius used by the Haversine distance calculation. */
    private static final double EARTH_RADIUS_METERS = 6_371_000;

    /** Validates that the coordinates belong to the WGS84 coordinate ranges. */
    public GeoPoint {
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    /**
     * Calculates the great-circle distance to another position.
     *
     * @param other destination position
     * @return distance in meters
     */
    public double distanceTo(GeoPoint other) {
        double latitudeDelta = Math.toRadians(other.latitude - latitude);
        double longitudeDelta = Math.toRadians(other.longitude - longitude);
        double originLatitudeRadians = Math.toRadians(latitude);
        double destinationLatitudeRadians = Math.toRadians(other.latitude);

        double haversineTerm = Math.pow(Math.sin(latitudeDelta / 2), 2)
                + Math.cos(originLatitudeRadians) * Math.cos(destinationLatitudeRadians)
                * Math.pow(Math.sin(longitudeDelta / 2), 2);
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.sqrt(haversineTerm));
    }
}
