package com.example.kurt.kitakasama;

public class LocalTracker {
    public static final String TABLE_NAME = "tracker_contact";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TRACKER = "name";
    public static final String COLUMN_CONTACT = "contact";

    private int trackerId;
    private String trackerName;
    private String trackerContact;

    public LocalTracker(int trackerId, String trackerName, String trackerContact) {

        this.trackerName = trackerName;
        this.trackerContact = trackerContact;
    }

    public LocalTracker(String trackerName, String trackerContact) {
        this.trackerName = trackerName;
        this.trackerContact = trackerContact;
    }

    public LocalTracker() {

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
