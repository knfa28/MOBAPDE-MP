package com.example.kurt.kitakasama;

public class LocalUser {
    public static final String TABLE_NAME = "user";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "name";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_NEUTRAL = "neutral_msg";
    public static final String COLUMN_NEGATIVE = "negative_msg";
    public static final String COLUMN_EXTREME = "extreme_msg";
    public static final String COLUMN_CONFIRM = "confirm_msg";
    public static final String COLUMN_CHECK = "check_msg";

    private int userId;
    private String userName;
    private String userContact;
    private String neutralMsg;
    private String negativeMsg;
    private String extremeMsg;
    private String confirmMsg;
    private String checkMsg;

    public LocalUser(String userName, String userContact) {
        this.userName = userName;
        this.userContact = userContact;
    }

    public LocalUser() {
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public String getNeutralMsg() {
        return neutralMsg;
    }

    public void setNeutralMsg(String neutralMsg) {
        this.neutralMsg = neutralMsg;
    }

    public String getNegativeMsg() {
        return negativeMsg;
    }

    public void setNegativeMsg(String negativeMsg) {
        this.negativeMsg = negativeMsg;
    }

    public String getExtremeMsg() {
        return extremeMsg;
    }

    public void setExtremeMsg(String extremeMsg) {
        this.extremeMsg = extremeMsg;
    }

    public String getConfirmMsg() {
        return confirmMsg;
    }

    public void setConfirmMsg(String confirmMsg) {
        this.confirmMsg = confirmMsg;
    }

    public String getCheckMsg() {
        return checkMsg;
    }

    public void setCheckMsg(String checkMsg) {
        this.checkMsg = checkMsg;
    }
}
