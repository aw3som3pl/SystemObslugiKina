package com.wat.jannowakowski.systemobslugikina.global;


import com.google.firebase.auth.FirebaseAuth;
import com.wat.jannowakowski.systemobslugikina.activities.models.User;

public class CurrentAppSession {

    private static CurrentAppSession INSTANCE = null;

    private User currentUser = null;
    private FirebaseAuth currentUserAuth = null;


    private CurrentAppSession(){

    }

    public static CurrentAppSession getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new CurrentAppSession();
        }
        return(INSTANCE);
    }

    public void removeUserSession(){
        this.currentUser = null;
        this.currentUserAuth = null;
        INSTANCE = null;
    }

    public FirebaseAuth getCurrentUserAuth() {
        return currentUserAuth;
    }

    public void setCurrentUserAuth(FirebaseAuth currentUserAuth) {
        this.currentUserAuth = currentUserAuth;
    }

    public String getUserFullName(){
        return currentUser.getName()+" "+currentUser.getSurname();
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

    public static int getStaffLoginCode() { return 1;}

    public static int getCustomerLoginCode() { return 0;}

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
