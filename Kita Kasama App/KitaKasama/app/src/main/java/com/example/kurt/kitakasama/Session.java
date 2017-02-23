package com.example.kurt.kitakasama;

public class Session {

    private int sessionId;
    private String sessionUser;
    private String sessionContact;
    private String sessionStatus;
    private Double sessionLongitude;
    private Double sessionLatitude;

    public Session(int sessionId, String sessionUser, String sessionContact, String sessionStatus, Double sessionLongitude, Double sessionLatitude) {
        this.sessionId = sessionId;
        this.sessionUser = sessionUser;
        this.sessionContact = sessionContact;
        this.sessionStatus = sessionStatus;
        this.sessionLongitude = sessionLongitude;
        this.sessionLatitude = sessionLatitude;
    }

    public Session(String sessionUser, String sessionContact) {
        this.sessionUser = sessionUser;
        this.sessionContact = sessionContact;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionUser() {
        return sessionUser;
    }

    public void setSessionUser(String sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String getSessionContact() {
        return sessionContact;
    }

    public void setSessionContact(String sessionContact) {
        this.sessionContact = sessionContact;
    }

    public String getSessionStatus() {
        return sessionStatus;
    }

    public void setSessionStatus(String sessionStatus) {
        this.sessionStatus = sessionStatus;
    }

    public Double getSessionLongitude() {
        return sessionLongitude;
    }

    public void setSessionLongitude(Double sessionLongitude) {
        this.sessionLongitude = sessionLongitude;
    }

    public Double getSessionLatitude() {
        return sessionLatitude;
    }

    public void setSessionLatitude(Double sessionLatitude) {
        this.sessionLatitude = sessionLatitude;
    }
}



