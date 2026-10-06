package com.parkingservice.parking.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GeoPointTest {

    @Test
    void calculatesDistanceWithTheHaversineFormula() {
        double distance = new GeoPoint(0, 0).distanceTo(new GeoPoint(0, 1));

        assertThat(distance).isCloseTo(111_195, within(10));
    }

    @Test
    void rejectsCoordinatesOutsideEarthBounds() {
        assertThatThrownBy(() -> new GeoPoint(91, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
