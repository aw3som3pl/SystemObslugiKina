package com.wat.jannowakowski.systemobslugikina.activities.models;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.*;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;

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
    private OnUserRegistered newUserRegisteredListener = null;
    private DatabaseReference userDbNodeRef;
    private FirebaseAuth userAuth;
    private int role;
    private String uid;
    private String name;
    private String surname;
    private String email;
    private String password;
    private int discountType;

    private ArrayList<Ticket> activeTicketsList;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FirebaseAuth getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(FirebaseAuth userAuth) {
        this.userAuth = userAuth;
    }

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


    public User(final FirebaseUser user) throws NullPointerException{

        activeTicketsList = new ArrayList<>();

        setUid(user.getUid());
        setUserAuth(CurrentAppSession.getINSTANCE().getCurrentUserAuth());

        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersParentRef = mDatabase.child("Users");
        userDbNodeRef = usersParentRef.child(getUid());

        userDbNodeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {   //dane logowania poprawne

                            name = dataSnapshot.child("name").getValue().toString();
                            surname = dataSnapshot.child("surname").getValue().toString();
                            role = Integer.parseInt(dataSnapshot.child("role").getValue().toString());
                            discountType = Integer.parseInt(dataSnapshot.child("discountType").getValue().toString());
                            email = dataSnapshot.child("email").getValue().toString();

                            for(DataSnapshot ticket : dataSnapshot.child("Tickets").getChildren()){
                                    for (DataSnapshot ticketEntry : ticket.getChildren()) {
                                        activeTicketsList.add(new Ticket(ticketEntry.child("movieDbRef").getValue().toString(),
                                                Integer.parseInt(ticketEntry.child("discountType").getValue().toString()),
                                                ticketEntry.child("seatCollumn").getValue().toString(),
                                                ticketEntry.child("seatRow").getValue().toString()));
                                }
                            }
                            if(role== CurrentAppSession.getCustomerLoginCode())      //rozróżnienie roli użytkownika w systemie
                            userDataReadyListener.onUserDataReloaded(CurrentAppSession.getCustomerLoginCode());
                            else
                                userDataReadyListener.onUserDataReloaded(CurrentAppSession.getStaffLoginCode());
                        }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }


                });
    }

    public User(FirebaseAuth temp_auth,String temp_name, String temp_surname, String temp_email, int temp_discountType, int temp_role){

        mDatabase = FirebaseDatabase.getInstance().getReference();
        usersParentRef = mDatabase.child("Users");

        this.name = temp_name;
        this.surname = temp_surname;
        this.email = temp_email;
        this.discountType = temp_discountType;
        this.role = temp_role;
        this.userAuth = temp_auth;
        this.uid = temp_auth.getUid();

        createUserNode(name,surname,email,discountType,role,uid);
    }



    private void createUserNode(String temp_name, String temp_surname, String temp_email, int temp_discountType, int temp_role, String temp_uid) {

        usersParentRef.child(temp_uid).child("name").setValue(temp_name);
        usersParentRef.child(temp_uid).child("surname").setValue(temp_surname);
        usersParentRef.child(temp_uid).child("email").setValue(temp_email);
        usersParentRef.child(temp_uid).child("role").setValue(temp_discountType);
        usersParentRef.child(temp_uid).child("discountType").setValue(temp_role).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                newUserRegisteredListener.onNewUserRegistered(true);
            }
        });

    }


    public void setUserDataLoadedListener(OnUserDataReload userDataReadyListener){
        this.userDataReadyListener = userDataReadyListener;
    }

    public void setNewUserRegisteredListener(OnUserRegistered userRegisteredListener){
        this.newUserRegisteredListener = userRegisteredListener;
    }
}
