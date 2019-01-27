package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.Ticket;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.UserTicketsPresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserTickets extends AppCompatActivity implements UserTicketsPresenter.View {

    private Activity thisActivity;
    private UserTicketsPresenter presenter = null;

    private LinearLayout ticketsContainer;
    private TextView userActiveTicketsMissingNotification;
    private TextView userHistoricTicketsMissingNotification;
    private Button backButton,showActiveButton, showHistoryButton;



    private boolean isQrCodeVisible = false;
    private View ticketNodeBeingRedeemed = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_tickets);
        setTitle("Twoje aktywne bilety");
        thisActivity = this;
        presenter = new UserTicketsPresenter(this);

        presenter.setUserTicketsActivityRef(thisActivity);

        ticketsContainer = findViewById(R.id.tickets_container);
        userActiveTicketsMissingNotification = findViewById(R.id.no_active_tickets);
        userHistoricTicketsMissingNotification = findViewById(R.id.no_historic_tickets);

        backButton = findViewById(R.id.back_button);
        showHistoryButton = findViewById(R.id.history_button);
        showActiveButton = findViewById(R.id.active_button);

        presenter.loadActiveUserTickets();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMenu();
            }
        });

        showActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Twoje aktywne bilety");
                presenter.loadActiveUserTickets();
                showHistoricTicketsButton();
                hideActiveTicketsButton();
            }
        });

        showHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTitle("Twoje wykorzystane bilety");
                presenter.loadUserTicketsHistory();
                showActiveTicketsButton();
                hideHistoricTicketsButton();
            }
        });

    }


    public void addActiveTicketToContainer(final Ticket ticket){
        LayoutInflater inflater = getLayoutInflater();
        final View ticketEntryNode = inflater.inflate(R.layout.ticket_show_node,ticketsContainer, false);

        final Button redeemTicket = ticketEntryNode.findViewById(R.id.redeem_ticket);
        LinearLayout redeemTicketMenu = ticketEntryNode.findViewById(R.id.active_ticket_menu);
        final LinearLayout qrCodeDisplayMenu = ticketEntryNode.findViewById(R.id.ticket_redeem_code);
        final ImageView qrCode = ticketEntryNode.findViewById(R.id.ticket_qr_code);
        TextView movieTime = ticketEntryNode.findViewById(R.id.movie_time);
        TextView movieTitle = ticketEntryNode.findViewById(R.id.movie_title);
        TextView movieTechnology = ticketEntryNode.findViewById(R.id.movie_screening_technology);
        TextView movieSeat = ticketEntryNode.findViewById(R.id.seat_designation);
        TextView movieDate = ticketEntryNode.findViewById(R.id.movie_date);
        TextView movieScreeningRoomNumber = ticketEntryNode.findViewById(R.id.movie_screening_room);
        TextView movieDiscountType = ticketEntryNode.findViewById(R.id.ticket_discount);

        try {
            Bitmap bitmap = presenter.encodeAsBitmap(ticket.getTicketDbUrl());
            qrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        redeemTicketMenu.setVisibility(View.VISIBLE);
        movieTime.setText(ticket.getMovieStartTime());
        movieDate.setText(ticket.getMovieStartDate());
        movieTitle.setText(ticket.getMovieTitle());
        movieTechnology.setText(EnumHandler.parseScreeningTechnology(thisActivity,ticket.getMovieTechnology()));
        movieSeat.setText(ticket.getSeatRow()+ticket.getSeatCollumn());
        movieScreeningRoomNumber.setText(String.valueOf(ticket.getScreeningRoomNumber()));
        movieDiscountType.setText(ticket.getDiscountType());

        redeemTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isQrCodeVisible) {
                    isQrCodeVisible = true;
                    ticketNodeBeingRedeemed = ticketEntryNode;
                    qrCodeDisplayMenu.setVisibility(View.VISIBLE);
                    presenter.listenForTicketRedeemAction(ticket);
                }
                else {
                    presenter.interruptRedeemListener();
                    qrCodeDisplayMenu.setVisibility(View.GONE);
                    isQrCodeVisible = false;
                }
            }
        });

        ticketsContainer.addView(ticketEntryNode);

    }

    public void addHistoricTicketToContainer(final Ticket ticket){
        LayoutInflater inflater = getLayoutInflater();
        final android.view.View ticketEntryNode = inflater.inflate(R.layout.ticket_show_node,ticketsContainer, false);

        TextView ticketRedeemedDate = ticketEntryNode.findViewById(R.id.redeemed_ticket_info);
        LinearLayout ticketRedeemedMenu = ticketEntryNode.findViewById(R.id.redeemed_ticket_menu);
        TextView movieTime = ticketEntryNode.findViewById(R.id.movie_time);
        TextView movieTitle = ticketEntryNode.findViewById(R.id.movie_title);
        TextView movieTechnology = ticketEntryNode.findViewById(R.id.movie_screening_technology);
        TextView movieSeat = ticketEntryNode.findViewById(R.id.seat_designation);
        TextView movieDate = ticketEntryNode.findViewById(R.id.movie_date);
        TextView movieScreeningRoomNumber = ticketEntryNode.findViewById(R.id.movie_screening_room);
        TextView movieDiscountType = ticketEntryNode.findViewById(R.id.ticket_discount);

        ticketRedeemedMenu.setVisibility(View.VISIBLE);
        ticketRedeemedDate.setText(ticket.getTicketRedeemedDate());
        movieTime.setText(ticket.getMovieStartTime());
        movieDate.setText(ticket.getMovieStartDate());
        movieTitle.setText(ticket.getMovieTitle());
        movieTechnology.setText(EnumHandler.parseScreeningTechnology(thisActivity,ticket.getMovieTechnology()));
        movieSeat.setText(ticket.getSeatRow()+ticket.getSeatCollumn());
        movieScreeningRoomNumber.setText(String.valueOf(ticket.getScreeningRoomNumber()));
        movieDiscountType.setText(ticket.getDiscountType());

        ticketsContainer.addView(ticketEntryNode);
    }
    @Override
    public void reloadActiveTicketList(ArrayList<Ticket> ticketList){
        clearTicketContainer();
        if(ticketList.size()>0) {
            hideActiveTicketsMissingNotification();
            for (Ticket ticket : ticketList) {
                addActiveTicketToContainer(ticket);
            }
        }
        else
            showActiveTicketsMissingNotification();

    }

    @Override
    public void reloadHistoricTicketList(ArrayList<Ticket> ticketList){
        clearTicketContainer();
        if(ticketList.size()>0) {
            hideActiveTicketsMissingNotification();
            for (Ticket ticket : ticketList) {
                addHistoricTicketToContainer(ticket);
            }
        }
        else
            showActiveTicketsMissingNotification();

    }
    @Override
    public void setQrCodeVisible(boolean qrCodeVisible) {
        isQrCodeVisible = qrCodeVisible;
    }

    @Override
    public void removeRedeemedTicketFromContainer(){
        ticketsContainer.removeView(ticketNodeBeingRedeemed);
    }

    @Override
    public void navigateToMenu(){
        onBackPressed();
    }
    @Override
    public void hideHistoricTicketsButton(){
        showHistoryButton.setVisibility(View.GONE);
    }
    @Override
    public void showHistoricTicketsButton(){
        showHistoryButton.setVisibility(View.VISIBLE);
    }
    @Override
    public void hideActiveTicketsButton(){
        showActiveButton.setVisibility(View.GONE);
    }
    @Override
    public void showActiveTicketsButton(){
        showActiveButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(thisActivity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showActiveTicketsMissingNotification(){
        userActiveTicketsMissingNotification.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideActiveTicketsMissingNotification(){
        userActiveTicketsMissingNotification.setVisibility(View.GONE);
    }
    @Override
    public void showHistoricTicketsMissingNotification(){
        userHistoricTicketsMissingNotification.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideHistoricTicketsMissingNotification(){
        userHistoricTicketsMissingNotification.setVisibility(View.GONE);
    }

    @Override
    public void clearTicketContainer() {
    ticketsContainer.removeAllViews();
    }

    @Override
    public void removeActiveTicketFromContainer(View ticketNode){
        ticketsContainer.removeView(ticketNode);
    }




}
