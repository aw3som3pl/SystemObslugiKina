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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wat.jannowakowski.systemobslugikina.R;
import com.wat.jannowakowski.systemobslugikina.activities.models.Screening;
import com.wat.jannowakowski.systemobslugikina.activities.presenters.CustomerMenuPresenter;
import com.wat.jannowakowski.systemobslugikina.adapters.RepertoirListAdapter;
import com.wat.jannowakowski.systemobslugikina.global.CurrentAppSession;

import java.util.ArrayList;

public class CustomerMenu extends AppCompatActivity implements CustomerMenuPresenter.View {

    private Activity thisActivity;

    private CustomerMenuPresenter presenter;
    private RelativeLayout loadingOverlayLayout;
    private Button showUserTicketsBtn, showRepertoirBtn;
    private RecyclerView currentScreeningsList;
    private RecyclerView.Adapter repertoireAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private RepertoirListAdapter.RecyclerViewClickListener mRepertoirListListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_menu);

        loadingOverlayLayout = findViewById(R.id.loading_in_progress);

        showUserTicketsBtn = findViewById(R.id.active_tickets);
        showRepertoirBtn = findViewById(R.id.browse_movies);
        currentScreeningsList = findViewById(R.id.current_screenings);

        currentScreeningsList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(thisActivity);
        currentScreeningsList.setLayoutManager(mLayoutManager);

        thisActivity = this;

        setTitle(CurrentAppSession.getINSTANCE().getUserFullName());

        presenter = new CustomerMenuPresenter(this);

        presenter.setCustomerMenuActivityRef(thisActivity);

        presenter.reloadCurrentRepertoire();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onDestroy(){
        super.onDestroy();

    }

    @Override
    public void navigateToLogin() {
        startActivity(new Intent(CustomerMenu.this, Login.class));
        finish();
    }

    @Override
    public void showLoadingIndicator() {
        loadingOverlayLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingIndicator() {
        loadingOverlayLayout.setVisibility(View.GONE);
    }


    @Override
    public void setScreeningsRecyclerViewAdapter(ArrayList<Screening> screeningsList){

        mRepertoirListListener = new RepertoirListAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(CustomerMenu.this, "Click "+ position, Toast.LENGTH_LONG).show();
            }
        };

        repertoireAdapter = new RepertoirListAdapter(screeningsList,mRepertoirListListener);
        currentScreeningsList.setAdapter(repertoireAdapter);

        hideLoadingIndicator();



    }

}
