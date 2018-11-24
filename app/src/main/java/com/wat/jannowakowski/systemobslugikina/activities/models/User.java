package com.wat.jannowakowski.systemobslugikina.activities.models;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.*;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class User {
    private DatabaseReference mDatabase;
    private DatabaseReference usersParentRef;
    private OnUserDataReload userDataReadyListener = null;
    private DatabaseReference userDbNodeRef;

    private int role;
    private String uid;
    private String name;
    private String surname;

    public DatabaseReference getUserDbNodeRef() {
        return userDbNodeRef;
    }

    public void setUserDbNodeRef(DatabaseReference userDbNodeRef) {
        this.userDbNodeRef = userDbNodeRef;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDiscountType() {
        return discountType;
    }

    public void setDiscountType(int discountType) {
        this.discountType = discountType;
    }

    public ArrayList<Ticket> getActiveTicketsList() {
        return activeTicketsList;
    }

    public void setActiveTicketsList(ArrayList<Ticket> activeTicketsList) {
        this.activeTicketsList = activeTicketsList;
    }

    private String email;
    private int discountType;
    private ArrayList<Ticket> activeTicketsList;


    public User(final FirebaseUser user) throws NullPointerException{

        activeTicketsList = new ArrayList<>();

        setUid(user.getUid());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersParentRef = mDatabase.child("Users");
        userDbNodeRef = usersParentRef.child(getUid());
        userDbNodeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!(dataSnapshot.exists())) {
                            createUserNode(getUid());
                        } else {

                            name = dataSnapshot.child("name").getValue().toString();
                            surname = dataSnapshot.child("surname").getValue().toString();
                            role = Integer.parseInt(dataSnapshot.child("role").getValue().toString());
                            discountType = Integer.parseInt(dataSnapshot.child("discountType").getValue().toString());
                            email = dataSnapshot.child("email").getValue().toString();

                            for(DataSnapshot ticket : dataSnapshot.child("Tickets").getChildren()){
                                if(!ticket.getKey().equalsIgnoreCase("placeholder")) {      //omijamy placeholder
                                    for (DataSnapshot ticketEntry : ticket.getChildren()) {
                                        activeTicketsList.add(new Ticket(new Movie(),
                                                Integer.parseInt(ticketEntry.child("discountType").getValue().toString()),
                                                ticketEntry.child("seatCollumn").getValue().toString(),
                                                ticketEntry.child("seatRow").getValue().toString()));
                                    }
                                }
                            }
                            if(role== CurrentAppSession.getCustomerLoginAttemptCode())
                            userDataReadyListener.onUserDataReloaded(CurrentAppSession.getCustomerLoginAttemptCode());
                            else
                                userDataReadyListener.onUserDataReloaded(CurrentAppSession.getStaffLoginAttemptCode());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }


                });
    }
    private void createUserNode(String uid) {

        usersParentRef.child(uid).child("name").setValue("");
        usersParentRef.child(uid).child("surname").setValue("");
        usersParentRef.child(uid).child("email").setValue("");
        usersParentRef.child(uid).child("role").setValue(0);
        usersParentRef.child(uid).child("discountType").setValue(0);
        usersParentRef.child(uid).child("Tickets").child("placeholder").setValue(0).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userDataReadyListener.onUserDataReloaded(CurrentAppSession.getSignInEventCode());
            }
        });

    }

    public void setUserDataLoadedListener(OnUserDataReload userDataReadyListener){
        this.userDataReadyListener = userDataReadyListener;
    }
}
