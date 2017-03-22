package com.jets.classes;

import java.io.Serializable;

/**
 * Created by toqae on 15/03/2017.
 */

public class Trip implements Serializable {


    private long tripDateTime;
    private int  tripType, tripStatus;
    private String tripTitle, tripNotes, tripStartLongLat, tripStartLocation, tripEndLongLat, tripEndLocation , tripId;

    public Trip() {

        this.tripId = "0";
        this.tripType = 0;
        this.tripStatus = 0;
        this.tripTitle = "Virtual";
        this.tripNotes = "NOTES";
        this.tripStartLongLat = "32.877540399999994;23.9705802";
        this.tripStartLocation = "Aswan Dam";
        this.tripEndLongLat = "32.3018661;31.265289299999992";
        this.tripEndLocation = "Port Said";
        this.tripDateTime = 7674567690L;
    }

    public Trip(String tripId, int tripType, int tripStatus, String tripTitle, String tripNotes, String tripStartLongLat, String tripStartLocation, String tripEndLongLat, String tripEndLocation, long tripDateTime) {
        this.tripId = tripId;
        this.tripType = tripType;
        this.tripStatus = tripStatus;
        this.tripTitle = tripTitle;
        this.tripNotes = tripNotes;
        this.tripStartLongLat = tripStartLongLat;
        this.tripStartLocation = tripStartLocation;
        this.tripEndLongLat = tripEndLongLat;
        this.tripEndLocation = tripEndLocation;
        this.tripDateTime = tripDateTime;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public int getTripType() {
        return tripType;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
    }

    public int getTripStatus() {
        return tripStatus;
    }

    public void setTripStatus(int tripStatus) {
        this.tripStatus = tripStatus;
    }

    public String getTripTitle() {
        return tripTitle;
    }

    public void setTripTitle(String tripTitle) {
        this.tripTitle = tripTitle;
    }

    public String getTripNotes() {
        return tripNotes;
    }

    public void setTripNotes(String tripNotes) {
        this.tripNotes = tripNotes;
    }

    public String getTripStartLongLat() {
        return tripStartLongLat;
    }

    public void setTripStartLongLat(String tripStartLongLat) {
        this.tripStartLongLat = tripStartLongLat;
    }

    public String getTripStartLocation() {
        return tripStartLocation;
    }

    public void setTripStartLocation(String tripStartLocation) {
        this.tripStartLocation = tripStartLocation;
    }

    public String getTripEndLongLat() {
        return tripEndLongLat;
    }

    public void setTripEndLongLat(String tripEndLongLat) {
        this.tripEndLongLat = tripEndLongLat;
    }

    public String getTripEndLocation() {
        return tripEndLocation;
    }

    public void setTripEndLocation(String tripEndLocation) {
        this.tripEndLocation = tripEndLocation;
    }

    public long getTripDateTime() {
        return tripDateTime;
    }

    public void setTripDateTime(long tripDateTime) {
        this.tripDateTime = tripDateTime;
    }

    @Override
    public String toString() {
        String str = tripId + "|" + tripTitle + "|" + tripStatus + "|" + tripType + "|" + tripStartLongLat + "|" + tripStartLocation +
                        "|" + tripEndLongLat + "|" + tripEndLocation + "|" + tripDateTime + "|" + tripNotes;

        return str;
    }
}

