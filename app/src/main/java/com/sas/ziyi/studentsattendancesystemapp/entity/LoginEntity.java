package com.sas.ziyi.studentsattendancesystemapp.entity;

public class LoginEntity {
    private String userID;
    private String userName;
    private String userPassword;

    public LoginEntity() {
        super();
    }





    public LoginEntity(String userID, String userName, String userPassword) {
        super();
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }





    public String getUserID() {
        return userID;
    }





    public void setUserID(String userID) {
        this.userID = userID;
    }





    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
