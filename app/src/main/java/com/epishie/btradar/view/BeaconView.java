package com.epishie.btradar.view;

import com.epishie.btradar.model.Beacon;

import java.util.List;

public interface BeaconView {

    /**
     * Updates the beacons with new scan data.
     * @param beacons the list of beacons
     */
    void update(List<Beacon> beacons);
}
