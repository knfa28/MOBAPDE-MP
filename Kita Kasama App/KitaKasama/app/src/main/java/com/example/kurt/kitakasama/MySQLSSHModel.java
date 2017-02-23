package com.example.kurt.kitakasama;


import com.google.android.gms.maps.model.LatLng;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLSSHModel {
    public static void startSession(int sessionId, LocalUser user, ArrayList<Integer> trackerIds, ArrayList<LocalTracker> trackers, LatLng loc){
        MySQLSSHConnector.executeStatement("INSERT INTO kita_kasama.session values(" +
                                            sessionId + ", '" +
                                            user.getUserName() + "', '" +
                                            user.getUserContact() + "', 'Running'," +
                                            loc.longitude + "," +
                                            loc.latitude + ");");

        for(int i = 0; i < trackerIds.size(); i++) {
            MySQLSSHConnector.executeStatement("INSERT INTO kita_kasama.session_has_trackers values(" +
                                                sessionId + ", " +
                                                trackerIds.get(i) + ", '" +
                                                trackers.get(i).getTrackerContact() + "', 'Disconnnected', 'N/A');");
        }
    }

    public static Session getSession(int sessionId){
        //MySQLSSHConnector.getInstance().getConnection();
        Session session = null;
        try {
            ResultSet rsList = MySQLSSHConnector.executeQuery("SELECT * FROM kita_kasama.session WHERE session_code = " + sessionId + ";");
            if(rsList.next()) {
                session = new Session(rsList.getInt("session_code"),
                        rsList.getString("session_user"),
                        rsList.getString("session_contact"),
                        rsList.getString("session_status"),
                        rsList.getDouble("session_longitude"),
                        rsList.getDouble("session_latitude"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLSSHModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return session;
    }

    public static void updateSessionStatus(int sessionId, String status){
        //MySQLSSHConnector.getInstance().getConnection();
        MySQLSSHConnector.executeStatement("UPDATE kita_kasama.session " +
                                            "SET session_status = '" + status + "' " +
                                            "WHERE session_code = " + sessionId + ";");
    }

    public static void updateSessionLocation(int sessionId, LatLng loc){
        //MySQLSSHConnector.getInstance().getConnection();
        MySQLSSHConnector.executeStatement("UPDATE kita_kasama.session " +
                "SET session_longitude = " + loc.longitude + ", " +
                "session_latitude = " + loc.latitude +
                "WHERE session_code = " + sessionId + ";");
    }

    public static LatLng getSessionLocation(int sessionId,  int trackerId){
        //MySQLSSHConnector.getInstance().getConnection();
        LatLng loc = null;
        try {
            ResultSet rsList = MySQLSSHConnector.executeQuery("SELECT S.session_longitude, S.session_latitude " +
                                                                "FROM kita_kasama.session S, kita_kasama.session_has_trackers T " +
                                                                "WHERE S.session_code = " + sessionId + " " +
                                                                "AND S.session_code = T.session_code " +
                                                                "AND T.tracker_code = " + trackerId + ";");
            if(rsList.next()) {
                loc = new LatLng(rsList.getDouble("session_longitude"), rsList.getDouble("session_latitude"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLSSHModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loc;
    }

    public static String getSessionStatus(int sessionId, int trackerId){
        //MySQLSSHConnector.getInstance().getConnection();
        String sessionStatus = null;
        try {
            ResultSet rsList = MySQLSSHConnector.executeQuery("SELECT S.session_status " +
                    "FROM kita_kasama.session S, kita_kasama.session_has_trackers T " +
                    "WHERE S.session_code = " + sessionId + " " +
                    "AND S.session_code = T.session_code " +
                    "AND T.tracker_code = " + trackerId + ";");
            if(rsList.next()) {
                sessionStatus = rsList.getString("session_status");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MySQLSSHModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sessionStatus;
    }

    public static void updateTrackerStatus(int sessionId, int trackerId, String status){
        //MySQLSSHConnector.getInstance().getConnection();
        MySQLSSHConnector.executeStatement("UPDATE kita_kasama.session_has_trackers " +
                "SET tracker_status = '" + status + "' " +
                "WHERE session_code = " + sessionId + " " +
                "AND tracker_code = " + trackerId + ";");
    }

    public static void updateTrackerMessage(int sessionId, int trackerId, String message){
        //MySQLSSHConnector.getInstance().getConnection();
        MySQLSSHConnector.executeStatement("UPDATE kita_kasama.session_has_trackers " +
                "SET tracker_message = '" + message + "' " +
                "WHERE session_code = " + sessionId + " " +
                "AND tracker_code = " + trackerId + ";");
    }
}
