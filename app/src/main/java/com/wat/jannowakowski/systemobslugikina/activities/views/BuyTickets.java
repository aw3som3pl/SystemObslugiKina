package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.abstractClasses.EnumHandler;
import com.wat.jannowakowski.systemobslugikina.activities.models.Discount;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.models.Ticket;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.BuyTicketsPresenter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

import org.w3c.dom.Text;

public class BuyTickets extends AppCompatActivity implements BuyTicketsPresenter.View {

    private Activity thisActivity;
    private BuyTicketsPresenter presenter;

    public Screening thisScreening;

    private ImageView premiereIcon;

    private TextView movieTitle;
    private TextView movieTechnology;
    private TextView movieAgeRating;
    private TextView movieScreeningDate;
    private TextView movieScreeningTime;
    private TextView movieDuration;


    private Spinner seatRowSpinner;
    private Spinner seatColSpinner;

    private Button reserveTicketButton;
    private TextView reserveTicketStatus;

    private LinearLayout ticketAvailableForm;
    private RadioGroup discountRadioGroup;
    private TextView ticketPrice;
    private Button addTempTicket;
    private Button closeTicketAvailableForm;

    private LinearLayout tempTicketsLayout;
    private LinearLayout tempTicketsContainer;

    private Button confirmAndBuy;
    private Button backBtn;

    private String tempTicketDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_tickets);

        Intent i = getIntent();
        thisScreening = (Screening) i.getSerializableExtra("Screening");

        thisActivity = this;
        presenter = new BuyTicketsPresenter(this);
        presenter.setBuyTicketsActivityRef(thisActivity);

        premiereIcon = findViewById(R.id.premiere_icon);
        movieTitle = findViewById(R.id.movie_title);
        movieTechnology = findViewById(R.id.movie_screening_technology);
        movieAgeRating = findViewById(R.id.movie_age_restriction);
        movieScreeningDate = findViewById(R.id.movie_screening_date);
        movieScreeningTime = findViewById(R.id.movie_screening_time);
        movieDuration = findViewById(R.id.movie_duration);

        seatRowSpinner = findViewById(R.id.row_spinner);
        seatColSpinner = findViewById(R.id.col_spinner);
        reserveTicketButton = findViewById(R.id.new_ticket);
        reserveTicketStatus = findViewById(R.id.new_ticket_message);
        ticketAvailableForm = findViewById(R.id.ticket_available);
        discountRadioGroup = findViewById(R.id.discount_radio_group);
        ticketPrice = findViewById(R.id.ticket_price);

        addTempTicket = findViewById(R.id.add_temp_ticket);
        closeTicketAvailableForm = findViewById(R.id.close_temp_ticket);
        tempTicketsLayout = findViewById(R.id.fill_tickets_layout);
        tempTicketsContainer = findViewById(R.id.tickets_container);
        confirmAndBuy = findViewById(R.id.buy_button);
        backBtn = findViewById(R.id.close_button);


        presenter.populateCustomerDiscountsOptions(discountRadioGroup, CurrentAppSession.getINSTANCE().getDiscountsCathegoriesList());

        if(thisScreening.isPremiere())
            premiereIcon.setVisibility(View.VISIBLE);

        movieTitle.setText(thisScreening.getMovie().getTitle());
        movieTechnology.setText(EnumHandler.parseScreeningTechnology(thisActivity,thisScreening.getMovie().getScreeningTechnology()));
        movieAgeRating.setText(EnumHandler.parseAgeRestriction(thisActivity,thisScreening.getMovie().getAgeRating()));
        movieScreeningDate.setText(thisScreening.getDateOfScreening());
        movieScreeningTime.setText(thisScreening.getTimeOfScreening());
        movieDuration.setText(String.valueOf(thisScreening.getMovie().getDuration())+ " min");

        ticketPrice.setText(String.valueOf(thisScreening.getBaseTicketPrice()));

        presenter.startSeatWatcher(thisScreening);

        if(presenter.checkTicketAvailability())
            unlockNewTicketButton();
        else {
            lockNewTicketButton();
            hideNewTicketForm();
        }

        confirmAndBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.saveBoughtTickets();
                hideNewTicketForm();
                hideTempTicketsList();
            }
        });

        addTempTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(presenter.checkTicketAvailability()) {
                    Ticket newTicket = new Ticket(thisScreening.getMovieDbRef(),
                            thisScreening.getMovie().getTitle(),
                            thisScreening.getDateOfScreening(),
                            thisScreening.getTimeOfScreening(),
                            presenter.tempTicketDiscountType,
                            thisScreening.getScreeningTechnology(),
                            presenter.chosenSeatCol,
                            presenter.chosenSeatRow,
                            thisScreening.getScreeningRoom().getReferenceNumber(),
                            ticketPrice.getText().toString());
                    presenter.addTicket(newTicket);
                    showTempTicketsList();
                    addTempTicketToContainer(newTicket);
                    hideNewTicketForm();
                } else {
                    hideNewTicketForm();
                    lockNewTicketButton();
                }
            }
        });

        closeTicketAvailableForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNewTicketForm();
                checkSeatAvailability();
            }
        });

        discountRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton tempRB = findViewById(checkedId);
                Discount discount = CurrentAppSession.getINSTANCE().getDiscountsCathegoriesList().get(discountRadioGroup.indexOfChild(tempRB));
                presenter.tempTicketDiscountType = discount.getDiscountName();
                ticketPrice.setText(String.format("%.2f",EnumHandler.calculateTicketPrice(thisScreening.getBaseTicketPrice(),discount.getDiscountModifier()))+" zł");
            }
        });

        seatRowSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.chosenSeatRow = EnumHandler.parseRowToString(position);
                checkSeatAvailability();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seatColSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                presenter.chosenSeatCol = String.valueOf(position+1);
                checkSeatAvailability();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        reserveTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewTicketForm();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.clearTempTickets();
                onBackPressed();
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();
        presenter.stopSeatWatcher();
    }


    public void addTempTicketToContainer(final Ticket ticket){
        LayoutInflater inflater = getLayoutInflater();
        final View ticketEntryNode = inflater.inflate(R.layout.ticket_show_node,tempTicketsContainer, false);

        Button remove_temp_ticket = ticketEntryNode.findViewById(R.id.remove_temp_ticket);
        TextView movieTime = ticketEntryNode.findViewById(R.id.movie_time);
        TextView movieTitle = ticketEntryNode.findViewById(R.id.movie_title);
        TextView movieTechnology = ticketEntryNode.findViewById(R.id.movie_screening_technology);
        TextView movieSeat = ticketEntryNode.findViewById(R.id.seat_designation);
        TextView movieDate = ticketEntryNode.findViewById(R.id.movie_date);
        TextView movieScreeningRoomNumber = ticketEntryNode.findViewById(R.id.movie_screening_room);
        TextView movieDiscountType = ticketEntryNode.findViewById(R.id.ticket_discount);

        remove_temp_ticket.setVisibility(View.VISIBLE);
        movieTime.setText(ticket.getMovieStartTime());
        movieDate.setText(ticket.getMovieStartDate());
        movieTitle.setText(ticket.getMovieTitle());
        movieTechnology.setText(EnumHandler.parseScreeningTechnology(thisActivity,ticket.getMovieTechnology()));
        movieSeat.setText(ticket.getSeatRow()+ticket.getSeatCollumn());
        movieScreeningRoomNumber.setText(String.valueOf(ticket.getScreeningRoomNumber()));
        movieDiscountType.setText(ticket.getDiscountType());

        tempTicketsContainer.addView(ticketEntryNode);

        remove_temp_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.cancelTempTicket(ticket);
                tempTicketsContainer.removeView(ticketEntryNode);
            }
        });
    }

    @Override
    public void checkSeatAvailability(){
        if(presenter.checkTicketAvailability())
            unlockNewTicketButton();
        else {
            lockNewTicketButton();
        }
    }

    @Override
    public void clearTempTicketsContainer(){
        tempTicketsContainer.removeAllViews();
    }

    @Override
    public void lockNewTicketButton(){
        reserveTicketButton.setEnabled(false);
        reserveTicketStatus.setText("Miejsce zajęte!");
    }

    @Override
    public void unlockNewTicketButton(){
        reserveTicketButton.setEnabled(true);
        reserveTicketStatus.setText("Miejsce wolne!");
    }

    @Override
    public void showNewTicketForm(){
        ticketAvailableForm.setVisibility(View.VISIBLE);
        reserveTicketButton.setEnabled(false);
    }

    @Override
    public void hideNewTicketForm(){
        ticketAvailableForm.setVisibility(View.GONE);
    }

    @Override
    public void showTempTicketsList(){
        tempTicketsLayout.setVisibility(View.VISIBLE);
        reserveTicketButton.setEnabled(false);
    }

    @Override
    public void hideTempTicketsList(){
        tempTicketsLayout.setVisibility(View.GONE);
    }

    @Override
    public void showToastMsg(String msg) {
        Toast.makeText(thisActivity, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateBack() {
        presenter.stopSeatWatcher();
        presenter.clearTempTickets();

    }
    @Override
    public void preformBackAction() {
        onBackPressed();
    }
}
