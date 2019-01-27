package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wat.jannowakowski.systemobslugikina.activities.models.Discount;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.Ticket;
import com.wat.jannowakowski.systemobslugikina.activities.models.User;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

import java.util.ArrayList;

/**
 * Created by Jan Nowakowski on 24.01.2019.
 */

public class BuyTicketsPresenter {

    private View view;

    private Activity buyTicketsActivityRef = null;

    private DatabaseReference mDatabase;

    private ArrayList<String> occupiedSeats;
    private ArrayList<Ticket> tempTicketList;

    private ChildEventListener seatEventsListener;
    private DatabaseReference screeningSeatsDbRef;

    public String chosenSeatRow;
    public String chosenSeatCol;

    public String tempTicketDiscountType = "Osoba dorosła";


    public BuyTicketsPresenter(View v){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        occupiedSeats = new ArrayList<>();
        tempTicketList = new ArrayList<>();
        this.view = v;

    }

    public void addTicket(Ticket ticket){
        screeningSeatsDbRef.child(ticket.getSeatRow()+ticket.getSeatCollumn()).setValue(CurrentAppSession.getINSTANCE().getCurrentUser().getUid());
        tempTicketList.add(ticket);
    }



    public boolean checkTicketAvailability(){
        return !occupiedSeats.contains(chosenSeatRow+chosenSeatCol);
    }

    public void cancelTempTicket(Ticket ticket){
        tempTicketList.remove(ticket);
        screeningSeatsDbRef.child(ticket.getSeatRow()+ticket.getSeatCollumn()).removeValue();
    }

    public void populateCustomerDiscountsOptions(RadioGroup radioGroup, ArrayList<Discount> discountsList){
        for(Discount discount : discountsList){
            RadioButton rbtn = new RadioButton(buyTicketsActivityRef);
            rbtn.setId(android.view.View.generateViewId());
            rbtn.setText(discount.getDiscountName());
            radioGroup.addView(rbtn);
            if(discount.getDiscountModifier()==1){  //docelowo zaznaczona opcja dla doroslych
                rbtn.setChecked(true);
            }
        }
    }


    public void saveBoughtTickets(){
        if(tempTicketList.size()>0) {
            User currentUser = CurrentAppSession.getINSTANCE().getCurrentUser();
            DatabaseReference curentUserTicketsRef = mDatabase.child("Users").child(currentUser.getUid()).child("Tickets").push();
            for (Ticket ticket : tempTicketList) {
                DatabaseReference ticketNodeRef = curentUserTicketsRef.push();
                ticketNodeRef.child("ticketDbUrl").setValue(ticketNodeRef.toString());
                ticketNodeRef.child("discountType").setValue(ticket.getDiscountType());
                ticketNodeRef.child("movieDbRef").setValue(ticket.getMovieDbRef());
                ticketNodeRef.child("movieStartDate").setValue(ticket.getMovieStartDate());
                ticketNodeRef.child("movieStartTime").setValue(ticket.getMovieStartTime());
                ticketNodeRef.child("movieTechnology").setValue(ticket.getMovieTechnology());
                ticketNodeRef.child("movieTitle").setValue(ticket.getMovieTitle());
                ticketNodeRef.child("screeningRoomNumber").setValue(ticket.getScreeningRoomNumber());
                ticketNodeRef.child("seatColumn").setValue(ticket.getSeatCollumn());
                ticketNodeRef.child("seatRow").setValue(ticket.getSeatRow());
                ticketNodeRef.child("ticketPrice").setValue(ticket.getTicketPrice());
            }
            tempTicketList.clear();
            view.clearTempTicketsContainer();
        }else
            view.showToastMsg("Brak biletów w koszyku");
    }



    public void startSeatWatcher(Screening screening){
        screeningSeatsDbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(screening.getScreeningDbRef()).child("seatsTaken");
        seatEventsListener = screeningSeatsDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.getValue().toString().equalsIgnoreCase("0")){
                    occupiedSeats.add(dataSnapshot.getKey());
                    view.checkSeatAvailability();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.getValue().toString().equalsIgnoreCase("0")){
                    occupiedSeats.remove(dataSnapshot.getKey());
                    view.checkSeatAvailability();
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void stopSeatWatcher(){
        screeningSeatsDbRef.removeEventListener(seatEventsListener);
        seatEventsListener = null;
    }

    public void clearTempTickets(){
        for(Ticket ticket : tempTicketList){
            cancelTempTicket(ticket);
        }
        view.preformBackAction();
    }

    public void setBuyTicketsActivityRef(Activity buyTicketsActivityRef) {
        this.buyTicketsActivityRef = buyTicketsActivityRef;
    }

    public interface View{
        void checkSeatAvailability();

        void clearTempTicketsContainer();

        void lockNewTicketButton();

        void unlockNewTicketButton();

        void showNewTicketForm();

        void hideNewTicketForm();

        void showTempTicketsList();

        void hideTempTicketsList();

        void showToastMsg(String msg);

        void navigateBack();

        void preformBackAction();
    }
}

