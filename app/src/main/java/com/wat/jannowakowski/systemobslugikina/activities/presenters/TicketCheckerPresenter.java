package com.wat.jannowakowski.systemobslugikina.activities.presenters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class TicketCheckerPresenter {

    private View view;

    private DatabaseReference mDatabase;


    private Activity userTicketsActivityRef = null;


    public TicketCheckerPresenter(View v) {
        this.view = v;
        this.mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    public void checkTicket(String url){
        final DatabaseReference ticketDbRef = FirebaseDatabase.getInstance().getReferenceFromUrl(url);

        ticketDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    redeemTicket(ticketDbRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                view.showToastMsg("Błąd odczytu biletu z bazy");
            }
        });
    }


    private void redeemTicket(final DatabaseReference ticketToBeRedeemedRef) {
        ticketToBeRedeemedRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                view.showToastMsg("Bilet został skasowany!");
            }
        });
    }


    public interface View {

        void navigateToMenu();

        void startQrCodeScanner();

        void showToastMsg(String msg);


    }
}
