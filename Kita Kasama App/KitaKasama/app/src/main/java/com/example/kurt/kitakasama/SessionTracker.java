package com.example.kurt.kitakasama;

public class SessionTracker {
    private int sessionId;
    private int trackerId;
    private String trackerStatus;
    private String lastMessage;

    public SessionTracker(int sessionId, int trackerId, String trackerStatus, String lastMessage) {
        this.sessionId = sessionId;
        this.trackerId = trackerId;
        this.trackerStatus = trackerStatus;
        this.lastMessage = lastMessage;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(int trackerId) {
        this.trackerId = trackerId;
    }

    public String getTrackerStatus() {
        return trackerStatus;
    }

    public void setTrackerStatus(String trackerStatus) {
        this.trackerStatus = trackerStatus;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}

