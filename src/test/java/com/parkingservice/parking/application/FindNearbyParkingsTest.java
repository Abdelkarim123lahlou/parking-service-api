package com.parkingservice.parking.application;

import com.parkingservice.parking.domain.GeoPoint;
import com.parkingservice.parking.domain.Parking;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindNearbyParkingsTest {

    @Test
    void filtersUnlocatedAndDistantParkingsThenSortsByDistance() {
        var near = parking("near", new GeoPoint(46.5805, 0.3400));
        var far = parking("far", new GeoPoint(46.6000, 0.3400));
        var unlocated = parking("unlocated", null);
        ParkingProvider provider = () -> List.of(far, unlocated, near);

        List<NearbyParking> result = new FindNearbyParkings(provider)
                .execute(new GeoPoint(46.58, 0.34), 1_000);

        assertThat(result).extracting(item -> item.parking().id())
                .containsExactly("near");
        assertThat(result.getFirst().distanceMeters()).isPositive();
    }

    private Parking parking(String id, GeoPoint location) {
        return new Parking(id, id, location, 100, 25, Instant.parse("2026-10-05T20:24:07Z"));
    }
}
