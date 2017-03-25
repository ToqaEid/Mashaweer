package com.jets.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by toqae on 15/03/2017.
 */

public class Trip implements Serializable {


    private long tripDateTime;
    private int  tripType, tripStatus;
    private String  tripPlaceId,  tripTitle, tripNotes, tripStartLongLat, tripStartLocation, tripEndLongLat, tripEndLocation , tripId;
    private ArrayList<String> tripCheckedNotes, tripUncheckedNotes;

    public Trip() {

    }

    public Trip(String tripId,  String placeId,  int tripType, int tripStatus, String tripTitle, String tripNotes, String tripStartLongLat, String tripStartLocation, String tripEndLongLat, String tripEndLocation, long tripDateTime) {
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

        this.tripPlaceId = placeId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }


    public String getTripPlaceId() {
        return tripPlaceId;
    }

    public void setTripPlaceId(String tripPlaceId) {
        this.tripPlaceId = tripPlaceId;
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

    public ArrayList<String> getTripCheckedNotes() {
        return tripCheckedNotes;
    }

    public void setTripCheckedNotes(ArrayList<String> tripCheckedNotes) {
        this.tripCheckedNotes = tripCheckedNotes;
    }

    public ArrayList<String> getTripUncheckedNotes() {
        return tripUncheckedNotes;
    }

    public void setTripUncheckedNotes(ArrayList<String> tripUncheckedNotes) {
        this.tripUncheckedNotes = tripUncheckedNotes;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripDateTime=" + tripDateTime +
                ", tripType=" + tripType +
                ", tripStatus=" + tripStatus +
                ", tripTitle='" + tripTitle + '\'' +
                ", tripNotes='" + tripNotes + '\'' +
                ", tripStartLongLat='" + tripStartLongLat + '\'' +
                ", tripStartLocation='" + tripStartLocation + '\'' +
                ", tripEndLongLat='" + tripEndLongLat + '\'' +
                ", tripEndLocation='" + tripEndLocation + '\'' +
                ", tripId='" + tripId + '\'' +
                ", tripCheckedNotes=" + tripCheckedNotes +
                ", tripUncheckedNotes=" + tripUncheckedNotes +
                '}';
    }
}

