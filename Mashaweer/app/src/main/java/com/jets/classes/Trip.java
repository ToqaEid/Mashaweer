package com.jets.classes;

import java.io.Serializable;

/**
 * Created by toqae on 15/03/2017.
 */

public class Trip implements Serializable {

    private int  tripType, tripStatus;
    private String tripTitle, tripNotes, tripStartLong, tripStartLat, tripEndLong, tripEndLAt, tripDateTime, tripId;

    public Trip() {

        this.tripId = "0";
        this.tripType = 0;
        this.tripStatus = 0;
        this.tripTitle = "Virtual";
        this.tripNotes = "NOTES";
        this.tripStartLong = "32.877540399999994;23.9705802";
        this.tripStartLat = "Aswan Dam";
        this.tripEndLong = "32.3018661;31.265289299999992";
        this.tripEndLAt = "Port Said";
        this.tripDateTime = "22/3/2017 6:30";
    }

    public Trip(String tripId, int tripType, int tripStatus, String tripTitle, String tripNotes, String tripStartLong, String tripStartLat, String tripEndLong, String tripEndLAt, String tripDateTime) {
        this.tripId = tripId;
        this.tripType = tripType;
        this.tripStatus = tripStatus;
        this.tripTitle = tripTitle;
        this.tripNotes = tripNotes;
        this.tripStartLong = tripStartLong;
        this.tripStartLat = tripStartLat;
        this.tripEndLong = tripEndLong;
        this.tripEndLAt = tripEndLAt;
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

    public String getTripStartLong() {
        return tripStartLong;
    }

    public void setTripStartLong(String tripStartLong) {
        this.tripStartLong = tripStartLong;
    }

    public String getTripStartLat() {
        return tripStartLat;
    }

    public void setTripStartLat(String tripStartLat) {
        this.tripStartLat = tripStartLat;
    }

    public String getTripEndLong() {
        return tripEndLong;
    }

    public void setTripEndLong(String tripEndLong) {
        this.tripEndLong = tripEndLong;
    }

    public String getTripEndLAt() {
        return tripEndLAt;
    }

    public void setTripEndLAt(String tripEndLAt) {
        this.tripEndLAt = tripEndLAt;
    }

    public String getTripDateTime() {
        return tripDateTime;
    }

    public void setTripDateTime(String tripDateTime) {
        this.tripDateTime = tripDateTime;
    }

    @Override
    public String toString() {
        String str = tripId + "|" + tripTitle + "|" + tripStatus + "|" + tripType + "|" + tripStartLong + "|" + tripStartLat +
                        "|" + tripEndLong + "|" + tripEndLAt + "|" + tripDateTime + "|" + tripNotes;

        return str;
    }
}

