package com.wat.jannowakowski.systemobslugikina.global;


import com.wat.jannowakowski.systemobslugikina.activities.models.User;

public class CurrentAppSession {

    private static CurrentAppSession INSTANCE = null;

    private User currentUser = null;


    private CurrentAppSession(){

    }

    public static CurrentAppSession getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new CurrentAppSession();
        }
        return(INSTANCE);
    }

    public static int getMaintenanceCode() {
        return 2;
    }

    public static int getUpdateRequiredCode() {
        return 1;
    }

    public static int getVersionUpToDateCode() {
        return 0;
    }

    public static int getSignInEventCode() {return 2;}

    public static int getStaffLoginAttemptCode() { return 1;}

    public static int getCustomerLoginAttemptCode() { return 0;}


    public static int getMaintenanceEnabledValue(){
        return 1;
    }

    public void setCurrentUser(User user){
        this.currentUser = user;
    }

    public User getCurrentUser(){
        return currentUser;
    }

}
