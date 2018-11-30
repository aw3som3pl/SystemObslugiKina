package com.wat.jannowakowski.systemobslugikina.activities.views;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.CustomerMenuPresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

import java.util.ArrayList;

public class CustomerMenu extends AppCompatActivity implements CustomerMenuPresenter.View {

    private Activity thisActivity;

    private CustomerMenuPresenter presenter;
    private Button showUserTicketsBtn,showRepertoirBtn;
    private RecyclerView currentScreeningsList;
    private RecyclerView.Adapter repertoireAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);

        thisActivity = this;

        setTitle(CurrentAppSession.getINSTANCE().getUserFullName());

        presenter = new CustomerMenuPresenter(this);

        presenter.setCustomerMenuActivityRef(thisActivity);

        presenter.reloadCurrentRepertoire();

        showUserTicketsBtn = findViewById(R.id.active_tickets);
        showRepertoirBtn = findViewById(R.id.browse_movies);
        currentScreeningsList = findViewById(R.id.current_screenings);

        currentScreeningsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(thisActivity);
        currentScreeningsList.setLayoutManager(mLayoutManager);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.settings:
                break;
            case R.id.contact:
                break;
            case R.id.logout:
                presenter.commenceUserLogout();
                break;
        }
        return true;
    }

    @Override
    public void navigateToLogin(){
        startActivity(new Intent(CustomerMenu.this, Login.class));
        finish();
    }
    @Override
    public void showLoadingIndicator(){}

    @Override
    public void hideLoadingIndicator(){}

    @Override
    public void setScreeningsRecyclerViewAdapter(ArrayList<Screening> screeningsList){

        repertoireAdapter = new RepertoirListAdapter(screeningsList);
        currentScreeningsList.setAdapter(repertoireAdapter);

    }

}
