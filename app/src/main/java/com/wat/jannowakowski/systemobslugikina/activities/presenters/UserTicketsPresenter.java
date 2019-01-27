package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.wat.jannowakowski.systemobslugikina.activities.models.Ticket;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnActiveTicketRedeem;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnActiveTicketsDataReload;
import com.wat.jannowakowski.systemobslugikina.interfaces.OnTicketsHistoryDataReload;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;


public class UserTicketsPresenter {

    public int WIDTH = 600;
    public int HEIGHT = 600;
    public int width = 600;

    private View view;

    private DatabaseReference mDatabase;

    private DatabaseReference userDbNodeRef;
    private DatabaseReference userActiveTicketsNodeRef;
    private DatabaseReference userTicketHistoryNodeRef;

    private OnActiveTicketsDataReload onActiveTicketsDataReloadListener = null;
    private OnTicketsHistoryDataReload onTicketsHistoryDataReloadListener = null;
    private OnActiveTicketRedeem onActiveTicketRedeemListener = null;

    private ChildEventListener ticketToBeRedeemedListener = null;
    private DatabaseReference ticketToBeRedeemedPath = null;


    private ArrayList<Ticket> activeTicketsList;
    private ArrayList<Ticket> historicTicketsList;



    private Activity userTicketsActivityRef = null;



    public UserTicketsPresenter(View v){
        this.view = v;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.activeTicketsList = new ArrayList<>();
        this.historicTicketsList = new ArrayList<>();
        this.userDbNodeRef = mDatabase.child("Users").child(CurrentAppSession.getINSTANCE().getCurrentUser().getUid()).getRef();
        this.userTicketHistoryNodeRef = userDbNodeRef.child("TicketsHistory").getRef();

        setOnActiveTicketsDataReloadedListener(new OnActiveTicketsDataReload() {
            @Override
            public void OnAllActiveTicketsDataReloaded(boolean state) {
                view.reloadActiveTicketList(activeTicketsList);
            }
        });

        setOnTicketsHistoryDataReloadedListener(new OnTicketsHistoryDataReload() {
            @Override
            public void OnAllTicketsHistoryDataReloaded(boolean state) {
                view.reloadHistoricTicketList(historicTicketsList);
            }
        });

    }

    public void loadActiveUserTickets(){

        this.activeTicketsList = new ArrayList<>();

        userActiveTicketsNodeRef = userDbNodeRef.child("Tickets").getRef();
                userActiveTicketsNodeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {   //dane logowania poprawne

                            for (DataSnapshot ticketGroup : dataSnapshot.getChildren()) {
                                for (DataSnapshot ticketEntry : ticketGroup.getChildren()) {
                                    activeTicketsList.add(new Ticket(ticketEntry.child("ticketDbUrl").getValue().toString(),
                                            ticketEntry.child("movieDbRef").getValue().toString(),
                                            ticketEntry.child("movieTitle").getValue().toString(),
                                            ticketEntry.child("movieStartDate").getValue().toString(),
                                            ticketEntry.child("movieStartTime").getValue().toString(),
                                            ticketEntry.child("discountType").getValue().toString(),
                                            Integer.parseInt(ticketEntry.child("movieTechnology").getValue().toString()),
                                            ticketEntry.child("seatColumn").getValue().toString(),
                                            ticketEntry.child("seatRow").getValue().toString(),
                                            Integer.parseInt(ticketEntry.child("screeningRoomNumber").getValue().toString()),
                                            ticketEntry.child("ticketPrice").getValue().toString()));
                                }
                            }
                        onActiveTicketsDataReloadListener.OnAllActiveTicketsDataReloaded(true);
                        }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }


                });
    }


    public void listenForTicketRedeemAction(final Ticket ticket) {

        onActiveTicketRedeemListener = null;

        setOnActiveTicketRedeemedListener(new OnActiveTicketRedeem() {
            @Override
            public void OnActiveTicketRedeemed(boolean state, Ticket ticket) {
                removeRedeemedTicketFromActiveTicketsList(ticket);
            }
        });

        ticketToBeRedeemedPath = FirebaseDatabase.getInstance().getReferenceFromUrl(ticket.getTicketDbUrl()).getParent();

        ticketToBeRedeemedListener = ticketToBeRedeemedPath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull final DataSnapshot dataSnapshot) {
                userTicketHistoryNodeRef.child(dataSnapshot.getKey()).setValue(dataSnapshot.getValue(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                        if (firebaseError != null) {
                            view.showToastMsg("Wystąpił błąd podczas kasowania biletu");
                        } else {
                            userTicketHistoryNodeRef.child(dataSnapshot.getKey()).child("ticketRedeemedDate").setValue(getCurrentDate());
                            onActiveTicketRedeemListener.OnActiveTicketRedeemed(true, ticket);
                        }
                    }
                });
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void removeRedeemedTicketFromActiveTicketsList(Ticket ticket) {
        onActiveTicketRedeemListener = null;
        view.removeRedeemedTicketFromContainer();
        activeTicketsList.remove(ticket);
        ticketToBeRedeemedPath.removeEventListener(ticketToBeRedeemedListener);
        view.setQrCodeVisible(false);
        view.showToastMsg("Bilet skasowany pomyślnie");
    }

    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, HEIGHT, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }




    public void loadUserTicketsHistory(){

        this.historicTicketsList = new ArrayList<>();

        userTicketHistoryNodeRef = userDbNodeRef.child("TicketsHistory").getRef();

        userTicketHistoryNodeRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {   //dane logowania poprawne

                            for (DataSnapshot ticketHistoryEntry : dataSnapshot.getChildren()) {
                                historicTicketsList.add(new Ticket(ticketHistoryEntry.child("ticketRedeemedDate").getValue().toString(),
                                        ticketHistoryEntry.child("ticketDbUrl").getValue().toString(),
                                        ticketHistoryEntry.child("movieDbRef").getValue().toString(),
                                        ticketHistoryEntry.child("movieTitle").getValue().toString(),
                                        ticketHistoryEntry.child("movieStartDate").getValue().toString(),
                                        ticketHistoryEntry.child("movieStartTime").getValue().toString(),
                                        ticketHistoryEntry.child("discountType").getValue().toString(),
                                        Integer.parseInt(ticketHistoryEntry.child("movieTechnology").getValue().toString()),
                                        ticketHistoryEntry.child("seatColumn").getValue().toString(),
                                        ticketHistoryEntry.child("seatRow").getValue().toString(),
                                        Integer.parseInt(ticketHistoryEntry.child("screeningRoomNumber").getValue().toString()),
                                        ticketHistoryEntry.child("ticketPrice").getValue().toString()));
                            }
                            onTicketsHistoryDataReloadListener.OnAllTicketsHistoryDataReloaded(true);
                        }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }


                });
    }

    private String getCurrentDate(){
        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyy HH:mm");
        return format.format(currentDate);
    }

    public void interruptRedeemListener() {
        ticketToBeRedeemedPath.removeEventListener(ticketToBeRedeemedListener);
    }

    public void setUserTicketsActivityRef(Activity userTicketsActivityRef) {
        this.userTicketsActivityRef = userTicketsActivityRef;
    }

    public void setOnActiveTicketsDataReloadedListener(OnActiveTicketsDataReload onActiveTicketsDataReloadListener){
        this.onActiveTicketsDataReloadListener = onActiveTicketsDataReloadListener;
    }

    public void setOnTicketsHistoryDataReloadedListener(OnTicketsHistoryDataReload onTicketsHistoryDataReloadListener){
        this.onTicketsHistoryDataReloadListener = onTicketsHistoryDataReloadListener;
    }

    public void setOnActiveTicketRedeemedListener(OnActiveTicketRedeem onActiveTicketRedeemedListener){
        this.onActiveTicketRedeemListener = onActiveTicketRedeemedListener;
    }



    public interface View{

        void reloadActiveTicketList(ArrayList<Ticket> ticketList);

        void reloadHistoricTicketList(ArrayList<Ticket> ticketList);

        void setQrCodeVisible(boolean qrCodeVisible);

        void removeRedeemedTicketFromContainer();

        void navigateToMenu();

        void hideHistoricTicketsButton();

        void showHistoricTicketsButton();

        void hideActiveTicketsButton();

        void showActiveTicketsButton();

        void showToastMsg(String msg);

        void showActiveTicketsMissingNotification();

        void hideActiveTicketsMissingNotification();

        void showHistoricTicketsMissingNotification();

        void hideHistoricTicketsMissingNotification();

        void clearTicketContainer();

        void removeActiveTicketFromContainer(android.view.View ticketNode);
    }
}
