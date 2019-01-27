package com.wat.jannowakowski.systemobslugikina.global;


import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wat.jannowakowski.systemobslugikina.activities.models.Discount;
import com.wat.jannowakowski.systemobslugikina.activities.models.User;

import java.util.ArrayList;

public class CurrentAppSession {

    private static CurrentAppSession INSTANCE = null;

    private User currentUser = null;
    private FirebaseAuth currentUserAuth = null;
    private DatabaseReference mDatabase;

    private DatabaseReference customerCathegoriesDbRef;
    private ChildEventListener customerCathegoriesNodeListener;

    private ArrayList<Discount> discountsCathegoriesList;


    private CurrentAppSession(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        discountsCathegoriesList = new ArrayList<>();
        populateDiscountCathegoriesList();
    }

    public static CurrentAppSession getINSTANCE(){
        if(INSTANCE == null){
            INSTANCE = new CurrentAppSession();
        }
        return(INSTANCE);
    }

    public void removeUserSession(){
        stopDiscountCathegoriesListener();
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

    public void populateDiscountCathegoriesList(){
        customerCathegoriesDbRef = mDatabase.child("CustomerCathegories").getRef();

        customerCathegoriesNodeListener = customerCathegoriesDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getChildrenCount()>1)
                discountsCathegoriesList.add(new Discount(Double.parseDouble(dataSnapshot.child("discountModifier").getValue().toString()),dataSnapshot.child("displayName").getValue().toString()));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void stopDiscountCathegoriesListener(){
        if(customerCathegoriesNodeListener!=null) {
            customerCathegoriesDbRef.removeEventListener(customerCathegoriesNodeListener);
            customerCathegoriesNodeListener = null;
        }
    }


    public ArrayList<Discount> getDiscountsCathegoriesList() {
        return discountsCathegoriesList;
    }

    public void setDiscountsCathegoriesList(ArrayList<Discount> discountsCathegoriesList) {
        this.discountsCathegoriesList = discountsCathegoriesList;
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
