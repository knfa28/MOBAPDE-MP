package com.example.kurt.kitakasama;

public class Tracker {
    private int trackerId;
    private String trackerName;
    private String trackerContact;

    public Tracker(int trackerId, String trackerName, String trackerContact) {
        this.trackerId = trackerId;
        this.trackerName = trackerName;
        this.trackerContact = trackerContact;
    }

    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }

    public String getTrackerName() {
        return trackerName;
    }

    public void setTrackerName(String trackerName) {
        this.trackerName = trackerName;
    }

    public String getTrackerContact() {
        return trackerContact;
    }

    public void setTrackerContact(String trackerContact) {
        this.trackerContact = trackerContact;
    }
}
